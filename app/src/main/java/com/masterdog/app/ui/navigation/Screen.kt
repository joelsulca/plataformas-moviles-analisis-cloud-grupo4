package com.masterdog.app.ui.navigation

sealed class Screen(val route: String) {
    object Login              : Screen("login")
    object Register           : Screen("register")
    object Home               : Screen("home")
    object PetList            : Screen("pet_list")
    object PetDetail          : Screen("pet_detail/{petId}") {
        fun createRoute(petId: String) = "pet_detail/$petId"
    }
    object PetAdd             : Screen("pet_add")
    object PetEdit            : Screen("pet_edit/{petId}") {
        fun createRoute(petId: String) = "pet_edit/$petId"
    }
    object AppointmentList    : Screen("appointment_list")
    // serviceId es opcional: appointment_new?serviceId={serviceId}
    object AppointmentNew     : Screen("appointment_new?serviceId={serviceId}") {
        fun createRoute(serviceId: String? = null) =
            if (serviceId != null) "appointment_new?serviceId=$serviceId" else "appointment_new?serviceId="
    }
    object AppointmentStep2   : Screen("appointment_step2")
    object AppointmentStep3   : Screen("appointment_step3")
    object AppointmentStep4   : Screen("appointment_step4")
    object AppointmentSummary : Screen("appointment_summary/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_summary/$appointmentId"
    }
    object AppointmentDetail  : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: String) = "appointment_detail/$appointmentId"
    }
    object ServiceCatalog     : Screen("service_catalog")
    object UserProfile        : Screen("user_profile")
    object UserProfileEdit    : Screen("user_profile_edit")
}
