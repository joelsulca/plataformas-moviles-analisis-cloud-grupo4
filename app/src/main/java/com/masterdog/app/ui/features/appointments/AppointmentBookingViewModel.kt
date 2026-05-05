package com.masterdog.app.ui.features.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.masterdog.app.mock.MockData

class AppointmentBookingViewModel : ViewModel() {
    var selectedPetId by mutableStateOf<String?>(null)
    var selectedServiceId by mutableStateOf<String?>(null)
    var selectedVetId by mutableStateOf<String?>("vet-any")
    var selectedDate by mutableStateOf<String?>(null)
    var selectedTime by mutableStateOf<String?>(null)
    var reason by mutableStateOf("")
    var phone by mutableStateOf(MockData.currentUser.phone)

    // Días disponibles: hoy + 6 días
    val availableDates: List<String> by lazy {
        val today = java.time.LocalDate.now()
        (0..6).map { today.plusDays(it.toLong()).toString() }
    }

    /** Slots disponibles según el vet y la fecha actualmente seleccionados. */
    fun currentSlots(): List<String> {
        val vetId = selectedVetId ?: return emptyList()
        val date = selectedDate ?: return emptyList()
        return MockData.slotsFor(vetId, date)
    }

    /**
     * Nombre del veterinario a mostrar en el resumen.
     * Si se eligió "vet-any", resuelve al primer vet disponible en esa fecha/hora.
     */
    fun resolvedVetName(): String {
        val date = selectedDate ?: return "—"
        val time = selectedTime ?: return "—"
        return if (selectedVetId == "vet-any") {
            MockData.resolveVet(date, time)
        } else {
            MockData.vets.find { it.id == selectedVetId }?.name ?: "—"
        }
    }

    fun reset() {
        selectedPetId = null
        selectedServiceId = null
        selectedVetId = "vet-any"
        selectedDate = null
        selectedTime = null
        reason = ""
        phone = MockData.currentUser.phone
    }
}
