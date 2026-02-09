package com.example.focusguardian.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val userId: String?,
    val name: String?,
    val email: String?,
    val role: String?
)

data class RoleRequest(
    val role: String
)

data class PreferencesRequest(
    val interests: List<String>,
    val sub_interests: List<String>
)

data class SimpleResponse(
    val success: Boolean,
    val message: String
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int
)

data class LeaderboardResponse(
    val success: Boolean,
    val items: List<LeaderboardEntry>
)

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/users/{userId}/role")
    suspend fun updateRole(
        @retrofit2.http.Path("userId") userId: String,
        @Body request: RoleRequest,
        @retrofit2.http.Header("Authorization") authHeader: String
    ): Response<SimpleResponse>

    @POST("api/users/{userId}/preferences")
    suspend fun savePreferences(
        @retrofit2.http.Path("userId") userId: String,
        @Body request: PreferencesRequest,
        @retrofit2.http.Header("Authorization") authHeader: String
    ): Response<SimpleResponse>

    @retrofit2.http.GET("api/leaderboard")
    suspend fun getLeaderboard(): Response<LeaderboardResponse>
}
