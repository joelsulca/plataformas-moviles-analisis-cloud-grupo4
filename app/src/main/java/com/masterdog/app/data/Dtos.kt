package com.masterdog.app.data

// ── ENVOLTORIOS GENÉRICOS ───────────────────────────────────────────────────

/** Todas las respuestas del backend vienen envueltas en `{ "data": ... }`. */
data class ApiEnvelope<T>(val data: T)

/** Respuesta plana de operaciones que solo devuelven éxito/mensaje (delete, etc.). */
data class SimpleResult(
    val success: Boolean = true,
    val message: String? = null
)

// ── UPLOAD ──────────────────────────────────────────────────────────────────

data class PresignedUrlResult(val url: String, val objectUrl: String)

// ── AUTH ────────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String,
    val photoUrl: String = ""
)

data class LoginRequest(
    val email: String,
    val password: String
)

/** Wrapper que el backend usa en login / register / update perfil. */
data class AuthResult(
    val success: Boolean = true,
    val message: String? = null,
    val user: UserDto? = null
)

data class UserDto(
    val id: Any?,           // backend devuelve Long o String según endpoint
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val photoUrl: String? = ""
)

data class UpdateUserRequest(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String,
    val photoUrl: String = ""
)

// ── MASCOTAS ────────────────────────────────────────────────────────────────

data class PetDto(
    val id: Any?,
    val userId: Any?,
    val name: String?,
    val species: String?,
    val breed: String?,
    val gender: String?,
    val birthDate: String? = null,
    val color: String? = "",
    val weightKg: Any?,         // backend a veces lo manda como string
    val allergies: String?,
    val neuteredStatus: Any?,   // boolean o string según endpoint
    val bloodType: String?,
    val photoUrl: String? = "",
    val active: Boolean? = true
)

/** Wrapper de POST/PUT /mascota. */
data class PetMutationResult(
    val success: Boolean = true,
    val message: String? = null,
    val pet: PetDto
)

data class CreatePetRequest(
    val userId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val weightKg: Double,
    val gender: String,
    val color: String,
    val birthDate: String,
    val allergies: String,
    val neuteredStatus: Boolean,
    val bloodType: String,
    val photoUrl: String = ""
)

data class UpdatePetRequest(
    val id: Long,
    val name: String,
    val species: String,
    val breed: String,
    val weightKg: Double,
    val gender: String,
    val color: String,
    val birthDate: String,
    val bloodType: String,
    val allergies: String,
    val neuteredStatus: Boolean,
    val photoUrl: String = ""
)

// ── SERVICIOS ───────────────────────────────────────────────────────────────

data class ServiceDto(
    val id: Any?,
    val name: String?,
    val description: String?,
    val category: String?,
    val priceRef: String?,
    val durationMin: Int? = 30,
    val photoUrl: String? = null,
    val active: Boolean? = true
)

// ── VETERINARIOS ────────────────────────────────────────────────────────────

data class VetDto(
    val id: Any?,
    val name: String?,
    val specialty: String?,
    val email: String? = null,
    val phone: String? = null,
    val cmp: String? = null,
    val photoUrl: String? = null,
    val active: Boolean? = true
)

data class ScheduleDto(
    val id: Any?,
    val vetId: Any?,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val active: Boolean? = true
)

data class BusySlotDto(
    val id: Any?,
    val vetId: Any?,
    val date: String,
    val time: String,
    val status: String?
)

// ── CITAS ───────────────────────────────────────────────────────────────────

data class AppointmentDto(
    val id: Any?,
    val petId: Any?,
    val petName: String?,
    val serviceId: Any?,
    val serviceName: String?,
    val vetId: Any?,
    val vetName: String?,
    val date: String?,
    val time: String?,
    val status: String?,
    val reason: String?
)

data class CreateAppointmentRequest(
    val userId: Long,
    val petId: Long,
    val vetId: Long,
    val serviceId: Long,
    val date: String,
    val time: String,
    val reason: String
)

/** Wrapper de PUT /cita/{id}/confirmar y /cancelar. */
data class AppointmentActionResult(
    val success: Boolean = true,
    val message: String? = null,
    val appointment: AppointmentRef? = null
)

data class AppointmentRef(
    val id: Any?,
    val status: String?
)
