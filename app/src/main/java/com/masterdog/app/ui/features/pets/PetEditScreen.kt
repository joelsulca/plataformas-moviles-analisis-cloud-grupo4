package com.masterdog.app.ui.features.pets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.mock.PetUi
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.ConfirmationDialog
import com.masterdog.app.ui.shared.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun PetEditScreen(
    navController: NavController,
    petId: String,
    petsViewModel: PetsViewModel = viewModel()
) {
    val pet = petsViewModel.petById(petId)
    var formState by remember { mutableStateOf(PetFormState()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Precarga el formulario con los datos actuales
    LaunchedEffect(pet) {
        pet?.let {
            formState = PetFormState(
                name = it.name,
                gender = it.gender,
                species = it.species,
                breed = it.breed,
                ageYears = it.ageYears.toString(),
                ageMonths = it.ageMonths.toString(),
                weightKg = if (it.weightKg > 0) it.weightKg.toString() else "",
                allergies = it.allergies,
                neuteredStatus = it.neuteredStatus,
                bloodType = it.bloodType
            )
        }
    }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Eliminar mascota",
            message = "¿Eliminar a ${pet?.name}? Esta acción no se puede deshacer.",
            confirmText = "Sí, eliminar",
            onConfirm = {
                petsViewModel.deletePet(petId)
                showDeleteDialog = false
                navController.navigate(Screen.PetList.route) {
                    popUpTo(Screen.PetList.route) { inclusive = true }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Editar mascota",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (pet == null) {
            Text(
                "Mascota no encontrada",
                modifier = Modifier.padding(innerPadding).padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    PetFormContent(
                        state = formState,
                        onStateChange = { formState = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val validated = formState.validate()
                            formState = validated
                            if (validated.isValid()) {
                                val updated = PetUi(
                                    id = petId,
                                    name = validated.name,
                                    species = validated.species,
                                    breed = validated.breed,
                                    gender = validated.gender,
                                    ageYears = validated.ageYears.toIntOrNull() ?: 0,
                                    ageMonths = validated.ageMonths.toIntOrNull() ?: 0,
                                    weightKg = validated.weightKg.toFloatOrNull() ?: 0f,
                                    allergies = validated.allergies,
                                    neuteredStatus = validated.neuteredStatus,
                                    bloodType = validated.bloodType
                                )
                                petsViewModel.updatePet(updated)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Perfil actualizado")
                                }
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Guardar cambios", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Eliminar mascota", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
