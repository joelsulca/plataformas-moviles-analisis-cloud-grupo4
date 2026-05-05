package com.masterdog.app.mock

import java.time.LocalDate

// ── MODELOS UI ────────────────────────────────────────────────────────────────

data class UserUi(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String
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
    val bloodType: String
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

// ── MOCK DATA ─────────────────────────────────────────────────────────────────

object MockData {

    // ── USUARIOS ──────────────────────────────────────────────────────────────
    val currentUser = UserUi(
        id = "user-001",
        firstName = "Joel",
        lastName = "Sulca",
        email = "joel@email.com",
        phone = "+51 910797208",
        address = "Av. Reducto 1518, Miraflores"
    )

    // ── MASCOTAS ──────────────────────────────────────────────────────────────
    val pets = listOf(
        PetUi(
            id = "pet-001",
            name = "Teresa",
            species = "Gato",
            breed = "Lynx",
            gender = "Hembra",
            ageYears = 2,
            ageMonths = 2,
            weightKg = 3.5f,
            allergies = "Ninguna",
            neuteredStatus = "Desconocido",
            bloodType = "No probado"
        ),
        PetUi(
            id = "pet-002",
            name = "Rocky",
            species = "Perro",
            breed = "Labrador",
            gender = "Macho",
            ageYears = 4,
            ageMonths = 0,
            weightKg = 22f,
            allergies = "Polen",
            neuteredStatus = "Esterilizado",
            bloodType = "DEA 1.1+"
        )
    )

    // ── SERVICIOS ─────────────────────────────────────────────────────────────
    val services = listOf(
        ServiceUi("svc-001", "Primera Visita",
            "Trae vacunas, análisis y documentos médicos previos.",
            category = ServiceCategory.MEDICAL, priceRef = "S/ 60"),
        ServiceUi("svc-002", "Consulta General",
            "Atendida por médicos colegiados.",
            category = ServiceCategory.MEDICAL, priceRef = "S/ 50"),
        ServiceUi("svc-003", "Consulta General — Medicina Felina",
            "Especialista en gatos.",
            category = ServiceCategory.MEDICAL, priceRef = "S/ 55"),
        ServiceUi("svc-004", "Vacunas",
            "Solo para mascotas clínicamente sanas.",
            category = ServiceCategory.MEDICAL, priceRef = "S/ 40"),
        ServiceUi("svc-005", "Aplicación de Microchip",
            "Se aplica una sola vez. Registro internacional opcional.",
            category = ServiceCategory.MEDICAL, priceRef = "S/ 80"),
        ServiceUi("svc-006", "Baño",
            "Baño completo con secado.",
            category = ServiceCategory.AESTHETIC, priceRef = "S/ 35", durationMin = 60),
        ServiceUi("svc-007", "Peluquería / Corte de pelo",
            "Corte estético según raza.",
            category = ServiceCategory.AESTHETIC, priceRef = "S/ 50", durationMin = 90),
        ServiceUi("svc-008", "Corte de uñas — perros",
            "",
            category = ServiceCategory.AESTHETIC, priceRef = "S/ 15", durationMin = 20),
        ServiceUi("svc-009", "Corte de uñas — gatos",
            "",
            category = ServiceCategory.AESTHETIC, priceRef = "S/ 15", durationMin = 20),
    )

    // ── VETERINARIOS — "Cualquier veterinario" aparece primero ────────────────
    val vets = listOf(
        VetUi("vet-any", "Cualquier veterinario", ""),
        VetUi("vet-001", "Cindy Villaverde", "Veterinario"),
        VetUi("vet-002", "Fiorella Oré Oviedo", "Veterinario")
    )

    // ── HORARIOS POR VETERINARIO ──────────────────────────────────────────────
    // DayOfWeek: 1=Lun, 2=Mar, 3=Mié, 4=Jue, 5=Vie, 6=Sáb, 7=Dom
    // TODO API: GET /vets/{id}/schedule
    private val vetSchedules: Map<String, Map<Int, List<String>>> = mapOf(
        "vet-001" to mapOf(
            1 to listOf("08:00", "08:30", "09:00", "09:30", "10:00", "10:30"),
            2 to listOf("08:00", "09:00", "10:00", "11:00", "15:00", "16:00"),
            3 to listOf("08:30", "09:30", "10:30", "11:30"),
            4 to listOf("08:00", "09:00", "10:00", "15:00", "15:30", "16:00"),
            5 to listOf("08:00", "08:30", "09:00", "09:30"),
            6 to listOf("09:00", "10:00", "11:00")
        ),
        "vet-002" to mapOf(
            1 to listOf("09:00", "09:30", "10:00", "11:00", "15:00", "15:30", "16:00", "16:30"),
            2 to listOf("08:30", "09:30", "10:30", "11:30", "15:30", "16:30"),
            3 to listOf("08:00", "09:00", "10:00", "11:00", "15:00", "16:00"),
            4 to listOf("08:30", "09:30", "10:30", "11:30"),
            5 to listOf("08:00", "09:00", "10:00", "11:00", "15:00", "15:30", "16:00"),
            6 to listOf("08:00", "09:00", "10:00")
        )
    )

    /** Slots disponibles para un vet y fecha. Para "vet-any" devuelve la unión de todos los vets. */
    fun slotsFor(vetId: String, date: String): List<String> {
        val dayOfWeek = LocalDate.parse(date).dayOfWeek.value
        return if (vetId == "vet-any") {
            vets.filter { it.id != "vet-any" }
                .flatMap { v -> vetSchedules[v.id]?.get(dayOfWeek) ?: emptyList() }
                .distinct()
                .sorted()
        } else {
            vetSchedules[vetId]?.get(dayOfWeek) ?: emptyList()
        }
    }

    /**
     * Dado "vet-any" seleccionado, retorna el nombre del primer vet disponible
     * en esa fecha y hora concreta.
     */
    fun resolveVet(date: String, time: String): String {
        val dayOfWeek = LocalDate.parse(date).dayOfWeek.value
        return vets
            .filter { it.id != "vet-any" }
            .firstOrNull { vetSchedules[it.id]?.get(dayOfWeek)?.contains(time) == true }
            ?.name ?: "Veterinario disponible"
    }

    // ── CITAS ─────────────────────────────────────────────────────────────────
    val appointments = listOf(
        AppointmentUi(
            id = "apt-001",
            petId = "pet-001",
            petName = "Teresa",
            serviceId = "svc-004",
            serviceName = "Vacunas",
            vetName = "Fiorella Oré Oviedo",
            date = "2026-05-08",
            time = "08:00",
            status = AppointmentStatus.UPCOMING,
            reason = "Vacuna anual"
        ),
        AppointmentUi(
            id = "apt-002",
            petId = "pet-002",
            petName = "Rocky",
            serviceId = "svc-002",
            serviceName = "Consulta General",
            vetName = "Cindy Villaverde",
            date = "2026-04-15",
            time = "09:30",
            status = AppointmentStatus.PAST,
            reason = "Revisión general"
        )
    )

    // ── HELPERS ───────────────────────────────────────────────────────────────
    fun petById(id: String): PetUi? = pets.find { it.id == id }
    fun serviceById(id: String): ServiceUi? = services.find { it.id == id }
    fun appointmentById(id: String): AppointmentUi? = appointments.find { it.id == id }
}
