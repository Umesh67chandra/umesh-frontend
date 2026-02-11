package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.focusguardian.R

import com.example.focusguardian.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotPasswordDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE3DFFF),
                        Color(0xFFF7EFFF)
                    )
                )
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        // LOGO
        Icon(
            painter = painterResource(id = R.drawable.ic_focus_guardian_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Focus Guardian",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "✨ Smart Social Media Awareness",
            color = Color(0xFF6B5CFF),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // LOGIN CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {

                Text(
                    text = "Welcome Back",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sign in to your account",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Forgot password?",
                    modifier = Modifier.clickable { showForgotPasswordDialog = true },
                    color = Color(0xFF6B5CFF),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            loginError = "Please enter email and password"
                        } else {
                            userViewModel.login(
                                emailInput = email.trim(),
                                passwordInput = password,
                                onSuccess = {
                                    loginError = null
                                    onSignIn()
                                },
                                onError = { error ->
                                    loginError = error
                                    android.util.Log.e("LoginScreen", "Login failed: $error")
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Sign In", fontSize = 16.sp)
                }
                
                if (loginError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // CREATE ACCOUNT SECTION
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF5B4CF5),
                            Color(0xFF8A4CF5)
                        )
                    )
                )
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "New Here?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create an account and start your journey\nwith personalized content awareness",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onCreateAccount,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text("Create Account", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Forgot Password") },
        text = {
            Column {
                Text("Enter your email to receive a password reset link.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Send")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
