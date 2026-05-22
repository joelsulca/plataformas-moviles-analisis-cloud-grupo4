package com.masterdog.app.ui.features.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterdog.app.data.ApiClient
import com.masterdog.app.data.Session
import com.masterdog.app.data.toCreateRequest
import com.masterdog.app.data.toUi
import com.masterdog.app.data.toUpdateRequest
import com.masterdog.app.data.PetUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetsViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _pets = MutableStateFlow<List<PetUi>>(emptyList())
    val pets: StateFlow<List<PetUi>> = _pets.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun consumeUpdateSuccess() { _updateSuccess.value = false }

    init { refresh() }

    fun refresh() {
        val userId = Session.currentUserId
        if (userId == 0L) return
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.listPets(userId)
                _pets.value = response.data
                    .filter { it.active != false }
                    .map { it.toUi() }
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las mascotas"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addPet(pet: PetUi, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = api.createPet(pet.toCreateRequest(Session.currentUserId))
                _pets.value = _pets.value + response.data.pet.toUi()
                onDone()
            } catch (e: Exception) {
                _error.value = "No se pudo registrar la mascota"
            }
        }
    }

    fun updatePet(updated: PetUi, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.updatePet(updated.toUpdateRequest())
                _pets.value = _pets.value.map { if (it.id == updated.id) updated else it }
                _updateSuccess.value = true
                onDone()
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar la mascota"
            }
        }
    }

    fun deletePet(petId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.deletePet(petId.toLongOrNull() ?: return@launch)
                _pets.value = _pets.value.filter { it.id != petId }
                onDone()
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar la mascota"
            }
        }
    }

    fun petById(id: String): PetUi? = _pets.value.find { it.id == id }

    fun consumeError() { _error.value = null }
}
