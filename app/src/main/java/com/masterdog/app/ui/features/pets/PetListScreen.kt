package com.masterdog.app.ui.features.pets

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.data.AppointmentStatus
import com.masterdog.app.data.PetUi
import com.masterdog.app.ui.features.appointments.AppointmentsViewModel
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.EmptyStateView
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.PetAvatarChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    navController: NavController,
    petsViewModel: PetsViewModel = viewModel(),
    appointmentsViewModel: AppointmentsViewModel = viewModel()
) {
    val pets by petsViewModel.pets.collectAsState()
    val appointments by appointmentsViewModel.appointments.collectAsState()
    var selectedPetId by remember { mutableStateOf<String?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Recarga mascotas cada vez que la pantalla es visible (incluido primer acceso post-login)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            petsViewModel.refresh()
            // Cuando pets se actualice, el LaunchedEffect de appointments en este mismo
            // screen disparará la carga de citas a través del collect de pets.
            petsViewModel.pets.collect { petList ->
                appointmentsViewModel.loadForPets(petList.map { it.id })
            }
        }
    }

    val selectedPet: PetUi? = pets.find { it.id == selectedPetId } ?: pets.firstOrNull()

    Scaffold(
        topBar = { TopBar(title = "Mis mascotas", colored = true) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.PetAdd.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar mascota")
            }
        }
    ) { innerPadding ->
        if (pets.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.Pets,
                message = "No tienes mascotas registradas",
                actionLabel = "Agregar mi primera mascota",
                onAction = { navController.navigate(Screen.PetAdd.route) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Selector horizontal de mascotas
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(pets) { pet ->
                        PetAvatarChip(
                            name = pet.name,
                            species = pet.species,
                            photoUrl = pet.photoUrl,
                            isSelected = selectedPet?.id == pet.id,
                            onClick = { selectedPetId = pet.id; selectedTabIndex = 0 }
                        )
                    }
                }

                selectedPet?.let { pet ->
                    // Tabs
                    val tabs = listOf("Perfil", "Visitas", "Documentos")
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> PetProfileTab(pet = pet, onEdit = {
                            navController.navigate(Screen.PetEdit.createRoute(pet.id))
                        })
                        1 -> {
                            val petAppointments = appointments.filter { it.petId == pet.id }
                            PetVisitsTab(appointments = petAppointments)
                        }
                        2 -> EmptyStateView(
                            icon = Icons.Outlined.Description,
                            message = "Sin documentos registrados",
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PetProfileTab(pet: PetUi, onEdit: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = pet.name, style = MaterialTheme.typography.titleLarge)
                        androidx.compose.material3.AssistChip(
                            onClick = onEdit,
                            label = { Text("Editar") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PetInfoRow(label = "Especie", value = pet.species)
                    PetInfoRow(label = "Raza", value = pet.breed)
                    PetInfoRow(label = "Género", value = pet.gender)
                    PetInfoRow(label = "Edad", value = "${pet.ageYears} años ${pet.ageMonths} meses")
                    PetInfoRow(label = "Peso", value = "${pet.weightKg} kg")
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Salud",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PetInfoRow(label = "Esterilización", value = pet.neuteredStatus)
                    PetInfoRow(label = "Tipo de sangre", value = pet.bloodType)
                    PetInfoRow(label = "Alergias", value = pet.allergies)
                }
            }
        }
    }
}

@Composable
private fun PetInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PetVisitsTab(appointments: List<com.masterdog.app.data.AppointmentUi>) {
    if (appointments.isEmpty()) {
        EmptyStateView(
            icon = Icons.Outlined.CalendarToday,
            message = "Sin visitas registradas",
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appointments) { apt ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(apt.serviceName, style = MaterialTheme.typography.titleSmall)
                        Text("${apt.date} — ${apt.time}", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(apt.vetName, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = apt.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (apt.status) {
                                AppointmentStatus.UPCOMING, AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                                AppointmentStatus.PAST -> MaterialTheme.colorScheme.onSurfaceVariant
                                AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }
        }
    }
}
