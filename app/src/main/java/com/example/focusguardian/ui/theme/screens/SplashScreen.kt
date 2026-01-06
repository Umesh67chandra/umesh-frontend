package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.focusguardian.R

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFDAD6FF), Color(0xFFEEDCFF))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(Modifier.height(40.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_focus_guardian_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Focus Guardian", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("✨ Smart Social Media Awareness", color = Color(0xFF6B5CFF))
        }

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Get Started")
        }
    }
}
