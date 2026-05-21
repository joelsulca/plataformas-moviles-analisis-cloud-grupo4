package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.data.ServiceCategory
import com.masterdog.app.data.ServiceUi
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.StepProgressIndicator

@Composable
fun AppointmentStep2Screen(
    navController: NavController,
    bookingViewModel: AppointmentBookingViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val services by bookingViewModel.services.collectAsState()

    val filtered = services.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true)
    }
    val medical = filtered.filter { it.category == ServiceCategory.MEDICAL }
    val aesthetic = filtered.filter { it.category == ServiceCategory.AESTHETIC }

    Scaffold(
        topBar = {
            TopBar(
                title = "Selecciona el servicio",
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
            StepProgressIndicator(currentStep = 2, totalSteps = 4)

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar servicio…") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (medical.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.MedicalServices, null,
                                tint = MaterialTheme.colorScheme.primary)
                            Text("Servicios Médicos", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                    items(medical) { svc ->
                        WizardServiceCard(
                            service = svc,
                            isSelected = bookingViewModel.selectedServiceId == svc.id,
                            onClick = { bookingViewModel.selectedServiceId = svc.id }
                        )
                    }
                }

                if (aesthetic.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.ContentCut, null,
                                tint = MaterialTheme.colorScheme.secondary)
                            Text("Estética y Baño", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                    items(aesthetic) { svc ->
                        WizardServiceCard(
                            service = svc,
                            isSelected = bookingViewModel.selectedServiceId == svc.id,
                            onClick = { bookingViewModel.selectedServiceId = svc.id }
                        )
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Screen.AppointmentStep3.route) },
                enabled = bookingViewModel.selectedServiceId != null,
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

@Composable
private fun WizardServiceCard(
    service: ServiceUi,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, style = MaterialTheme.typography.titleSmall)
                if (service.description.isNotBlank()) {
                    Text(service.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = service.priceRef,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
