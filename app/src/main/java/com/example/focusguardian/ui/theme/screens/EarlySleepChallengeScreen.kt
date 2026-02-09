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
fun EarlySleepChallengeScreen(navController: NavController) {
    val totalSeconds = 8 * 60 * 60
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .padding(16.dp)
    ) {

        // Header
        Text(
            text = "Challenges & Rewards",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gamify your digital wellness journey",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Challenge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF8E6BFF),
                                Color(0xFF6A5AE0)
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Challenge Mode",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Early Sleep Challenge",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Go to bed before your scheduled bedtime",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = formatHoursMinutes(remainingSeconds),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Remaining",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                LinearProgressIndicator(
                    progress = 1f - (remainingSeconds / totalSeconds.toFloat()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("✕  Give Up", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Achievements
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Recent Achievements", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "No completed challenges yet. Start one above!",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Badge Collection
        Text("Badge Collection", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Badge("First Step")
            Badge("3-Day Streak")
            Badge("Zen Master")
            Badge("Sleep Guardian")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Leaderboard
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF7B4DFF), Color(0xFF5E35B1))
                        )
                    )
                    .padding(16.dp)
            ) {
                Text("Leaderboard", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Leader("1", "Sarah K.", "2,450 pts")
                Leader("2", "You", "0 pts")
                Leader("3", "Mike R.", "1,650 pts")
            }
        }
    }
}

private fun formatHoursMinutes(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return "${hours}h ${minutes}m"
}

@Composable
private fun Badge(title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 11.sp)
    }
}

@Composable
private fun Leader(rank: String, name: String, score: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$rank  $name", color = Color.White)
        Text(score, color = Color.White)
    }
}
