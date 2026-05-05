package com.masterdog.app.ui.features.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class PetFormState(
    val name: String = "",
    val gender: String = "Macho",
    val species: String = "Perro",
    val breed: String = "",
    val ageYears: String = "0",
    val ageMonths: String = "0",
    val weightKg: String = "",
    val allergies: String = "Ninguna",
    val neuteredStatus: String = "Desconocido",
    val bloodType: String = "No probado",
    val nameError: String? = null,
    val speciesError: String? = null
)

private val GENDERS = listOf("Macho", "Hembra")
private val SPECIES = listOf("Perro", "Gato", "Otro")
private val NEUTERED_STATUS = listOf("Desconocido", "Esterilizado", "No esterilizado")
private val BLOOD_TYPES = listOf("No probado", "DEA 1.1+", "DEA 1.1-", "DEA 1.2+", "DEA 1.2-", "A", "B", "AB")
private val COLOR_SWATCHES = listOf(
    Pair("Negro", Color(0xFF1A1A1A)),
    Pair("Blanco", Color(0xFFF5F5F5)),
    Pair("Marrón", Color(0xFF6D4C41)),
    Pair("Marrón claro", Color(0xFFBCAAA4)),
    Pair("Naranja", Color(0xFFFF8A65)),
    Pair("Gris", Color(0xFF9E9E9E))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormContent(
    state: PetFormState,
    onStateChange: (PetFormState) -> Unit
) {
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var genderExpanded by remember { mutableStateOf(false) }
    var speciesExpanded by remember { mutableStateOf(false) }
    var neuteredExpanded by remember { mutableStateOf(false) }
    var bloodExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Foto avatar mock
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                .align(Alignment.CenterHorizontally)
                .clickable { /* mock: no galería real */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Agregar foto",
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Nombre
        OutlinedTextField(
            value = state.name,
            onValueChange = { onStateChange(state.copy(name = it, nameError = null)) },
            label = { Text("Nombre *") },
            isError = state.nameError != null,
            supportingText = { state.nameError?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Género
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                GENDERS.forEach { g ->
                    DropdownMenuItem(
                        text = { Text(g) },
                        onClick = { onStateChange(state.copy(gender = g)); genderExpanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Especie
        ExposedDropdownMenuBox(
            expanded = speciesExpanded,
            onExpandedChange = { speciesExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.species,
                onValueChange = {},
                readOnly = true,
                label = { Text("Especie *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                isError = state.speciesError != null,
                supportingText = { state.speciesError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = speciesExpanded,
                onDismissRequest = { speciesExpanded = false }
            ) {
                SPECIES.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s) },
                        onClick = { onStateChange(state.copy(species = s, speciesError = null)); speciesExpanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Raza
        OutlinedTextField(
            value = state.breed,
            onValueChange = { onStateChange(state.copy(breed = it)) },
            label = { Text("Raza") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Colores
        Text("Color", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            COLOR_SWATCHES.forEach { (name, color) ->
                val isSelected = selectedColor == name
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { selectedColor = name }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Edad
        Text("Edad", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.ageYears,
                onValueChange = { onStateChange(state.copy(ageYears = it.filter { c -> c.isDigit() })) },
                label = { Text("Años") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.ageMonths,
                onValueChange = { onStateChange(state.copy(ageMonths = it.filter { c -> c.isDigit() })) },
                label = { Text("Meses") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Sección salud
        Text(
            text = "SALUD",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Esterilización
        ExposedDropdownMenuBox(
            expanded = neuteredExpanded,
            onExpandedChange = { neuteredExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.neuteredStatus,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado esterilización") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = neuteredExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = neuteredExpanded,
                onDismissRequest = { neuteredExpanded = false }
            ) {
                NEUTERED_STATUS.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s) },
                        onClick = { onStateChange(state.copy(neuteredStatus = s)); neuteredExpanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Peso
        OutlinedTextField(
            value = state.weightKg,
            onValueChange = { onStateChange(state.copy(weightKg = it)) },
            label = { Text("Peso") },
            suffix = { Text("kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tipo de sangre
        ExposedDropdownMenuBox(
            expanded = bloodExpanded,
            onExpandedChange = { bloodExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.bloodType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de sangre") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = bloodExpanded,
                onDismissRequest = { bloodExpanded = false }
            ) {
                BLOOD_TYPES.forEach { b ->
                    DropdownMenuItem(
                        text = { Text(b) },
                        onClick = { onStateChange(state.copy(bloodType = b)); bloodExpanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Alergias
        OutlinedTextField(
            value = state.allergies,
            onValueChange = { onStateChange(state.copy(allergies = it)) },
            label = { Text("Alergias") },
            singleLine = false,
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun PetFormState.validate(): PetFormState {
    return copy(
        nameError = if (name.isBlank()) "El nombre es obligatorio" else null,
        speciesError = if (species.isBlank()) "La especie es obligatoria" else null
    )
}

fun PetFormState.isValid() = nameError == null && speciesError == null && name.isNotBlank()
