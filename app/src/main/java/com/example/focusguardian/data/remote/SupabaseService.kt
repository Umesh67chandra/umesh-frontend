package com.example.focusguardian.data.remote

import com.example.focusguardian.data.model.CreateUserRequest
import com.example.focusguardian.data.model.SupabaseUser
import com.example.focusguardian.data.model.UpdateUserRoleRequest
import com.example.focusguardian.data.model.UserPreferencesRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseService {

    @POST("rest/v1/users")
    suspend fun createUser(
        @Body request: CreateUserRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<SupabaseUser>

    @GET("rest/v1/users")
    suspend fun getUserByEmail(
        @Query("email") emailEq: String,
        @Query("select") select: String = "id,email,password_hash,role,name,created_at"
    ): List<SupabaseUser>

    @PATCH("rest/v1/users")
    suspend fun updateUserRole(
        @Query("id") idEq: String,
        @Body request: UpdateUserRoleRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<SupabaseUser>

    @POST("rest/v1/user_preferences")
    suspend fun upsertPreferences(
        @Body request: UserPreferencesRequest,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<UserPreferencesRequest>
}
