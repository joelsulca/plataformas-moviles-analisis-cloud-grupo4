package com.masterdog.app.ui.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterdog.app.data.ApiClient
import com.masterdog.app.data.Session
import com.masterdog.app.data.UpdateUserRequest
import com.masterdog.app.data.toUi
import com.masterdog.app.data.UserUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {

    private val api = ApiClient.api

    var user by mutableStateOf(
        Session.currentUser ?: UserUi("", "", "", "", "", "")
    )
        private set

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun refreshFromSession() {
        Session.currentUser?.let { user = it }
    }

    fun updateUser(updated: UserUi, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = api.updateUser(
                    UpdateUserRequest(
                        id = updated.id,
                        firstName = updated.firstName,
                        lastName = updated.lastName,
                        phone = updated.phone,
                        address = updated.address
                    )
                )
                val result = response.data
                val userDto = result.user
                if (!result.success || userDto == null) {
                    _error.value = result.message ?: "No se pudo actualizar el perfil"
                    return@launch
                }
                val saved = userDto.toUi()
                user = saved
                Session.set(saved)
                onDone()
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar el perfil"
            }
        }
    }

    fun logout() {
        Session.clear()
        user = UserUi("", "", "", "", "", "")
    }

    fun consumeError() { _error.value = null }
}
