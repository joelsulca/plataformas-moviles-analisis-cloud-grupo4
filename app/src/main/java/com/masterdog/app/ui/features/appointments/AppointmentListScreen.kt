package com.masterdog.app.ui.features.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.data.AppointmentStatus
import com.masterdog.app.data.AppointmentUi
import com.masterdog.app.ui.features.pets.PetsViewModel
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.ConfirmationDialog
import com.masterdog.app.ui.shared.components.EmptyStateView
import com.masterdog.app.ui.shared.components.TopBar
import com.masterdog.app.ui.shared.components.PetAvatarChip
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen(
    navController: NavController,
    appointmentsViewModel: AppointmentsViewModel = viewModel(),
    petsViewModel: PetsViewModel = viewModel()
) {
    val appointments by appointmentsViewModel.appointments.collectAsState()
    val pets by petsViewModel.pets.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Recarga mascotas y luego citas en cada RESUME (primer acceso post-login incluido)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            petsViewModel.refresh()
            petsViewModel.pets.collect { petList ->
                appointmentsViewModel.loadForPets(petList.map { it.id })
            }
        }
    }

    var selectedPetId by remember { mutableStateOf<String?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var appointmentToCancel by remember { mutableStateOf<AppointmentUi?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filteredByPet = if (selectedPetId == null) appointments
    else appointments.filter { it.petId == selectedPetId }

    val upcoming = filteredByPet.filter {
        it.status == AppointmentStatus.UPCOMING || it.status == AppointmentStatus.CONFIRMED
    }
    val past = filteredByPet.filter { it.status == AppointmentStatus.PAST }
    val cancelled = filteredByPet.filter { it.status == AppointmentStatus.CANCELLED }

    if (appointmentToCancel != null) {
        ConfirmationDialog(
            title = "Cancelar cita",
            message = "¿Cancelar esta cita? El horario quedará disponible para otros pacientes.",
            confirmText = "Sí, cancelar",
            onConfirm = {
                appointmentsViewModel.cancelAppointment(appointmentToCancel!!.id)
                appointmentToCancel = null
                scope.launch { snackbarHostState.showSnackbar("Cita cancelada correctamente") }
            },
            onDismiss = { appointmentToCancel = null }
        )
    }

    Scaffold(
        topBar = { TopBar(title = "Mis citas", colored = true) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AppointmentNew.createRoute()) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva cita")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Selector de mascota
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedPetId == null,
                        onClick = { selectedPetId = null },
                        label = { Text("TODAS") }
                    )
                }
                items(pets) { pet ->
                    PetAvatarChip(
                        name = pet.name,
                        species = pet.species,
                        isSelected = selectedPetId == pet.id,
                        onClick = { selectedPetId = pet.id }
                    )
                }
            }

            // Tabs
            val tabs = listOf("Próximas (${upcoming.size})", "Pasadas (${past.size})", "Canceladas (${cancelled.size})")
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTabIndex == idx,
                        onClick = { selectedTabIndex = idx },
                        text = { Text(title, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> AppointmentTabContent(
                    appointments = upcoming,
                    emptyMessage = "No tienes citas próximas",
                    emptyAction = "Agendar cita",
                    onEmptyAction = { navController.navigate(Screen.AppointmentNew.createRoute()) },
                    onCardClick = { navController.navigate(Screen.AppointmentDetail.createRoute(it.id)) },
                    showCancel = true,
                    onCancel = { appointmentToCancel = it }
                )
                1 -> AppointmentTabContent(
                    appointments = past,
                    emptyMessage = "No hay citas pasadas",
                    onCardClick = { navController.navigate(Screen.AppointmentDetail.createRoute(it.id)) }
                )
                2 -> AppointmentTabContent(
                    appointments = cancelled,
                    emptyMessage = "No hay citas canceladas",
                    onCardClick = { navController.navigate(Screen.AppointmentDetail.createRoute(it.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentTabContent(
    appointments: List<AppointmentUi>,
    emptyMessage: String,
    emptyAction: String? = null,
    onEmptyAction: (() -> Unit)? = null,
    onCardClick: (AppointmentUi) -> Unit,
    showCancel: Boolean = false,
    onCancel: ((AppointmentUi) -> Unit)? = null
) {
    if (appointments.isEmpty()) {
        EmptyStateView(
            icon = Icons.Outlined.CalendarToday,
            message = emptyMessage,
            actionLabel = emptyAction,
            onAction = onEmptyAction,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appointments) { apt ->
                AppointmentCard(
                    appointment = apt,
                    showCancel = showCancel && apt.status == AppointmentStatus.UPCOMING,
                    onClick = { onCardClick(apt) },
                    onCancel = { onCancel?.invoke(apt) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentCard(
    appointment: AppointmentUi,
    showCancel: Boolean,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    val dayNumber = try {
        java.time.LocalDate.parse(appointment.date).dayOfMonth.toString()
    } catch (e: Exception) { "--" }

    val monthShort = try {
        java.time.LocalDate.parse(appointment.date)
            .format(java.time.format.DateTimeFormatter.ofPattern("MMM", java.util.Locale("es", "PE")))
            .replaceFirstChar { it.uppercase() }
    } catch (e: Exception) { "" }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag de fecha
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.width(44.dp)
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(dayNumber, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Text(monthShort, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(appointment.serviceName, style = MaterialTheme.typography.titleSmall)
                Text(appointment.petName, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${appointment.time} · ${appointment.vetName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (appointment.status == AppointmentStatus.CONFIRMED) {
                    Text("✓ Confirmada", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
            if (showCancel) {
                OutlinedButton(
                    onClick = onCancel,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Cancelar", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
