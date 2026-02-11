package com.example.focusguardian.viewmodel

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStats
import android.app.usage.UsageEvents
import android.content.Context
import android.os.Process
import java.util.Calendar
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.focusguardian.ui.theme.screens.AppUsageInfo
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppUsageViewModel : ViewModel() {
    val appLimits = mutableStateMapOf<String, AppUsageInfo>()
    val smartAlerts = mutableStateListOf<SmartAlert>()
    var totalDeviceUsageMinutes by mutableStateOf(0)

    val totalDailyLimitMinutes: Int
        get() = appLimits.values.sumOf { it.usageLimitInMinutes }

    val totalTimeUsedMinutes: Int
        get() = totalDeviceUsageMinutes

    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun refreshUsageTimes(context: Context) {
        if (!hasUsageAccess(context)) {
            totalDeviceUsageMinutes = 0
            appLimits.entries.forEach { entry ->
                appLimits[entry.key] = entry.value.copy(timeUsedInMinutes = 0)
            }

            addAlertIfMissing(
                context = context,
                type = "USAGE_ACCESS",
                appLabel = null,
                message = "Usage access is required to track time used."
            )
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        // Calculate usage from events manually for precision
        val usageByPackageMap = calculateUsageFromEvents(context, startTime, endTime)
        
        if (usageByPackageMap.isEmpty()) {
            totalDeviceUsageMinutes = 0
            return
        }

        totalDeviceUsageMinutes = (usageByPackageMap.values.sum() / 60000L).toInt()
        
        // Update limits/alerts based on this data
        appLimits.entries.forEach { entry ->
            val timeInMillis = usageByPackageMap[entry.key] ?: 0L
            val minutes = (timeInMillis / 60000L).toInt()
            appLimits[entry.key] = entry.value.copy(timeUsedInMinutes = minutes)

            val limit = entry.value.usageLimitInMinutes
            if (limit > 0 && minutes > limit) {
                addAlertIfMissing(
                    context = context,
                    type = "LIMIT_EXCEEDED",
                    appLabel = entry.value.label,
                    message = "You exceeded your daily limit on ${entry.value.label}."
                )
            }
        }

        if (appLimits.isEmpty()) {
            addAlertIfMissing(
                context = context,
                type = "NO_LIMITS",
                appLabel = null,
                message = "Set daily limits to receive smart alerts."
            )
        }
    }

    suspend fun getUsageByPackage(context: Context): Map<String, Int> = withContext(Dispatchers.Default) {
        if (!hasUsageAccess(context)) return@withContext emptyMap()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val rawUsageMap = calculateUsageFromEvents(context, startTime, endTime)
        
        val usageByPackageMap = rawUsageMap.mapValues { (_, duration) ->
            (duration / 60000L).toInt()
        }
        
        usageByPackageMap
    }

    suspend fun getAddictionMetrics(context: Context): AddictionMetrics? = withContext(Dispatchers.Default) {
        if (!hasUsageAccess(context)) return@withContext null

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startDay = calendar.timeInMillis
        val now = System.currentTimeMillis()

        // Use the shared event-based calculation
        val usageMap = calculateUsageFromEvents(context, startDay, now)
        val totalUsageMinutes = if (usageMap.isEmpty()) 0 else (usageMap.values.sum() / 60000L).toInt()

        // Late night usage: 10 PM - 12 AM and 12 AM - 6 AM
        val lateNightMinutes = run {
            val midnight = startDay
            val sixAm = midnight + 6 * 60 * 60 * 1000L
            val tenPm = midnight + 22 * 60 * 60 * 1000L
            var total = 0L

            if (now > midnight) {
                // Calculate specifically for the early morning window
                val earlyMap = calculateUsageFromEvents(context, midnight, minOf(now, sixAm))
                total += earlyMap.values.sum()
            }
            if (now > tenPm) {
                // Calculate for the late night window
                val lateMap = calculateUsageFromEvents(context, tenPm, now)
                total += lateMap.values.sum()
            }
            (total / 60000L).toInt()
        }

        val usageEvents = usageStatsManager.queryEvents(startDay, now)
        var switchCount = 0
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                switchCount += 1
            }
        }

        val limitMinutes = totalDailyLimitMinutes
        val baselineLimit = if (limitMinutes > 0) limitMinutes else 180

        val scrollingPercent = percent(totalUsageMinutes, baselineLimit)
        val lateNightPercent = percent(lateNightMinutes, 90)
        val switchingPercent = percent(switchCount, 120)
        val moodDropPercent = ((lateNightPercent * 0.6) + (switchingPercent * 0.4)).toInt().coerceIn(0, 100)

        val usageRatio = (totalUsageMinutes.toFloat() / baselineLimit.toFloat()).coerceIn(0f, 2f)
        val usageScore = (usageRatio * 50f).toInt() // 0-100 scaled, hits 100 at 2x limit

        val score = (
            usageScore * 0.6 +
            lateNightPercent * 0.2 +
            switchingPercent * 0.15 +
            moodDropPercent * 0.05
        ).toInt().coerceIn(0, 100)

        AddictionMetrics(
            scorePercent = score,
            scrollingPercent = scrollingPercent,
            lateNightPercent = lateNightPercent,
            switchingPercent = switchingPercent,
            moodDropPercent = moodDropPercent
        )
    }

    suspend fun getUsageTrend(context: Context, days: Int = 7): List<DailyUsage> = withContext(Dispatchers.Default) {
        if (!hasUsageAccess(context)) return@withContext emptyList()

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val baselineLimit = if (totalDailyLimitMinutes > 0) totalDailyLimitMinutes else 180
        val trend = mutableListOf<DailyUsage>()

        for (i in days - 1 downTo 0) {
            val dayStart = cal.timeInMillis - i * 24L * 60L * 60L * 1000L
            val dayEnd = dayStart + 24L * 60L * 60L * 1000L
            // calculateUsageFromEvents now handles precision
            val usageMap = calculateUsageFromEvents(context, dayStart, dayEnd)
            val totalMinutes = if (usageMap.isEmpty()) 0 else (usageMap.values.sum() / 60000L).toInt()
            val percent = percent(totalMinutes, baselineLimit)
            val label = buildDayLabel(dayStart)
            trend.add(DailyUsage(label = label, minutes = totalMinutes, percent = percent))
        }

        trend
    }

    suspend fun getAnalyticsSnapshot(
        context: Context,
        topN: Int = 5
    ): AnalyticsSnapshot? = withContext(Dispatchers.Default) {
        if (!hasUsageAccess(context)) return@withContext null

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startDay = calendar.timeInMillis
        val now = System.currentTimeMillis()

        val usageMap = calculateUsageFromEvents(context, startDay, now)

        val totalMinutes = if (usageMap.isEmpty()) {
            0
        } else {
            (usageMap.values.sum() / 60000L).toInt()
        }

        // Re-use logic for consistency
        val lateNightMinutes = run {
            val midnight = startDay
            val sixAm = midnight + 6 * 60 * 60 * 1000L
            val tenPm = midnight + 22 * 60 * 60 * 1000L
            var total = 0L
            if (now > midnight) {
                val usageEarly = calculateUsageFromEvents(context, midnight, minOf(now, sixAm))
                 total += usageEarly.values.sum()
            }
            if (now > tenPm) {
                val usageLate = calculateUsageFromEvents(context, tenPm, now)
                total += usageLate.values.sum()
            }
            (total / 60000L).toInt()
        }

        val usageEvents = usageStatsManager.queryEvents(startDay, now)
        var switchCount = 0
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                switchCount += 1
            }
        }

        val topApps = if (usageMap.isEmpty()) {
            emptyList()
        } else {
            usageMap.entries
                .filter { it.value > 0L }
                .sortedByDescending { it.value }
                .take(topN)
                .map { entry ->
                    val pkg = entry.key
                    val mins = (entry.value / 60000L).toInt()
                    val label: String = appLabelFor(context, pkg)
                    AnalyticsAppUsageInfo(
                        label = label,
                        minutes = mins
                    )
                }
        }

        AnalyticsSnapshot(
            totalMinutes = totalMinutes,
            lateNightMinutes = lateNightMinutes,
            switchCount = switchCount,
            topApps = topApps
        )
    }

    // ... (Keep other methods like loadLimits, saveLimits etc unchanged) ...
    // Note: I will only replace the methods I'm changing.
    // However, since I need to replace calculateUsageFromEvents which is at the bottom,
    // and potentially others, I'll essentially rewrite the logic block.
    // Wait, the tool requires a contiguous block.
    // getAddictionMetrics starts at 125.
    // calculateUsageFromEvents ends at 433.
    // I can replace from 125 to 433.

    fun loadLimits(context: Context) {
        val prefs = context.getSharedPreferences("focus_guardian_limits", Context.MODE_PRIVATE)
        val json = prefs.getString("limits", null) ?: return
        val array = JSONArray(json)
        appLimits.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val packageName = obj.getString("packageName")
            appLimits[packageName] = AppUsageInfo(
                packageName = packageName,
                label = obj.getString("label"),
                usageLimitInMinutes = obj.getInt("usageLimitInMinutes"),
                timeUsedInMinutes = obj.getInt("timeUsedInMinutes")
            )
        }
    }

    fun loadAlerts(context: Context) {
        val prefs = context.getSharedPreferences("focus_guardian_alerts", Context.MODE_PRIVATE)
        val json = prefs.getString("alerts", null) ?: return
        val array = JSONArray(json)
        smartAlerts.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            smartAlerts.add(
                SmartAlert(
                    id = obj.getString("id"),
                    type = obj.getString("type"),
                    message = obj.getString("message"),
                    timestamp = obj.getLong("timestamp"),
                    appLabel = if (obj.has("appLabel") && !obj.isNull("appLabel")) obj.getString("appLabel") else null
                )
            )
        }
    }

    fun saveLimits(context: Context) {
        val prefs = context.getSharedPreferences("focus_guardian_limits", Context.MODE_PRIVATE)
        val array = JSONArray()
        appLimits.values.forEach { info ->
            array.put(
                JSONObject()
                    .put("packageName", info.packageName)
                    .put("label", info.label)
                    .put("usageLimitInMinutes", info.usageLimitInMinutes)
                    .put("timeUsedInMinutes", info.timeUsedInMinutes)
            )
        }
        prefs.edit().putString("limits", array.toString()).apply()
    }

    private fun saveAlerts(context: Context) {
        val prefs = context.getSharedPreferences("focus_guardian_alerts", Context.MODE_PRIVATE)
        val array = JSONArray()
        smartAlerts.forEach { alert ->
            array.put(
                JSONObject()
                    .put("id", alert.id)
                    .put("type", alert.type)
                    .put("message", alert.message)
                    .put("timestamp", alert.timestamp)
                    .put("appLabel", alert.appLabel)
            )
        }
        prefs.edit().putString("alerts", array.toString()).apply()
    }

    private fun addAlertIfMissing(
        context: Context,
        type: String,
        appLabel: String?,
        message: String
    ) {
        val key = buildAlertKey(type, appLabel)
        val exists = smartAlerts.any { it.id == key }
        if (exists) return

        smartAlerts.add(
            0,
            SmartAlert(
                id = key,
                type = type,
                message = message,
                timestamp = System.currentTimeMillis(),
                appLabel = appLabel
            )
        )
        saveAlerts(context)
    }

    fun recordUserDailyUsage(context: Context, userId: String, minutes: Int = totalDeviceUsageMinutes) {
        val prefs = context.getSharedPreferences("focus_guardian_user_usage", Context.MODE_PRIVATE)
        val key = "${userId}_${buildDayKey()}"
        prefs.edit().putInt(key, minutes).apply()
    }

    private fun calculateUsageFromEvents(context: Context, startTime: Long, endTime: Long): Map<String, Long> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        
        val usageMap = mutableMapOf<String, Long>()
        val openEvents = mutableMapOf<String, Long>()
        val knownPackages = mutableSetOf<String>()

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val pkg = event.packageName
            val time = event.timeStamp

            // Filter out system/launcher apps early to save processing but
            // keep logic inside the loop or filter result later.
            // Better to filter result later to avoid missing paired events if filter logic changes.

            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                openEvents[pkg] = time
                knownPackages.add(pkg)
            } else if (event.eventType == UsageEvents.Event.ACTIVITY_PAUSED || 
                       event.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                
                if (openEvents.containsKey(pkg)) {
                    val start = openEvents.remove(pkg)!!
                    val duration = time - start
                    usageMap[pkg] = (usageMap[pkg] ?: 0L) + duration
                    knownPackages.add(pkg)
                } else {
                    // Ends without a known start in this window.
                    // Only count this if we HAVEN'T seen this package yet in this window.
                    // If we have seen it (knownPackages contains pkg), it means we already processed
                    // a session or a close for it, so this is likely a redundant STOP after PAUSE.
                    if (!knownPackages.contains(pkg)) {
                        // Assume it started at 'startTime'
                        val duration = time - startTime
                        if (duration > 0) {
                             usageMap[pkg] = (usageMap[pkg] ?: 0L) + duration
                        }
                        knownPackages.add(pkg)
                    }
                }
            }
        }

        // Handle open events that haven't closed yet (still running)
        for ((pkg, start) in openEvents) {
            val duration = endTime - start
            if (duration > 0) {
                usageMap[pkg] = (usageMap[pkg] ?: 0L) + duration
            }
        }

        // Now filter the result
        val finalMap = mutableMapOf<String, Long>()
        val allLaunchers = getAllLaunchers(context)

        for ((pkg, duration) in usageMap) {
             if (allLaunchers.contains(pkg)) continue
             // Skip if no launch intent
             if (!hasLaunchIntent(context, pkg)) continue
             
             if (duration > 0) {
                 finalMap[pkg] = duration
             }
        }
        
        return finalMap
    }

    private fun buildAlertKey(type: String, appLabel: String?): String {
        return listOf(type, appLabel ?: "global", buildDayKey()).joinToString("_")
    }



    private fun buildDayKey(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}${cal.get(Calendar.MONTH) + 1}${cal.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun percent(value: Int, max: Int): Int {
        if (max <= 0) return 0
        return ((value.toFloat() / max) * 100f).toInt().coerceIn(0, 100)
    }

    private fun buildDayLabel(dayStartMillis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = dayStartMillis }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH) + 1
        return "$day/$month"
    }

    private fun appLabelFor(context: Context, packageName: String): String {
        val pm = context.packageManager
        return try {
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (ex: Exception) {
            packageName
        }
    }

    private fun getAllLaunchers(context: Context): List<String> {
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN)
        intent.addCategory(android.content.Intent.CATEGORY_HOME)
        val resolveInfos = context.packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfos.map { it.activityInfo.packageName }
    }

    private fun hasLaunchIntent(context: Context, packageName: String): Boolean {
        return context.packageManager.getLaunchIntentForPackage(packageName) != null
    }
}

data class SmartAlert(
    val id: String,
    val type: String,
    val message: String,
    val timestamp: Long,
    val appLabel: String?
)

data class AddictionMetrics(
    val scorePercent: Int,
    val scrollingPercent: Int,
    val lateNightPercent: Int,
    val switchingPercent: Int,
    val moodDropPercent: Int
)

data class DailyUsage(
    val label: String,
    val minutes: Int,
    val percent: Int
)

data class AnalyticsSnapshot(
    val totalMinutes: Int,
    val lateNightMinutes: Int,
    val switchCount: Int,
    val topApps: List<AnalyticsAppUsageInfo>
)

data class AnalyticsAppUsageInfo(
    val label: String,
    val minutes: Int
)