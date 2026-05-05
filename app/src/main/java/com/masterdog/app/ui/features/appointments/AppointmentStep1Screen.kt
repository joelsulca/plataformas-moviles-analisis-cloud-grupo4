package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.masterdog.app.ui.features.pets.PetsViewModel
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.EmptyStateView
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.StepProgressIndicator

@Composable
fun AppointmentStep1Screen(
    navController: NavController,
    preselectedServiceId: String? = null,
    petsViewModel: PetsViewModel = viewModel(),
    bookingViewModel: AppointmentBookingViewModel = viewModel()
) {
    val pets by petsViewModel.pets.collectAsState()

    // Resetear wizard y preseleccionar servicio si viene de HU011
    LaunchedEffect(Unit) {
        bookingViewModel.reset()
        if (!preselectedServiceId.isNullOrBlank()) {
            bookingViewModel.selectedServiceId = preselectedServiceId
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Nueva cita",
                showClose = true,
                onClose = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StepProgressIndicator(currentStep = 1, totalSteps = 4)

            Text(
                text = "¿Para cuál mascota es la cita?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (pets.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Outlined.Pets,
                    message = "No tienes mascotas registradas",
                    actionLabel = "Agregar mascota",
                    onAction = { navController.navigate(Screen.PetAdd.route) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pets) { pet ->
                        val isSelected = bookingViewModel.selectedPetId == pet.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { bookingViewModel.selectedPetId = pet.id }
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
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column {
                                    Text(pet.name, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${pet.species} · ${pet.breed}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${pet.ageYears} años",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate(Screen.AppointmentStep2.route) },
                    enabled = bookingViewModel.selectedPetId != null,
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
}
