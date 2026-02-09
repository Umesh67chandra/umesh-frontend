package com.example.focusguardian.ui.theme.screens

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.service.SleepAlarmScheduler
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepCycleScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("focus_guardian_sleep", Context.MODE_PRIVATE) }

    var isEnabled by remember { mutableStateOf(prefs.getBoolean("enabled", true)) }
    var bedtimeMinutes by remember { mutableStateOf(prefs.getInt("bedtimeMinutes", 23 * 60)) }
    var wakeMinutes by remember { mutableStateOf(prefs.getInt("wakeMinutes", 7 * 60)) }

    fun savePrefs() {
        prefs.edit()
            .putBoolean("enabled", isEnabled)
            .putInt("bedtimeMinutes", bedtimeMinutes)
            .putInt("wakeMinutes", wakeMinutes)
            .apply()

        SleepAlarmScheduler.scheduleDailyWakeAlarm(
            context = context,
            wakeMinutes = wakeMinutes,
            enabled = isEnabled
        )
    }

    LaunchedEffect(Unit) {
        SleepAlarmScheduler.scheduleDailyWakeAlarm(
            context = context,
            wakeMinutes = wakeMinutes,
            enabled = isEnabled
        )
    }

    fun showTimePicker(initialMinutes: Int, onTimeSelected: (Int) -> Unit) {
        val hour = initialMinutes / 60
        val minute = initialMinutes % 60
        TimePickerDialog(
            context,
            { _, h, m ->
                onTimeSelected(h * 60 + m)
                savePrefs()
            },
            hour,
            minute,
            false
        ).show()
    }

    val bedtimeLabel = formatTime(bedtimeMinutes)
    val wakeLabel = formatTime(wakeMinutes)
    val lockStartMinutes = (bedtimeMinutes - 60 + 24 * 60) % (24 * 60)
    val lockStartLabel = formatTime(lockStartMinutes)
    val blueLightPercent = if (isEnabled && isInWindow(lockStartMinutes, wakeMinutes)) 0.7f else 0.2f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Sleep Cycle Mode",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Optimize your sleep schedule and reduce blue light exposure",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sleep Cycle Block
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sleep Cycle Block",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = {
                            isEnabled = it
                            savePrefs()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimeCard("Bedtime", bedtimeLabel) {
                        showTimePicker(bedtimeMinutes) { bedtimeMinutes = it }
                    }
                    TimeCard("Wake Up", wakeLabel) {
                        showTimePicker(wakeMinutes) { wakeMinutes = it }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Social apps lock 1 hour before bedtime (starts at $lockStartLabel).",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Blue Light Exposure ${(blueLightPercent * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                LinearProgressIndicator(
                    progress = { blueLightPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sleep Schedule
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Sleep Schedule",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$bedtimeLabel - $wakeLabel",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Morning Tips
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Morning Routine Tips",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "1. Avoid checking phone for first 30 mins",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "2. Drink water immediately after waking",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "3. Get natural sunlight exposure",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Evening Wind-down
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Evening Wind-down",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Blue light from screens suppresses melatonin production. Enable Auto-lock to help your brain prepare for sleep.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Melatonin Level  •  Rising",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { 0.7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimeCard(label: String, time: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(time, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun TimeCard(label: String, time: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(140.dp)
            .heightIn(min = 64.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(time, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

private fun formatTime(minutes: Int): String {
    val hour24 = (minutes / 60) % 24
    val minute = minutes % 60
    val isPm = hour24 >= 12
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val minuteStr = minute.toString().padStart(2, '0')
    val suffix = if (isPm) "PM" else "AM"
    return "$hour12:$minuteStr $suffix"
}

private fun isInWindow(startMinutes: Int, endMinutes: Int): Boolean {
    val now = Calendar.getInstance()
    val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    return if (startMinutes <= endMinutes) {
        currentMinutes in startMinutes..endMinutes
    } else {
        currentMinutes >= startMinutes || currentMinutes <= endMinutes
    }
}
