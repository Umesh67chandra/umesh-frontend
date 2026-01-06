package com.example.focusguardian.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ThreeDayStreakBadgeScreen(navController: NavController) {
    BadgeDetail(
        title = "3-Day Streak",
        desc = "Complete challenges 3 days in a row",
        reward = "300 points",
        navController = navController
    )
}
