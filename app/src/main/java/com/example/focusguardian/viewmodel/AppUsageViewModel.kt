package com.example.focusguardian.viewmodel

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.os.Process
import java.util.Calendar
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.focusguardian.ui.theme.screens.AppUsageInfo
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppUsageViewModel : ViewModel() {
    val appLimits = mutableStateMapOf<String, AppUsageInfo>()
    val smartAlerts = mutableStateListOf<SmartAlert>()

    val totalDailyLimitMinutes: Int
        get() = appLimits.values.sumOf { it.usageLimitInMinutes }

    val totalTimeUsedMinutes: Int
        get() = appLimits.values.sumOf { it.timeUsedInMinutes }

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
            addAlertIfMissing(
                context = context,
                type = "USAGE_ACCESS",
                appLabel = null,
                message = "Usage access is required to track time used."
            )
            return
        }

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
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

        if (stats.isNullOrEmpty()) return

        val statsMap = stats.associateBy { it.packageName }
        appLimits.entries.forEach { entry ->
            val usage = statsMap[entry.key]
            val minutes = ((usage?.totalTimeInForeground ?: 0L) / 60000L).toInt()
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

        fun sumUsage(start: Long, end: Long): Int {
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                start,
                end
            )
            if (stats.isNullOrEmpty()) return 0
            return (stats.sumOf { it.totalTimeInForeground } / 60000L).toInt()
        }

        val totalUsageMinutes = sumUsage(startDay, now)

        // Late night usage: 10 PM - 12 AM and 12 AM - 6 AM
        val lateNightMinutes = run {
            val midnight = startDay
            val sixAm = midnight + 6 * 60 * 60 * 1000L
            val tenPm = midnight + 22 * 60 * 60 * 1000L
            var total = 0
            if (now > midnight) {
                total += sumUsage(midnight, minOf(now, sixAm))
            }
            if (now > tenPm) {
                total += sumUsage(tenPm, now)
            }
            total
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

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val baselineLimit = if (totalDailyLimitMinutes > 0) totalDailyLimitMinutes else 180
        val trend = mutableListOf<DailyUsage>()

        for (i in 0 until days) {
            val dayStart = cal.timeInMillis - i * 24L * 60L * 60L * 1000L
            val dayEnd = dayStart + 24L * 60L * 60L * 1000L
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                dayStart,
                dayEnd
            )
            val totalMinutes = if (stats.isNullOrEmpty()) 0 else (stats.sumOf { it.totalTimeInForeground } / 60000L).toInt()
            val percent = percent(totalMinutes, baselineLimit)
            val label = buildDayLabel(dayStart)
            trend.add(0, DailyUsage(label = label, minutes = totalMinutes, percent = percent))
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

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startDay,
            now
        )

        val totalMinutes = if (stats.isNullOrEmpty()) {
            0
        } else {
            (stats.sumOf { it.totalTimeInForeground } / 60000L).toInt()
        }

        val lateNightMinutes = run {
            val midnight = startDay
            val sixAm = midnight + 6 * 60 * 60 * 1000L
            val tenPm = midnight + 22 * 60 * 60 * 1000L
            var total = 0
            if (now > midnight) {
                val statsEarly = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    midnight,
                    minOf(now, sixAm)
                )
                total += if (statsEarly.isNullOrEmpty()) {
                    0
                } else {
                    (statsEarly.sumOf { it.totalTimeInForeground } / 60000L).toInt()
                }
            }
            if (now > tenPm) {
                val statsLate = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    tenPm,
                    now
                )
                total += if (statsLate.isNullOrEmpty()) {
                    0
                } else {
                    (statsLate.sumOf { it.totalTimeInForeground } / 60000L).toInt()
                }
            }
            total
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

        val topApps = if (stats.isNullOrEmpty()) {
            emptyList()
        } else {
            stats
                .filter { it.totalTimeInForeground > 0L }
                .sortedByDescending { it.totalTimeInForeground }
                .take(topN)
                .map { usage ->
                    AnalyticsAppUsage(
                        label = appLabelFor(context, usage.packageName),
                        minutes = (usage.totalTimeInForeground / 60000L).toInt()
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
                    appLabel = obj.optString("appLabel", null)
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

    private fun buildAlertKey(type: String, appLabel: String?): String {
        val cal = Calendar.getInstance()
        val dayKey = "${cal.get(Calendar.YEAR)}${cal.get(Calendar.MONTH) + 1}${cal.get(Calendar.DAY_OF_MONTH)}"
        return listOf(type, appLabel ?: "global", dayKey).joinToString("_")
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
    val topApps: List<AnalyticsAppUsage>
)

data class AnalyticsAppUsage(
    val label: String,
    val minutes: Int
)
