package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.StepProgressIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppointmentStep3Screen(
    navController: NavController,
    bookingViewModel: AppointmentBookingViewModel = viewModel()
) {
    val vets by bookingViewModel.vets.collectAsState()
    val slots by bookingViewModel.slots.collectAsState()
    val loadingSlots by bookingViewModel.loadingSlots.collectAsState()

    // Recargar slots cuando cambian vet o fecha.
    LaunchedEffect(bookingViewModel.selectedVetId, bookingViewModel.selectedDate) {
        if (bookingViewModel.selectedVetId != null && bookingViewModel.selectedDate != null) {
            bookingViewModel.reloadSlots()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Veterinario y horario",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StepProgressIndicator(currentStep = 3, totalSteps = 4)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // ── VETERINARIO ───────────────────────────────────────────
                item {
                    Text(
                        "Veterinario",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(vets) { vet ->
                            val isSelected = bookingViewModel.selectedVetId == vet.id
                            Card(
                                modifier = Modifier
                                    .clickable {
                                        bookingViewModel.selectedVetId = vet.id
                                        bookingViewModel.selectedTime = null
                                    }
                                    .then(
                                        if (isSelected) Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(12.dp)
                                        ) else Modifier
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(vet.name, style = MaterialTheme.typography.labelLarge)
                                    if (vet.specialty.isNotBlank()) {
                                        Text(
                                            vet.specialty,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── FECHA ─────────────────────────────────────────────────
                item {
                    Text(
                        "Fecha",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(bookingViewModel.availableDates) { dateStr ->
                            val date = LocalDate.parse(dateStr)
                            val isSelected = bookingViewModel.selectedDate == dateStr
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    bookingViewModel.selectedDate = dateStr
                                    bookingViewModel.selectedTime = null
                                },
                                label = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            date.format(DateTimeFormatter.ofPattern("EEE", Locale("es", "PE")))
                                                .replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            "${date.dayOfMonth}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            date.format(DateTimeFormatter.ofPattern("MMM", Locale("es", "PE")))
                                                .replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── HORARIO — solo visible cuando vet + fecha están seleccionados ──
                item {
                    Text(
                        "Horario disponible",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    val vetId = bookingViewModel.selectedVetId
                    val date = bookingViewModel.selectedDate

                    when {
                        vetId == null || date == null -> {
                            Text(
                                "Selecciona un veterinario y una fecha para ver los horarios",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        loadingSlots -> {
                            Text(
                                "Cargando horarios disponibles…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        else -> {
                            if (slots.isEmpty()) {
                                Text(
                                    "No hay horarios disponibles para este día",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    slots.forEach { slot ->
                                        FilterChip(
                                            selected = bookingViewModel.selectedTime == slot,
                                            onClick = { bookingViewModel.selectedTime = slot },
                                            label = { Text(slot) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Screen.AppointmentStep4.route) },
                enabled = bookingViewModel.selectedVetId != null &&
                        bookingViewModel.selectedDate != null &&
                        bookingViewModel.selectedTime != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Continuar", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
