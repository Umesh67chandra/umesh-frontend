package com.example.focusguardian.data

import com.example.focusguardian.data.remote.ApiService
import com.example.focusguardian.data.remote.AuthResponse
import com.example.focusguardian.data.remote.LoginRequest
import com.example.focusguardian.data.remote.PreferencesRequest
import com.example.focusguardian.data.remote.RegisterRequest
import com.example.focusguardian.data.remote.RoleRequest

class UserRepository(private val api: ApiService) {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> = runCatching {
        val response = api.register(request)
        if (!response.isSuccessful || response.body() == null) {
            error("Register failed")
        }
        response.body()!!
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> = runCatching {
        val response = api.login(request)
        if (!response.isSuccessful || response.body() == null) {
            error("Login failed")
        }
        response.body()!!
    }

    suspend fun updateUserRole(userId: String, role: String, token: String): Result<Unit> = runCatching {
        val response = api.updateRole(userId, RoleRequest(role), "Bearer $token")
        if (!response.isSuccessful) {
            error("Role update failed")
        }
        Unit
    }

    suspend fun savePreferences(
        userId: String,
        interests: Set<String>,
        subInterests: Set<String>,
        token: String
    ): Result<Unit> = runCatching {
        val response = api.savePreferences(
            userId,
            PreferencesRequest(interests.toList(), subInterests.toList()),
            "Bearer $token"
        )
        if (!response.isSuccessful) {
            error("Save preferences failed")
        }
        Unit
    }
}
