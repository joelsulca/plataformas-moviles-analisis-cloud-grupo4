package com.masterdog.app.ui.features.pets

import androidx.lifecycle.ViewModel
import com.masterdog.app.mock.MockData
import com.masterdog.app.mock.PetUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetsViewModel : ViewModel() {

    private val _pets = MutableStateFlow(MockData.pets.toMutableList())
    val pets: StateFlow<List<PetUi>> = _pets.asStateFlow()

    fun addPet(pet: PetUi) {
        _pets.value = (_pets.value + pet).toMutableList()
    }

    fun updatePet(updated: PetUi) {
        _pets.value = _pets.value.map { if (it.id == updated.id) updated else it }.toMutableList()
    }

    fun deletePet(petId: String) {
        _pets.value = _pets.value.filter { it.id != petId }.toMutableList()
    }

    fun petById(id: String): PetUi? = _pets.value.find { it.id == id }
}
