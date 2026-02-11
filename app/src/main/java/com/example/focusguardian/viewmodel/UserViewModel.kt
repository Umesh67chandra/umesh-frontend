package com.example.focusguardian.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var userId by mutableStateOf<String?>(null)
    var role by mutableStateOf<String?>(null)

    fun login(
        emailInput: String,
        passwordInput: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = com.example.focusguardian.data.remote.RetrofitClient.apiService.login(
                    com.example.focusguardian.data.remote.LoginRequest(emailInput, passwordInput)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!
                    userId = data.userId
                    name = data.name ?: ""
                    email = data.email ?: ""
                    role = data.role
                    token = data.token
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            org.json.JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Login failed"
                        }
                    } else {
                        "Login failed"
                    }
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Connection error")
            }
        }
    }
    fun register(
        nameInput: String,
        emailInput: String,
        passwordInput: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = com.example.focusguardian.data.remote.RetrofitClient.apiService.register(
                    com.example.focusguardian.data.remote.RegisterRequest(nameInput, emailInput, passwordInput)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!
                    userId = data.userId
                    name = data.name ?: ""
                    email = data.email ?: ""
                    role = data.role
                    // Store token if needed, currently we just use it in memory or it should be returned
                    // Ideally we should store the token in DataStore/SharedPreferences
                    // For now, let's assume the LoginResponse token is sufficient for immediate use
                    // But wait, the updateRole needs the token. We need to save it.
                    // This ViewModel seems to be missing a token property. I'll add it.
                    token = data.token
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            org.json.JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "Registration failed"
                        }
                    } else {
                        "Registration failed"
                    }
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Connection error")
            }
        }
    }

    var token by mutableStateOf<String?>(null)

    fun updateRole(
        roleInput: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId == null || token == null) {
            onError("User not logged in")
            return
        }
        viewModelScope.launch {
            try {
                val response = com.example.focusguardian.data.remote.RetrofitClient.apiService.updateRole(
                    userId!!,
                    "Bearer $token",
                    com.example.focusguardian.data.remote.RoleRequest(roleInput)
                )
                if (response.isSuccessful) {
                    role = roleInput
                    onSuccess()
                } else {
                    onError("Failed to update role")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Connection error")
            }
        }
    }

    fun savePreferences(
        interests: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId == null || token == null) {
            onError("User not logged in")
            return
        }
        viewModelScope.launch {
            try {
                val response = com.example.focusguardian.data.remote.RetrofitClient.apiService.savePreferences(
                    userId!!,
                    "Bearer $token",
                    com.example.focusguardian.data.remote.PreferencesRequest(interests)
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed to save preferences")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Connection error")
            }
        }
    }
}
