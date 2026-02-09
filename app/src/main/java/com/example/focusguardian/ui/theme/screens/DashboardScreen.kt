
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.focusguardian.navigation.Routes
import com.example.focusguardian.viewmodel.AppUsageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, appUsageViewModel: AppUsageViewModel) {
    val context = LocalContext.current
    val hasUsageAccess = appUsageViewModel.hasUsageAccess(context)

    LaunchedEffect(Unit) {
        appUsageViewModel.loadAlerts(context)
    }

    LaunchedEffect(hasUsageAccess) {
        while (true) {
            appUsageViewModel.refreshUsageTimes(context)
            delay(30000)
        }
    }
    val totalDailyLimitMinutes = appUsageViewModel.totalDailyLimitMinutes
    val totalTimeUsedMinutes = appUsageViewModel.totalTimeUsedMinutes

    val dailyLimitHours = totalDailyLimitMinutes / 60
    val dailyLimitMinutes = totalDailyLimitMinutes % 60

    val timeUsedHours = totalTimeUsedMinutes / 60
    val timeUsedMinutes = totalTimeUsedMinutes % 60

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(Routes.PROFILE_SETTINGS) }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Welcome back,",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Here’s your daily digital wellness overview",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // ---- Stats cards ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    Icons.Default.Schedule,
                    "${dailyLimitHours}h ${dailyLimitMinutes}m",
                    "Daily Limit",
                    Color(0xFF4C6EF5)
                ) {
                    navController.navigate(Routes.MANAGE_APPS)
                }
                DashboardCard(
                    Icons.Default.TrendingUp,
                    "${timeUsedHours}h ${timeUsedMinutes}m",
                    "Time Used",
                    Color(0xFF2ECC71)
                ) {
                    if (!hasUsageAccess) {
                        context.startActivity(
                            android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        )
                    } else {
                        navController.navigate(Routes.TIME_USED)
                    }
                }
            }

            if (!hasUsageAccess) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Grant Usage Access to show Time Used",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    icon = Icons.Default.Notifications,
                    value = appUsageViewModel.smartAlerts.size.toString(),
                    label = "Smart Alerts",
                    iconBackgroundColor = Color(0xFF9B59B6),
                    onClick = { navController.navigate(Routes.NOTIFICATIONS) }
                )
                DashboardCard(Icons.Default.Settings, appUsageViewModel.appLimits.size.toString(), "Apps Tracked", Color(0xFFF39C12)) {
                    navController.navigate(Routes.MANAGE_APPS)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Quick Access",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
            Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
