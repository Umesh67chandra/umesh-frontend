package com.example.focusguardian.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object SleepAlarmScheduler {
    fun scheduleDailyWakeAlarm(context: Context, wakeMinutes: Int, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context)

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val now = Calendar.getInstance()
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeMinutes / 60)
            set(Calendar.MINUTE, wakeMinutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (trigger.before(now)) {
            trigger.add(Calendar.DAY_OF_YEAR, 1)
        }

        val triggerAt = trigger.timeInMillis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
            return
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        } catch (ex: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        }
    }

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            1010,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
