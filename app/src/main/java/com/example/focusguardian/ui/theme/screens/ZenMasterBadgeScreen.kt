package com.example.focusguardian.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ZenMasterBadgeScreen(navController: NavController) {
    BadgeDetail(
        title = "Zen Master",
        desc = "Complete 5 no-scroll challenges",
        reward = "500 points",
        navController = navController
    )
}
