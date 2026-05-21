package com.masterdog.app.ui.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterdog.app.data.ApiClient
import com.masterdog.app.data.toUi
import com.masterdog.app.data.AppointmentStatus
import com.masterdog.app.data.AppointmentUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentsViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _appointments = MutableStateFlow<List<AppointmentUi>>(emptyList())
    val appointments: StateFlow<List<AppointmentUi>> = _appointments.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Carga (o recarga) las citas de todas las mascotas del usuario. */
    fun loadForPets(petIds: List<String>) {
        if (petIds.isEmpty()) {
            _appointments.value = emptyList()
            return
        }
        viewModelScope.launch {
            _loading.value = true
            try {
                val merged = coroutineScope {
                    petIds.map { petId ->
                        async {
                            try {
                                val response = api.petAppointments(
                                    petId.toLongOrNull() ?: return@async emptyList()
                                )
                                response.data.map { it.toUi() }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                    }.awaitAll().flatten()
                }
                _appointments.value = merged.distinctBy { it.id }
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las citas"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelAppointment(id: String) {
        viewModelScope.launch {
            try {
                api.cancelAppointment(id.toLongOrNull() ?: return@launch)
                _appointments.value = _appointments.value.map {
                    if (it.id == id) it.copy(status = AppointmentStatus.CANCELLED) else it
                }
            } catch (e: Exception) {
                _error.value = "No se pudo cancelar la cita"
            }
        }
    }

    fun confirmAppointment(id: String) {
        viewModelScope.launch {
            try {
                api.confirmAppointment(id.toLongOrNull() ?: return@launch)
                _appointments.value = _appointments.value.map {
                    if (it.id == id) it.copy(status = AppointmentStatus.CONFIRMED) else it
                }
            } catch (e: Exception) {
                _error.value = "No se pudo confirmar la cita"
            }
        }
    }

    /** Agrega localmente una cita recién creada (la respuesta del POST /citas). */
    fun addAppointment(appointment: AppointmentUi) {
        _appointments.value = _appointments.value + appointment
    }

    fun appointmentById(id: String): AppointmentUi? =
        _appointments.value.find { it.id == id }

    fun consumeError() { _error.value = null }
}
