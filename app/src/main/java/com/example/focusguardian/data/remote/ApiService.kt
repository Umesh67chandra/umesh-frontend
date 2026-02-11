package com.example.focusguardian.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val userId: String?,
    val name: String?,
    val email: String?,
    val role: String?
)

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    @POST("api/users/{userId}/role")
    suspend fun updateRole(
        @retrofit2.http.Path("userId") userId: String,
        @retrofit2.http.Header("Authorization") token: String,
        @Body request: RoleRequest
    ): Response<Void>

    @POST("api/users/{userId}/preferences")
    suspend fun savePreferences(
        @retrofit2.http.Path("userId") userId: String,
        @retrofit2.http.Header("Authorization") token: String,
        @Body request: PreferencesRequest
    ): Response<Void>
}

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RoleRequest(
    val role: String
)

data class PreferencesRequest(
    val interests: List<String>,
    val sub_interests: List<String> = emptyList()
)
