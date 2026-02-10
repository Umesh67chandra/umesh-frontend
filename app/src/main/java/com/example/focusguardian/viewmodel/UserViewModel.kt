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
                val response = com.example.focusguardian.data.remote.RetrofitClient.api.login(
                    com.example.focusguardian.data.remote.LoginRequest(emailInput, passwordInput)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!
                    userId = data.userId
                    name = data.name ?: ""
                    email = data.email ?: ""
                    role = data.role
                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Connection error")
            }
        }
    }
}
