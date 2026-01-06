package com.example.focusguardian.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.focusguardian.ui.theme.screens.AppUsageInfo

class AppUsageViewModel : ViewModel() {
    val appLimits = mutableStateMapOf<String, AppUsageInfo>()

    val totalDailyLimitMinutes: Int
        get() = appLimits.values.sumOf { it.usageLimitInMinutes }

    val totalTimeUsedMinutes: Int
        get() = appLimits.values.sumOf { it.timeUsedInMinutes }
}
