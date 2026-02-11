package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.R
import com.example.focusguardian.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onCreateAccount: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3DFFF), Color(0xFFF5E9FF))
                    )
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // 🔹 LOGO (UNCHANGED)
            Image(
                painter = painterResource(R.drawable.ic_focus_guardian_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Focus Guardian", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("✨ Smart Social Media Awareness", color = Color(0xFF6B5CFF))

            Spacer(Modifier.height(24.dp))

            // 🔹 CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(Modifier.padding(22.dp)) {

                    // 🔹 STEP INDICATOR
                    StepIndicator(currentStep = 1, totalSteps = 4)

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Create your account",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Enter your details to get started",
                        color = Color.Black
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = null
                                    userViewModel.register(
                                        nameInput = name.trim(),
                                        emailInput = email.trim(),
                                        passwordInput = password,
                                        onSuccess = {
                                            isLoading = false
                                            onCreateAccount()
                                        },
                                        onError = { error ->
                                            isLoading = false
                                            errorMessage = error
                                        }
                                    )
                                } else {
                                    errorMessage = "Please fill all required fields"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Create Account")
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    }
                }

            Spacer(Modifier.height(40.dp))
        }
    }
}
