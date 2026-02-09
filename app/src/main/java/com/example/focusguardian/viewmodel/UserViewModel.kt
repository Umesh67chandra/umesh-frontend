package com.example.focusguardian.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusguardian.data.UserRepository
import com.example.focusguardian.data.remote.ApiClient
import com.example.focusguardian.data.remote.LoginRequest
import com.example.focusguardian.data.remote.RegisterRequest
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repository = UserRepository(ApiClient.apiService)

    var userId by mutableStateOf<String?>(null)
    var authToken by mutableStateOf<String?>(null)
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var role by mutableStateOf<String?>(null)
    var interests by mutableStateOf<Set<String>>(emptySet())
    var subInterests by mutableStateOf<Set<String>>(emptySet())

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(
                RegisterRequest(
                    name = name.trim(),
                    email = email.trim(),
                    password = password
                )
            )
            result.onSuccess { response ->
                if (response.success && response.userId != null) {
                    userId = response.userId
                    authToken = response.token
                    this@UserViewModel.name = response.name.orEmpty()
                    this@UserViewModel.email = response.email.orEmpty()
                    this@UserViewModel.role = response.role
                    onResult(true, null)
                } else {
                    onResult(false, response.message)
                }
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(LoginRequest(email.trim(), password))
            result.onSuccess { response ->
                if (response.success && response.userId != null) {
                    userId = response.userId
                    authToken = response.token
                    this@UserViewModel.name = response.name.orEmpty()
                    this@UserViewModel.email = response.email.orEmpty()
                    this@UserViewModel.role = response.role
                    onResult(true, null)
                } else {
                    onResult(false, response.message)
                }
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }

    fun updateRole(role: String, onResult: (Boolean, String?) -> Unit) {
        val currentUserId = userId
        val token = authToken
        if (currentUserId == null) {
            onResult(false, "User not initialized")
            return
        }
        if (token.isNullOrBlank()) {
            onResult(false, "Missing auth token")
            return
        }

        viewModelScope.launch {
            val result = repository.updateUserRole(currentUserId, role, token)
            result.onSuccess {
                this@UserViewModel.role = role
                onResult(true, null)
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }

    fun updateInterests(interests: Set<String>) {
        this.interests = interests
    }

    fun savePreferences(subInterests: Set<String>, onResult: (Boolean, String?) -> Unit) {
        val currentUserId = userId
        val token = authToken
        if (currentUserId == null) {
            onResult(false, "User not initialized")
            return
        }
        if (token.isNullOrBlank()) {
            onResult(false, "Missing auth token")
            return
        }

        this.subInterests = subInterests

        viewModelScope.launch {
            val result = repository.savePreferences(currentUserId, interests, subInterests, token)
            result.onSuccess {
                onResult(true, null)
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }
}
