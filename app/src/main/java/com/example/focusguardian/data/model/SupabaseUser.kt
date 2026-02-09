package com.example.focusguardian.data.model

data class SupabaseUser(
    val id: String? = null,
    val email: String,
    val password_hash: String,
    val role: String? = null,
    val name: String? = null,
    val created_at: String? = null
)

data class CreateUserRequest(
    val email: String,
    val password_hash: String,
    val role: String? = null,
    val name: String? = null
)

data class UpdateUserRoleRequest(
    val role: String
)

data class UserPreferencesRequest(
    val user_id: String,
    val interests: List<String>,
    val sub_interests: List<String>
)
