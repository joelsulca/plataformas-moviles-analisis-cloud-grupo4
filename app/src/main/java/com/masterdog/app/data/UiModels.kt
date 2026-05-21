package com.masterdog.app.data

data class UserUi(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String,
    val photoUrl: String = ""
)

data class PetUi(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val gender: String,
    val ageYears: Int,
    val ageMonths: Int,
    val weightKg: Float,
    val allergies: String,
    val neuteredStatus: String,
    val bloodType: String,
    val photoUrl: String = ""
)

enum class ServiceCategory { MEDICAL, AESTHETIC }

data class ServiceUi(
    val id: String,
    val name: String,
    val description: String,
    val category: ServiceCategory,
    val priceRef: String,
    val durationMin: Int = 30
)

data class VetUi(
    val id: String,
    val name: String,
    val specialty: String
)

enum class AppointmentStatus { UPCOMING, CONFIRMED, PAST, CANCELLED }

data class AppointmentUi(
    val id: String,
    val petId: String,
    val petName: String,
    val serviceId: String,
    val serviceName: String,
    val vetName: String,
    val date: String,
    val time: String,
    val status: AppointmentStatus,
    val reason: String
)
