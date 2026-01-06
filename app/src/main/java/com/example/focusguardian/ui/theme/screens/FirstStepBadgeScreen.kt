package com.example.focusguardian.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun FirstStepBadgeScreen(navController: NavController) {
    BadgeDetail(
        title = "First Step",
        desc = "Complete your first challenge",
        reward = "100 points",
        navController = navController
    )
}
