package com.example.focusguardian.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

data class InstalledApp(val packageName: String, val label: String)

@Composable
fun ManageAppsScreen(appUsageViewModel: AppUsageViewModel) {
    val context = LocalContext.current
    val installedApps = remember { getInstalledApps(context) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedAppLabel by remember { mutableStateOf<String?>(null) }
    var selectedAppPackage by remember { mutableStateOf<String?>(null) }

    if (showDialog && selectedAppLabel != null && selectedAppPackage != null) {
        SetDailyLimitDialog(
            appName = selectedAppLabel!!,
            onDismiss = { showDialog = false },
            onSave = { hours, minutes ->
                val limitInMinutes = hours * 60 + minutes
                appUsageViewModel.appLimits[selectedAppPackage!!] = AppUsageInfo(
                    packageName = selectedAppPackage!!,
                    label = selectedAppLabel!!,
                    usageLimitInMinutes = limitInMinutes,
                    timeUsedInMinutes = 0
                )
                appUsageViewModel.saveLimits(context)
                showDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        appUsageViewModel.loadLimits(context)
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(installedApps) { app ->
                AppItem(
                    appName = app.label,
                    usageInfo = appUsageViewModel.appLimits[app.packageName],
                    onToggle = { enabled ->
                        if (enabled) {
                            selectedAppLabel = app.label
                            selectedAppPackage = app.packageName
                            showDialog = true
                        } else {
                            appUsageViewModel.appLimits.remove(app.packageName)
                            appUsageViewModel.saveLimits(context)
                        }
                    }
                )
            }
        }
    }
}

private fun getInstalledApps(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
        addCategory(android.content.Intent.CATEGORY_LAUNCHER)
    }
    val activities = pm.queryIntentActivities(intent, 0)
    val allApps = activities
        .map { resolveInfo ->
            val appInfo = resolveInfo.activityInfo.applicationInfo
            InstalledApp(
                packageName = appInfo.packageName,
                label = pm.getApplicationLabel(appInfo).toString()
            )
        }
        .distinctBy { it.packageName }

    val socialPackages = listOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.lite",
        "com.whatsapp",
        "com.snapchat.android",
        "com.twitter.android",
        "com.twitter.android.lite",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill",
        "com.google.android.youtube",
        "com.google.android.apps.youtube.music",
        "com.linkedin.android",
        "com.reddit.frontpage",
        "com.discord",
        "com.pinterest",
        "com.spotify.music",
        "org.telegram.messenger",
        "com.google.android.apps.messaging"
    )

    val socialApps = allApps.filter { app ->
        socialPackages.any { pkg -> app.packageName == pkg }
    }

    if (socialApps.isNotEmpty()) {
        return socialApps.sortedBy { it.label.lowercase() }
    }

    // Fallback: show user-installed apps (exclude system apps)
    return allApps.filter { app ->
        val appInfo = pm.getApplicationInfo(app.packageName, 0)
        (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0
    }.sortedBy { it.label.lowercase() }
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
