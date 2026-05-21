package com.masterdog.app.ui.features.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterdog.app.data.ApiClient
import com.masterdog.app.data.CreateAppointmentRequest
import com.masterdog.app.data.Session
import com.masterdog.app.data.toUi
import com.masterdog.app.data.AppointmentUi
import com.masterdog.app.data.ServiceUi
import com.masterdog.app.data.VetUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AppointmentBookingViewModel : ViewModel() {

    private val api = ApiClient.api

    // ── Selección del wizard ────────────────────────────────────────────────
    var selectedPetId by mutableStateOf<String?>(null)
    var selectedServiceId by mutableStateOf<String?>(null)
    var selectedVetId by mutableStateOf<String?>(VET_ANY_ID)
    var selectedDate by mutableStateOf<String?>(null)
    var selectedTime by mutableStateOf<String?>(null)
    var reason by mutableStateOf("")
    var phone by mutableStateOf("")

    // ── Catálogos ───────────────────────────────────────────────────────────
    private val _services = MutableStateFlow<List<ServiceUi>>(emptyList())
    val services: StateFlow<List<ServiceUi>> = _services.asStateFlow()

    private val _vets = MutableStateFlow<List<VetUi>>(emptyList())
    val vets: StateFlow<List<VetUi>> = _vets.asStateFlow()

    private val _slots = MutableStateFlow<List<String>>(emptyList())
    val slots: StateFlow<List<String>> = _slots.asStateFlow()

    private val _loadingSlots = MutableStateFlow(false)
    val loadingSlots: StateFlow<Boolean> = _loadingSlots.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Mapa vetId real → primera hora disponible en la fecha, para resolver "vet-any". */
    private var availabilityByVet: Map<String, List<String>> = emptyMap()
    /** Caché de horarios semanales por vet (vetId → dayOfWeek → rangos). */
    private val scheduleCache = mutableMapOf<String, List<ScheduleRange>>()

    init {
        loadCatalogs()
        phone = Session.currentUser?.phone.orEmpty()
    }

    fun loadCatalogs() {
        viewModelScope.launch {
            try {
                val (svcResp, vetResp) = coroutineScope {
                    val s = async { api.listServices() }
                    val v = async { api.listVets() }
                    s.await() to v.await()
                }
                _services.value = svcResp.data.map { it.toUi() }
                val realVets = vetResp.data
                    .filter { it.active != false }
                    .map { it.toUi() }
                _vets.value = listOf(VetUi(VET_ANY_ID, "Cualquier veterinario", "")) + realVets
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar veterinarios o servicios"
            }
        }
    }

    /** Cierra el wizard: limpia selección y conserva los catálogos cargados. */
    fun reset() {
        selectedPetId = null
        selectedServiceId = null
        selectedVetId = VET_ANY_ID
        selectedDate = null
        selectedTime = null
        reason = ""
        phone = Session.currentUser?.phone.orEmpty()
        _slots.value = emptyList()
    }

    val availableDates: List<String> by lazy {
        val today = LocalDate.now()
        (0..6).map { today.plusDays(it.toLong()).toString() }
    }

    fun serviceById(id: String?): ServiceUi? =
        id?.let { sid -> _services.value.find { it.id == sid } }

    fun resolvedVetName(): String {
        val time = selectedTime ?: return "—"
        return if (selectedVetId == VET_ANY_ID) {
            // Primer vet real con ese slot disponible.
            availabilityByVet.entries
                .firstOrNull { (_, list) -> list.contains(time) }
                ?.let { (vetId, _) -> _vets.value.find { it.id == vetId }?.name }
                ?: "Veterinario disponible"
        } else {
            _vets.value.find { it.id == selectedVetId }?.name ?: "—"
        }
    }

    /** Devuelve el id del vet real que se enviará al backend al confirmar. */
    private fun resolvedVetId(): String? {
        val time = selectedTime ?: return null
        return if (selectedVetId == VET_ANY_ID) {
            availabilityByVet.entries
                .firstOrNull { (_, list) -> list.contains(time) }
                ?.key
        } else {
            selectedVetId
        }
    }

    /** Llama API para recalcular slots según vet+fecha seleccionados. */
    fun reloadSlots() {
        val vetSel = selectedVetId ?: return
        val date = selectedDate ?: return
        viewModelScope.launch {
            _loadingSlots.value = true
            try {
                val parsedDate = LocalDate.parse(date)
                val realVets = _vets.value.filter { it.id != VET_ANY_ID }
                val targetVets = if (vetSel == VET_ANY_ID) realVets
                else realVets.filter { it.id == vetSel }

                val byVet = coroutineScope {
                    targetVets.map { vet ->
                        async {
                            vet.id to computeSlotsFor(vet.id, parsedDate, date)
                        }
                    }.awaitAll().toMap()
                }
                availabilityByVet = byVet
                _slots.value = byVet.values.flatten().distinct().sorted()
            } catch (e: Exception) {
                _error.value = "No se pudo cargar la disponibilidad"
                _slots.value = emptyList()
            } finally {
                _loadingSlots.value = false
            }
        }
    }

    private suspend fun computeSlotsFor(
        vetId: String,
        parsedDate: LocalDate,
        dateStr: String
    ): List<String> {
        val vetIdLong = vetId.toLongOrNull() ?: return emptyList()
        val schedules = scheduleCache.getOrPut(vetId) {
            try {
                api.vetSchedules(vetIdLong).data.map {
                    ScheduleRange(it.dayOfWeek, it.startTime, it.endTime)
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
        val ranges = schedules.filter { it.dayOfWeek == parsedDate.dayOfWeek.value }
        if (ranges.isEmpty()) return emptyList()

        val occupied = try {
            api.busySlots(vetIdLong, dateStr).data.map { (it.time).take(5) }.toSet()
        } catch (e: Exception) {
            emptySet()
        }

        return ranges.flatMap { generateSlots(it.start, it.end) }
            .filter { it !in occupied }
            .distinct()
            .sorted()
    }

    /** Genera slots cada 30 min desde start hasta end (exclusivo). */
    private fun generateSlots(start: String, end: String): List<String> {
        val s = LocalTime.parse(start.take(5))
        val e = LocalTime.parse(end.take(5))
        val out = mutableListOf<String>()
        var cur = s
        while (cur.isBefore(e)) {
            out.add(cur.toString().take(5))
            cur = cur.plusMinutes(30)
        }
        return out
    }

    fun submitAppointment(onSuccess: (AppointmentUi) -> Unit) {
        val userId = Session.currentUserId
        val petId = selectedPetId?.toLongOrNull()
        val vetId = resolvedVetId()?.toLongOrNull()
        val serviceId = selectedServiceId?.toLongOrNull()
        val date = selectedDate
        val time = selectedTime
        if (userId == 0L || petId == null || vetId == null || serviceId == null ||
            date == null || time == null || reason.isBlank()
        ) {
            _error.value = "Faltan datos para confirmar la cita"
            return
        }
        viewModelScope.launch {
            try {
                val response = api.createAppointment(
                    CreateAppointmentRequest(
                        userId = userId,
                        petId = petId,
                        vetId = vetId,
                        serviceId = serviceId,
                        date = date,
                        time = time,
                        reason = reason
                    )
                )
                onSuccess(response.data.toUi())
            } catch (e: Exception) {
                _error.value = "No se pudo registrar la cita"
            }
        }
    }

    fun consumeError() { _error.value = null }

    private data class ScheduleRange(val dayOfWeek: Int, val start: String, val end: String)

    companion object {
        const val VET_ANY_ID = "vet-any"
    }
}
