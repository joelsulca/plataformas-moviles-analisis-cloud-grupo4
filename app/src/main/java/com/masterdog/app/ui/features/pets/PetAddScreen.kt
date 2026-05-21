package com.masterdog.app.ui.features.pets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.masterdog.app.data.PetUi
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun PetAddScreen(
    navController: NavController,
    petsViewModel: PetsViewModel = viewModel()
) {
    var formState by remember { mutableStateOf(PetFormState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                title = "Nueva mascota",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
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
                            val newPet = PetUi(
                                id = "",  // backend asigna el id real
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
                            petsViewModel.addPet(newPet) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Mascota registrada correctamente")
                                }
                                navController.navigate(Screen.PetList.route) {
                                    popUpTo(Screen.PetList.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar mascota", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
