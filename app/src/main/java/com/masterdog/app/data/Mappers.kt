package com.masterdog.app.data

import com.masterdog.app.data.AppointmentStatus
import com.masterdog.app.data.AppointmentUi
import com.masterdog.app.data.PetUi
import com.masterdog.app.data.ServiceCategory
import com.masterdog.app.data.ServiceUi
import com.masterdog.app.data.UserUi
import com.masterdog.app.data.VetUi
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

// ── HELPERS ─────────────────────────────────────────────────────────────────

private fun Any?.idString(): String = when (this) {
    null -> ""
    is Number -> this.toLong().toString()
    else -> this.toString()
}

private fun Any?.toFloatOrZero(): Float = when (this) {
    is Number -> this.toFloat()
    is String -> this.toFloatOrNull() ?: 0f
    else -> 0f
}

private fun parseLocalDate(raw: String?): LocalDate? = try {
    if (raw.isNullOrBlank()) null else LocalDate.parse(raw)
} catch (e: DateTimeParseException) {
    null
}

// ── USER ────────────────────────────────────────────────────────────────────

fun UserDto.toUi(): UserUi = UserUi(
    id = id.idString(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    email = email.orEmpty(),
    phone = phone.orEmpty(),
    address = address.orEmpty(),
    photoUrl = photoUrl.orEmpty()
)

// ── PET ─────────────────────────────────────────────────────────────────────

fun PetDto.toUi(): PetUi {
    val birth = parseLocalDate(birthDate)
    val period = birth?.let { Period.between(it, LocalDate.now()) }
    val years = period?.years ?: 0
    val months = period?.months ?: 0

    val neutered = when (val raw = neuteredStatus) {
        is Boolean -> if (raw) "Esterilizado" else "No esterilizado"
        is String -> when {
            raw.equals("true", ignoreCase = true) -> "Esterilizado"
            raw.equals("false", ignoreCase = true) -> "No esterilizado"
            raw.isBlank() -> "Desconocido"
            else -> raw
        }
        else -> "Desconocido"
    }

    return PetUi(
        id = id.idString(),
        name = name.orEmpty(),
        species = species.orEmpty(),
        breed = breed.orEmpty(),
        gender = gender.orEmpty(),
        ageYears = years,
        ageMonths = months,
        weightKg = weightKg.toFloatOrZero(),
        allergies = allergies.orEmpty().ifBlank { "Ninguna" },
        neuteredStatus = neutered,
        bloodType = bloodType.orEmpty().ifBlank { "No probado" },
        photoUrl = photoUrl.orEmpty()
    )
}

/** Calcula una `birthDate` aproximada a partir de ageYears + ageMonths. */
private fun birthDateFromAge(ageYears: Int, ageMonths: Int): String =
    LocalDate.now()
        .minusYears(ageYears.toLong())
        .minusMonths(ageMonths.toLong())
        .toString()

private fun PetUi.neuteredBoolean(): Boolean = neuteredStatus == "Esterilizado"

fun PetUi.toCreateRequest(userId: Long): CreatePetRequest = CreatePetRequest(
    userId = userId,
    name = name,
    species = species,
    breed = breed,
    weightKg = weightKg.toDouble(),
    gender = gender,
    color = "",
    birthDate = birthDateFromAge(ageYears, ageMonths),
    allergies = allergies,
    neuteredStatus = neuteredBoolean(),
    bloodType = bloodType,
    photoUrl = photoUrl
)

fun PetUi.toUpdateRequest(): UpdatePetRequest = UpdatePetRequest(
    id = id.toLongOrNull() ?: 0L,
    name = name,
    species = species,
    breed = breed,
    weightKg = weightKg.toDouble(),
    gender = gender,
    color = "",
    birthDate = birthDateFromAge(ageYears, ageMonths),
    bloodType = bloodType,
    allergies = allergies,
    neuteredStatus = neuteredBoolean(),
    photoUrl = photoUrl
)

// ── SERVICE ─────────────────────────────────────────────────────────────────

fun ServiceDto.toUi(): ServiceUi {
    val cat = when (category?.uppercase()) {
        "MEDICA", "MEDICAL" -> ServiceCategory.MEDICAL
        "ESTETICO", "ESTETICA", "AESTHETIC" -> ServiceCategory.AESTHETIC
        else -> ServiceCategory.MEDICAL
    }
    return ServiceUi(
        id = id.idString(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        category = cat,
        priceRef = priceRef.orEmpty(),
        durationMin = durationMin ?: 30
    )
}

// ── VET ─────────────────────────────────────────────────────────────────────

fun VetDto.toUi(): VetUi = VetUi(
    id = id.idString(),
    name = name.orEmpty(),
    specialty = specialty.orEmpty()
)

// ── APPOINTMENT ─────────────────────────────────────────────────────────────

fun AppointmentDto.toUi(): AppointmentUi {
    val mapped = when (status?.uppercase()) {
        "PENDIENTE", "UPCOMING" -> AppointmentStatus.UPCOMING
        "CONFIRMADA", "CONFIRMED" -> AppointmentStatus.CONFIRMED
        "CANCELADA", "CANCELLED" -> AppointmentStatus.CANCELLED
        "COMPLETADA", "REALIZADA", "PAST" -> AppointmentStatus.PAST
        else -> AppointmentStatus.UPCOMING
    }
    // Si la fecha ya pasó y la cita no está CANCELLED, se considera PAST.
    val parsedDate = parseLocalDate(date)
    val final = if (
        mapped != AppointmentStatus.CANCELLED &&
        parsedDate != null &&
        parsedDate.isBefore(LocalDate.now())
    ) AppointmentStatus.PAST else mapped

    return AppointmentUi(
        id = id.idString(),
        petId = petId.idString(),
        petName = petName.orEmpty(),
        serviceId = serviceId.idString(),
        serviceName = serviceName.orEmpty(),
        vetName = vetName.orEmpty(),
        date = date.orEmpty(),
        time = (time ?: "").take(5),   // recorta "HH:mm:ss" → "HH:mm" si llega así
        status = final,
        reason = reason.orEmpty()
    )
}
