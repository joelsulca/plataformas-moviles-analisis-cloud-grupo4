package com.masterdog.app.ui.features.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterdog.app.data.ApiClient
import com.masterdog.app.data.LoginRequest
import com.masterdog.app.data.RegisterRequest
import com.masterdog.app.data.Session
import com.masterdog.app.data.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val api = ApiClient.api

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun consumeError() { _error.value = null }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val envelope = api.login(LoginRequest(email, password))
                val result = envelope.data
                Log.d("AuthVM", "login raw data → success=${result.success} user=${result.user} message=${result.message}")
                val userDto = result.user
                if (!result.success || userDto == null) {
                    _error.value = result.message ?: "Credenciales inválidas"
                    return@launch
                }
                val ui = userDto.toUi()
                Log.d("AuthVM", "login mapped user → id=${ui.id} firstName=${ui.firstName}")
                Session.set(ui)
                Log.d("AuthVM", "Session.currentUserId=${Session.currentUserId}")
                onSuccess()
            } catch (e: Exception) {
                Log.e("AuthVM", "login error", e)
                _error.value = "Credenciales inválidas o sin conexión"
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = api.register(
                    RegisterRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        phone = phone,
                        address = address
                    )
                ).data
                val userDto = result.user
                if (!result.success || userDto == null) {
                    _error.value = result.message ?: "No se pudo crear la cuenta"
                    return@launch
                }
                Session.set(userDto.toUi())
                onSuccess()
            } catch (e: Exception) {
                _error.value = "No se pudo crear la cuenta. Verifica tus datos."
            } finally {
                _loading.value = false
            }
        }
    }
}
