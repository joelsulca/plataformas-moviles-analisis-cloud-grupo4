package com.masterdog.app.ui.features.appointments

import androidx.lifecycle.ViewModel
import com.masterdog.app.mock.AppointmentStatus
import com.masterdog.app.mock.AppointmentUi
import com.masterdog.app.mock.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppointmentsViewModel : ViewModel() {

    private val _appointments = MutableStateFlow(MockData.appointments.toMutableList())
    val appointments: StateFlow<List<AppointmentUi>> = _appointments.asStateFlow()

    fun cancelAppointment(id: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == id) it.copy(status = AppointmentStatus.CANCELLED) else it
        }.toMutableList()
    }

    fun confirmAppointment(id: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == id) it.copy(status = AppointmentStatus.CONFIRMED) else it
        }.toMutableList()
    }

    fun addAppointment(appointment: AppointmentUi) {
        _appointments.value = (_appointments.value + appointment).toMutableList()
    }

    fun appointmentById(id: String): AppointmentUi? = _appointments.value.find { it.id == id }
}
