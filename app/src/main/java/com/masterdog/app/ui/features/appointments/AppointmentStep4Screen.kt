package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.ui.features.pets.PetsViewModel
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.StepProgressIndicator
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AppointmentStep4Screen(
    navController: NavController,
    petsViewModel: PetsViewModel = viewModel(),
    bookingViewModel: AppointmentBookingViewModel = viewModel(),
    appointmentsViewModel: AppointmentsViewModel = viewModel()
) {
    val pets by petsViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var reasonError by remember { mutableStateOf<String?>(null) }

    val pet = pets.find { it.id == bookingViewModel.selectedPetId }
    val service = bookingViewModel.serviceById(bookingViewModel.selectedServiceId)
    val dateStr = bookingViewModel.selectedDate ?: ""
    val timeStr = bookingViewModel.selectedTime ?: ""
    val resolvedVetName = bookingViewModel.resolvedVetName()

    val dayNumber = try { LocalDate.parse(dateStr).dayOfMonth.toString() } catch (e: Exception) { "--" }
    val monthShort = try {
        LocalDate.parse(dateStr)
            .format(DateTimeFormatter.ofPattern("MMM", Locale("es", "PE")))
            .replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { "" }

    Scaffold(
        topBar = {
            TopBar(
                title = "Confirmar cita",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            StepProgressIndicator(currentStep = 4, totalSteps = 4)
            Spacer(modifier = Modifier.height(8.dp))

            // Card resumen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Tag de fecha visual
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.width(56.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayNumber,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = monthShort,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service?.name ?: "Servicio no seleccionado",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SummaryRow(label = "Hora", value = timeStr)
                        SummaryRow(label = "Mascota", value = pet?.name ?: "—")
                        SummaryRow(label = "Veterinario", value = resolvedVetName)
                        service?.priceRef?.let { SummaryRow(label = "Precio", value = it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = bookingViewModel.reason,
                    onValueChange = { bookingViewModel.reason = it; reasonError = null },
                    label = { Text("Motivo de consulta *") },
                    isError = reasonError != null,
                    supportingText = { reasonError?.let { Text(it) } },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bookingViewModel.phone,
                    onValueChange = { bookingViewModel.phone = it },
                    label = { Text("Teléfono de contacto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (bookingViewModel.reason.isBlank()) {
                            reasonError = "El motivo es obligatorio"
                            return@Button
                        }
                        bookingViewModel.submitAppointment { newApt ->
                            appointmentsViewModel.addAppointment(newApt)
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Cita agendada exitosamente!")
                            }
                            navController.navigate(Screen.AppointmentList.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("ENVIAR SOLICITUD", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}
