package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusguardian.viewmodel.AppUsageViewModel

data class AppUsageInfo(
    val packageName: String,
    val label: String,
    val usageLimitInMinutes: Int,
    val timeUsedInMinutes: Int
)

@Composable
fun ManageAppsScreen(appUsageViewModel: AppUsageViewModel) {

    var showDialog by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf<String?>(null) }

    if (showDialog && selectedApp != null) {
        SetDailyLimitDialog(
            appName = selectedApp!!,
            onDismiss = { showDialog = false },
            onSave = { hours, minutes ->
                val limitInMinutes = hours * 60 + minutes
                // For now, using the app name as packageName since we don't have real package names here
                appUsageViewModel.appLimits[selectedApp!!] = AppUsageInfo(
                    packageName = selectedApp!!,
                    label = selectedApp!!,
                    usageLimitInMinutes = limitInMinutes,
                    timeUsedInMinutes = 0
                ) 
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        Text(
            text = "${appUsageViewModel.appLimits.size} apps tracked",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(
            text = "Connect apps and set daily limits",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        val apps = listOf("Instagram", "YouTube", "TikTok", "Facebook", "Twitter", "Snapchat", "Reddit", "LinkedIn")

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps) {
                AppItem(
                    appName = it,
                    usageInfo = appUsageViewModel.appLimits[it],
                    onToggle = { enabled ->
                        if (enabled) {
                            selectedApp = it
                            showDialog = true
                        } else {
                            appUsageViewModel.appLimits.remove(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AppItem(
    appName: String,
    usageInfo: AppUsageInfo?,
    onToggle: (Boolean) -> Unit
) {

    val isEnabled = usageInfo != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = appName, color = MaterialTheme.colorScheme.onSurface)
                if (isEnabled) {
                    val limitHours = usageInfo!!.usageLimitInMinutes / 60
                    val limitMinutes = usageInfo.usageLimitInMinutes % 60
                    val usedHours = usageInfo.timeUsedInMinutes / 60
                    val usedMinutes = usageInfo.timeUsedInMinutes % 60

                    Text(
                        text = "${usedHours}h ${usedMinutes}m / ${limitHours}h ${limitMinutes}m limit",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Not connected",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}
