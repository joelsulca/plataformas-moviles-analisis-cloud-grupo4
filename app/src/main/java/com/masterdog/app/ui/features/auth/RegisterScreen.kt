package com.masterdog.app.ui.features.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.masterdog.app.ui.navigation.Screen
import com.masterdog.app.ui.shared.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val loading by authViewModel.loading.collectAsState()
    val authError by authViewModel.error.collectAsState()

    LaunchedEffect(authError) {
        authError?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.consumeError()
        }
    }

    fun validate(): Boolean {
        firstNameError = if (firstName.isBlank()) "El nombre es obligatorio" else null
        lastNameError = if (lastName.isBlank()) "Los apellidos son obligatorios" else null
        phoneError = if (phone.isBlank()) "El teléfono es obligatorio" else null
        emailError = if (email.isBlank()) "El correo es obligatorio" else null
        addressError = if (address.isBlank()) "La dirección es obligatoria" else null
        passwordError = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 8 -> "Mínimo 8 caracteres"
            else -> null
        }
        return listOf(firstNameError, lastNameError, phoneError, emailError, addressError, passwordError).all { it == null }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Crear cuenta",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it; firstNameError = null },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                isError = firstNameError != null,
                supportingText = { firstNameError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it; lastNameError = null },
                label = { Text("Apellidos") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                isError = lastNameError != null,
                supportingText = { lastNameError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; phoneError = null },
                label = { Text("Teléfono") },
                leadingIcon = { Text("+51", modifier = Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyMedium) },
                trailingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                isError = phoneError != null,
                supportingText = { phoneError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it; addressError = null },
                label = { Text("Dirección") },
                leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                isError = addressError != null,
                supportingText = { addressError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } ?: Text("Mínimo 8 caracteres") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (validate()) {
                        val phoneE164 = if (phone.startsWith("+")) phone else "+51$phone"
                        authViewModel.register(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = email.trim(),
                            password = password,
                            phone = phoneE164,
                            address = address.trim()
                        ) {
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Cuenta creada exitosamente!")
                            }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    if (loading) "Registrando…" else "Registrarse",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
