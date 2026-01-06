package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.navigation.Routes

@Composable
fun ChallengesScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🔹 Header
        Text(
            text = "Challenges & Rewards",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Gamify your digital wellness journey",
            fontSize = 13.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Challenge Mode Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF7043), Color(0xFFE91E63))
                        )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = "Challenge Mode",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ 30-Min No Scroll
                ChallengeItem(
                    title = "30-Min No-Scroll Challenge",
                    subtitle = "Stay away from all social media apps for 30 minutes",
                    time = "30m",
                    onClick = {
                        navController.navigate(Routes.NO_SCROLL)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ 1-Hour Deep Focus
                ChallengeItem(
                    title = "1-Hour Deep Focus Challenge",
                    subtitle = "Complete focused work without any app usage",
                    time = "1h",
                    onClick = {
                        navController.navigate(Routes.DEEP_FOCUS)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Early Sleep
                ChallengeItem(
                    title = "Early Sleep Challenge",
                    subtitle = "Go to bed before your scheduled bedtime",
                    time = "8h",
                    onClick = {
                        navController.navigate(Routes.EARLY_SLEEP)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Recent Achievements
        Text(
            text = "Recent Achievements",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "No completed challenges yet. Start one above!",
            fontSize = 13.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Badge Collection
        Text(
            text = "Badge Collection",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BadgeItem(
                title = "First Step",
                icon = Icons.Default.Done,
                onClick = { navController.navigate(Routes.FIRST_STEP_BADGE) }
            )
            BadgeItem(
                title = "3-Day Streak",
                icon = Icons.Default.Leaderboard,
                onClick = { navController.navigate(Routes.THREE_DAY_STREAK_BADGE) }
            )
            BadgeItem(
                title = "Zen Master",
                icon = Icons.Default.SelfImprovement,
                onClick = { navController.navigate(Routes.ZEN_MASTER_BADGE) }
            )
            BadgeItem(
                title = "Sleep Guardian",
                icon = Icons.Default.Bedtime,
                onClick = { navController.navigate(Routes.SLEEP_GUARDIAN_BADGE) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Leaderboard
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

                Text(
                    text = "Leaderboard",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LeaderboardItem("1", "Sarah K.", "2,450 pts")
                LeaderboardItem("2", "You", "0 pts")
                LeaderboardItem("3", "Mike R.", "1,650 pts")
            }
        }
    }
}

@Composable
private fun ChallengeItem(
    title: String,
    subtitle: String,
    time: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(time, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BadgeItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF1F1F1), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 11.sp, color = Color.Black)
    }
}

@Composable
private fun LeaderboardItem(rank: String, name: String, points: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$rank  $name", color = Color.White)
        Text(points, color = Color.White)
    }
}
