package com.example.focusguardian.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var role by mutableStateOf<String?>(null)
}
