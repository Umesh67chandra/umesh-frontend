package com.example.focusguardian.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SleepGuardianBadgeScreen(navController: NavController) {
    BadgeDetail(
        title = "Sleep Guardian",
        desc = "Complete an early sleep challenge",
        reward = "250 points",
        navController = navController
    )
}
