package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.mock.AppointmentStatus
import com.masterdog.app.ui.shared.components.ConfirmationDialog
import com.masterdog.app.ui.shared.components.TopBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AppointmentDetailScreen(
    navController: NavController,
    appointmentId: String,
    appointmentsViewModel: AppointmentsViewModel = viewModel()
) {
    val appointments by appointmentsViewModel.appointments.collectAsState()
    val appointment = appointments.find { it.id == appointmentId }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showCancelDialog by remember { mutableStateOf(false) }

    val isUpcoming = appointment?.status == AppointmentStatus.UPCOMING
    val isConfirmed = appointment?.status == AppointmentStatus.CONFIRMED

    val dayNumber = try {
        LocalDate.parse(appointment?.date ?: "").dayOfMonth.toString()
    } catch (e: Exception) { "--" }

    val monthShort = try {
        LocalDate.parse(appointment?.date ?: "")
            .format(DateTimeFormatter.ofPattern("MMM", Locale("es", "PE")))
            .replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { "" }

    val dateFormatted = try {
        LocalDate.parse(appointment?.date ?: "")
            .format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "PE")))
            .replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { appointment?.date ?: "" }

    if (showCancelDialog) {
        ConfirmationDialog(
            title = "Cancelar cita",
            message = "¿Cancelar esta cita? El horario quedará disponible para otros pacientes.",
            confirmText = "Sí, cancelar",
            onConfirm = {
                appointmentsViewModel.cancelAppointment(appointmentId)
                showCancelDialog = false
                scope.launch { snackbarHostState.showSnackbar("Cita cancelada correctamente") }
            },
            onDismiss = { showCancelDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Detalle de cita",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (appointment == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Cita no encontrada")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Banner recordatorio mock (solo citas próximas)
                if (isUpcoming || isConfirmed) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Recordatorio: tienes una cita el ${appointment.date} a las ${appointment.time}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Card principal
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Tag fecha visual
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.width(56.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(dayNumber, style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center)
                                    Text(monthShort, style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center)
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(appointment.serviceName, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow("Fecha", dateFormatted)
                                DetailRow("Hora", appointment.time)
                                DetailRow("Mascota", appointment.petName)
                                DetailRow("Veterinario", appointment.vetName)
                                DetailRow("Motivo", appointment.reason)
                                DetailRow("Estado", when (appointment.status) {
                                    AppointmentStatus.UPCOMING -> "Pendiente"
                                    AppointmentStatus.CONFIRMED -> "Confirmada"
                                    AppointmentStatus.PAST -> "Realizada"
                                    AppointmentStatus.CANCELLED -> "Cancelada"
                                })
                            }
                        }
                    }
                }

                // Botón confirmar asistencia (solo UPCOMING, no CONFIRMED)
                if (isUpcoming) {
                    item {
                        Button(
                            onClick = {
                                appointmentsViewModel.confirmAppointment(appointmentId)
                                scope.launch { snackbarHostState.showSnackbar("Asistencia confirmada") }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirmar asistencia", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // Chip "Confirmada" si ya se confirmó
                if (isConfirmed) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓ Asistencia confirmada",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Botón cancelar (solo UPCOMING)
                if (isUpcoming) {
                    item {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar cita", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f, fill = false),
            textAlign = TextAlign.End)
    }
}
