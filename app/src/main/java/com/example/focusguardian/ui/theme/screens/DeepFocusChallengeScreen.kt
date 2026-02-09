package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun DeepFocusChallengeScreen(navController: NavController) {
    val totalSeconds = 60 * 60
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "1-Hour Deep Focus Challenge",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Complete focused work without any app distraction",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = formatDuration(remainingSeconds),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Remaining",
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("✕  Give Up")
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins}m ${secs.toString().padStart(2, '0')}s"
}
