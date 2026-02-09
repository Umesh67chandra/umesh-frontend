package com.example.focusguardian.ui.theme.screens

import android.app.usage.UsageStatsManager
import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.app.AppOpsManager
import kotlinx.coroutines.delay
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

private data class UsageRow(val label: String, val minutes: Int)

@Composable
fun TimeUsedScreen(navController: NavController) {
    val context = LocalContext.current
    val hasAccess = hasUsageAccess(context)

    var usageRows by remember { mutableStateOf<List<UsageRow>>(emptyList()) }

    LaunchedEffect(hasAccess) {
            if (hasAccess) {
                while (true) {
                    usageRows = loadUsageRows(context)
                    delay(30000)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                "Time Used",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(8.dp))

        if (!hasAccess) {
            Text(
                text = "Grant Usage Access to view time used.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                context.startActivity(android.content.Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }) {
                Text("Open Usage Access")
            }
            return
        }

        if (usageRows.isEmpty()) {
            Text(
                text = "No usage data available yet.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(usageRows) { row ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                row.label,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${row.minutes} min",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun hasUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private suspend fun loadUsageRows(context: Context): List<UsageRow> = withContext(Dispatchers.IO) {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startTime = calendar.timeInMillis
    val endTime = System.currentTimeMillis()

    val stats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_BEST,
        startTime,
        endTime
    )

    if (stats.isNullOrEmpty()) return@withContext emptyList()

    val pm = context.packageManager
    stats
        .filter { it.totalTimeInForeground > 0 }
        .mapNotNull { stat ->
            val label = try {
                val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (ex: Exception) {
                null
            }
            label?.let { UsageRow(it, (stat.totalTimeInForeground / 60000L).toInt()) }
        }
        .sortedByDescending { it.minutes }
}
