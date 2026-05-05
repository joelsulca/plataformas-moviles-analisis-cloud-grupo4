package com.masterdog.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.masterdog.app.ui.features.appointments.AppointmentsViewModel
import com.masterdog.app.ui.features.auth.LoginScreen
import com.masterdog.app.ui.features.auth.RegisterScreen
import com.masterdog.app.ui.features.home.HomeScreen
import com.masterdog.app.ui.features.pets.PetAddScreen
import com.masterdog.app.ui.features.pets.PetEditScreen
import com.masterdog.app.ui.features.pets.PetListScreen
import com.masterdog.app.ui.features.pets.PetsViewModel
import com.masterdog.app.ui.features.profile.UserProfileEditScreen
import com.masterdog.app.ui.features.profile.UserProfileScreen
import com.masterdog.app.ui.features.profile.UserProfileViewModel
import com.masterdog.app.ui.shared.components.BottomBar

// Rutas donde se muestra el BottomBar
private val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.PetList.route,
    Screen.AppointmentList.route,
    Screen.UserProfile.route
)

@Composable
fun MasterDogNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val petsViewModel: PetsViewModel = viewModel()
    val appointmentsViewModel: AppointmentsViewModel = viewModel()
    val profileViewModel: UserProfileViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }

            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    appointmentsViewModel = appointmentsViewModel
                )
            }

            // ── PERFIL ────────────────────────────────────────────────────────
            composable(Screen.UserProfile.route) {
                UserProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }

            composable(Screen.UserProfileEdit.route) {
                UserProfileEditScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
            // ── MASCOTAS ──────────────────────────────────────────────────────
            composable(Screen.PetList.route) {
                PetListScreen(
                    navController = navController,
                    petsViewModel = petsViewModel,
                    appointmentsViewModel = appointmentsViewModel
                )
            }

            composable(Screen.PetAdd.route) {
                PetAddScreen(
                    navController = navController,
                    petsViewModel = petsViewModel
                )
            }

            composable(
                route = Screen.PetEdit.route,
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStack ->
                PetEditScreen(
                    navController = navController,
                    petId = backStack.arguments?.getString("petId") ?: "",
                    petsViewModel = petsViewModel
                )
            }
        }
    }
}
