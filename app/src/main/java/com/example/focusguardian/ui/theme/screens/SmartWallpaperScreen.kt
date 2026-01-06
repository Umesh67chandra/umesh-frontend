package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmartWallpaperScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Dynamic wallpapers that react to your digital habits",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        // Wallpaper Preview
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("12:45", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Great job! Keep it up.", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Theme Settings", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Low Usage • Score: 29",
            color = Color(0xFF2ECC71)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Fullscreen Preview")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download Current Wallpaper")
        }

        Spacer(Modifier.height(20.dp))

        Text("How it Works", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        Text("• Green – Healthy usage (0–40)")
        Text("• Yellow – Moderate usage (41–70)")
        Text("• Red – High usage (71–100)")
    }
}
