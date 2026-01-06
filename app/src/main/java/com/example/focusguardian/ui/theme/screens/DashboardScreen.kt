
package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.navigation.Routes
import com.example.focusguardian.viewmodel.AppUsageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, appUsageViewModel: AppUsageViewModel) {
    val totalDailyLimitMinutes = appUsageViewModel.totalDailyLimitMinutes
    val totalTimeUsedMinutes = appUsageViewModel.totalTimeUsedMinutes

    val dailyLimitHours = totalDailyLimitMinutes / 60
    val dailyLimitMinutes = totalDailyLimitMinutes % 60

    val timeUsedHours = totalTimeUsedMinutes / 60
    val timeUsedMinutes = totalTimeUsedMinutes % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.PROFILE_SETTINGS) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF7F8FC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text("Welcome back,", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                "Here’s your daily digital wellness overview",
                fontSize = 13.sp,
                color = Color.Black // Changed from Gray to Black for better visibility
            )

            Spacer(Modifier.height(16.dp))

            // ---- Stats cards ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(Icons.Default.Schedule, "${dailyLimitHours}h ${dailyLimitMinutes}m", "Daily Limit", Color(0xFF4C6EF5))
                DashboardCard(Icons.Default.TrendingUp, "${timeUsedHours}h ${timeUsedMinutes}m", "Time Used", Color(0xFF2ECC71))
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    icon = Icons.Default.Notifications,
                    value = "3",
                    label = "Smart Alerts",
                    iconBackgroundColor = Color(0xFF9B59B6),
                    onClick = { navController.navigate(Routes.NOTIFICATIONS) }
                )
                DashboardCard(Icons.Default.Settings, appUsageViewModel.appLimits.size.toString(), "Apps Tracked", Color(0xFFF39C12)) {
                    navController.navigate(Routes.MANAGE_APPS)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Quick Access", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)

            Spacer(Modifier.height(12.dp))

            QuickAccessItem(
                icon = Icons.Default.ShowChart,
                title = "Addiction Score",
                subtitle = "Check your digital health"
            ) {
                navController.navigate(Routes.ADDICTION_SCORE)
            }

            QuickAccessItem(
                icon = Icons.Default.Bedtime,
                title = "Sleep Cycle",
                subtitle = "Manage bedtime routine"
            ) {
                navController.navigate(Routes.SLEEP)
            }

            QuickAccessItem(
                icon = Icons.Default.EmojiEvents,
                title = "Challenges",
                subtitle = "Gamify your focus"
            ) {
                navController.navigate(Routes.CHALLENGES)
            }

            QuickAccessItem(
                icon = Icons.Default.BarChart,
                title = "Analytics",
                subtitle = "Deep dive into data"
            ) {
                navController.navigate(Routes.ANALYTICS)
            }
        }
    }
}

@Composable
private fun RowScope.DashboardCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconBackgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun QuickAccessItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray)
        }
    }
}
