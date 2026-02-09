package com.example.focusguardian.ui.theme.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.viewmodel.AnalyticsSnapshot
import com.example.focusguardian.viewmodel.AppUsageViewModel
import com.example.focusguardian.viewmodel.DailyUsage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavController,
    appUsageViewModel: AppUsageViewModel
) {
    val context = LocalContext.current
    var snapshot by remember { mutableStateOf<AnalyticsSnapshot?>(null) }
    var trend by remember { mutableStateOf<List<DailyUsage>>(emptyList()) }
    val hasUsageAccess = appUsageViewModel.hasUsageAccess(context)

    LaunchedEffect(hasUsageAccess) {
        if (!hasUsageAccess) return@LaunchedEffect
        while (true) {
            snapshot = appUsageViewModel.getAnalyticsSnapshot(context)
            trend = appUsageViewModel.getUsageTrend(context, days = 7)
            delay(60000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Text(
            text = "Advanced Analytics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Deep dive into your usage patterns and predictions",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasUsageAccess) {
            AnalyticsCard("Usage Access", "Grant access to view analytics.")
            OutlinedButton(onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }) {
                Text("Open Usage Access")
            }
            return@Column
        }

        val totalMinutes = snapshot?.totalMinutes ?: 0
        val lateNightMinutes = snapshot?.lateNightMinutes ?: 0
        val switchCount = snapshot?.switchCount ?: 0

        AnalyticsCard("Total Time Today", formatMinutes(totalMinutes))
        AnalyticsCard("Late Night Usage", formatMinutes(lateNightMinutes))
        AnalyticsCard("App Switches", "$switchCount switches")
        AnalyticsCard("Focus Difficulty", focusDifficulty(totalMinutes, switchCount))

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Top Apps",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                val topApps = snapshot?.topApps ?: emptyList()
                if (topApps.isEmpty()) {
                    Text(
                        "No usage data yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    topApps.forEach { app ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(app.label, modifier = Modifier.weight(1f))
                            Text(
                                formatMinutes(app.minutes),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "7-Day Trend",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (trend.isEmpty()) {
                    Text(
                        "No trend data yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                } else {
                    trend.forEach { day ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(day.label, modifier = Modifier.weight(1f))
                            Text(
                                formatMinutes(day.minutes),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}

private fun focusDifficulty(totalMinutes: Int, switchCount: Int): String {
    return when {
        totalMinutes < 60 && switchCount < 40 -> "Easy Day"
        totalMinutes < 180 && switchCount < 100 -> "Moderate"
        else -> "Hard Day"
    }
}
