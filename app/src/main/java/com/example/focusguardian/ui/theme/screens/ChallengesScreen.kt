package com.example.focusguardian.ui.theme.screens

import android.content.Context
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.data.remote.ApiClient
import com.example.focusguardian.data.remote.LeaderboardEntry
import com.example.focusguardian.navigation.Routes
import java.util.Calendar

@Composable
fun ChallengesScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("focus_guardian_challenges", Context.MODE_PRIVATE) }

    var leaderboard by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }

    var completedIds by remember { mutableStateOf(loadCompletedIds(prefs)) }
    var streakCount by remember { mutableStateOf(prefs.getInt("streakCount", 0)) }
    var lastCompletedDay by remember { mutableStateOf(prefs.getInt("lastCompletedDay", 0)) }

    LaunchedEffect(Unit) {
        val response = ApiClient.apiService.getLeaderboard()
        if (response.isSuccessful) {
            leaderboard = response.body()?.items ?: emptyList()
        }
    }

    fun saveState() {
        prefs.edit()
            .putStringSet("completedIds", completedIds)
            .putInt("streakCount", streakCount)
            .putInt("lastCompletedDay", lastCompletedDay)
            .apply()
    }

    fun markCompleted(id: String) {
        val today = todayKey()
        if (!completedIds.contains(id)) {
            completedIds = completedIds + id
        }
        streakCount = when {
            lastCompletedDay == today -> streakCount
            lastCompletedDay == yesterdayKey() -> streakCount + 1
            else -> 1
        }
        lastCompletedDay = today
        saveState()
    }

    val earnedFirstStep = completedIds.isNotEmpty()
    val earnedThreeDay = streakCount >= 3
    val earnedSevenDay = streakCount >= 7

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🔹 Header
        Text(
            text = "Challenges & Rewards",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Gamify your digital wellness journey",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
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
                    isCompleted = completedIds.contains("no_scroll"),
                    onStart = { navController.navigate(Routes.NO_SCROLL) },
                    onComplete = { markCompleted("no_scroll") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ 1-Hour Deep Focus
                ChallengeItem(
                    title = "1-Hour Deep Focus Challenge",
                    subtitle = "Complete focused work without any app usage",
                    time = "1h",
                    isCompleted = completedIds.contains("deep_focus"),
                    onStart = { navController.navigate(Routes.DEEP_FOCUS) },
                    onComplete = { markCompleted("deep_focus") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Early Sleep
                ChallengeItem(
                    title = "Early Sleep Challenge",
                    subtitle = "Go to bed before your scheduled bedtime",
                    time = "8h",
                    isCompleted = completedIds.contains("early_sleep"),
                    onStart = { navController.navigate(Routes.EARLY_SLEEP) },
                    onComplete = { markCompleted("early_sleep") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Recent Achievements
        Text(
            text = "Recent Achievements",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (completedIds.isEmpty()) {
            Text(
                text = "No completed challenges yet. Start one above!",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Streak: $streakCount day(s)",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Badge Collection
        Text(
            text = "Badge Collection",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BadgeItem(
                title = "First Step",
                icon = Icons.Default.Done,
                earned = earnedFirstStep,
                onClick = { navController.navigate(Routes.FIRST_STEP_BADGE) }
            )
            BadgeItem(
                title = "3-Day Streak",
                icon = Icons.Default.Leaderboard,
                earned = earnedThreeDay,
                onClick = { navController.navigate(Routes.THREE_DAY_STREAK_BADGE) }
            )
            BadgeItem(
                title = "Zen Master",
                icon = Icons.Default.SelfImprovement,
                earned = earnedSevenDay,
                onClick = { navController.navigate(Routes.ZEN_MASTER_BADGE) }
            )
            BadgeItem(
                title = "Sleep Guardian",
                icon = Icons.Default.Bedtime,
                earned = earnedSevenDay,
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
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
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

                if (leaderboard.isEmpty()) {
                    LeaderboardItem("-", "No data", "-")
                } else {
                    leaderboard.forEach { entry ->
                        LeaderboardItem(
                            entry.rank.toString(),
                            entry.name,
                            "${entry.points} pts"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeItem(
    title: String,
    subtitle: String,
    time: String,
    isCompleted: Boolean,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(time, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onStart,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Start")
                }
                Button(
                    onClick = onComplete,
                    enabled = !isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(if (isCompleted) "Completed" else "Complete")
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(title: String, icon: ImageVector, earned: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (earned) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (earned) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun loadCompletedIds(prefs: android.content.SharedPreferences): Set<String> {
    return prefs.getStringSet("completedIds", emptySet()) ?: emptySet()
}

private fun todayKey(): Int {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH)
}

private fun yesterdayKey(): Int {
    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH)
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
