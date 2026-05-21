package com.masterdog.app.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MasterDogApi {

    // ── AUTH ────────────────────────────────────────────────────────────────
    @POST("usuario")
    suspend fun register(@Body body: RegisterRequest): ApiEnvelope<AuthResult>

    @POST("login")
    suspend fun login(@Body body: LoginRequest): ApiEnvelope<AuthResult>

    @PUT("usuario")
    suspend fun updateUser(@Body body: UpdateUserRequest): ApiEnvelope<AuthResult>

    // ── MASCOTAS ────────────────────────────────────────────────────────────
    @GET("mascotas/usuario/{userId}")
    suspend fun listPets(@Path("userId") userId: Long): ApiEnvelope<List<PetDto>>

    @POST("mascota")
    suspend fun createPet(@Body body: CreatePetRequest): ApiEnvelope<PetMutationResult>

    @PUT("mascota")
    suspend fun updatePet(@Body body: UpdatePetRequest): ApiEnvelope<PetMutationResult>

    @DELETE("mascota/{petId}")
    suspend fun deletePet(@Path("petId") petId: Long): ApiEnvelope<SimpleResult>

    // ── SERVICIOS ───────────────────────────────────────────────────────────
    @GET("servicios")
    suspend fun listServices(): ApiEnvelope<List<ServiceDto>>

    // ── VETERINARIOS Y DISPONIBILIDAD ───────────────────────────────────────
    @GET("veterinarios")
    suspend fun listVets(): ApiEnvelope<List<VetDto>>

    @GET("veterinarios/{vetId}/horarios")
    suspend fun vetSchedules(@Path("vetId") vetId: Long): ApiEnvelope<List<ScheduleDto>>

    @GET("citas/{vetId}/fecha/{date}")
    suspend fun busySlots(
        @Path("vetId") vetId: Long,
        @Path("date") date: String
    ): ApiEnvelope<List<BusySlotDto>>

    // ── CITAS ───────────────────────────────────────────────────────────────
    @POST("citas")
    suspend fun createAppointment(
        @Body body: CreateAppointmentRequest
    ): ApiEnvelope<AppointmentDto>

    @PUT("cita/{appointmentId}/confirmar")
    suspend fun confirmAppointment(
        @Path("appointmentId") appointmentId: Long
    ): ApiEnvelope<AppointmentActionResult>

    @PUT("cita/{appointmentId}/cancelar")
    suspend fun cancelAppointment(
        @Path("appointmentId") appointmentId: Long
    ): ApiEnvelope<AppointmentActionResult>

    @GET("mascotas/{petId}/citas")
    suspend fun petAppointments(
        @Path("petId") petId: Long
    ): ApiEnvelope<List<AppointmentDto>>
}
