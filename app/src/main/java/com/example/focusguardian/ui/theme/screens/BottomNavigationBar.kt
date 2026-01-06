package com.example.focusguardian.ui.theme.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.focusguardian.navigation.Routes

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : BottomNavItem(Routes.DASHBOARD, Icons.Filled.GridView, "Dashboard")
    object Notifications : BottomNavItem(Routes.NOTIFICATIONS, Icons.Filled.Notifications, "Notifications")
    object AddictionScore : BottomNavItem(Routes.ADDICTION_SCORE, Icons.Filled.MonitorHeart, "Addiction")
    object Sleep : BottomNavItem(Routes.SLEEP, Icons.Filled.ModeNight, "Sleep")
    object Analytics : BottomNavItem(Routes.ANALYTICS, Icons.Filled.BarChart, "Analytics")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Notifications,
        BottomNavItem.AddictionScore,
        BottomNavItem.Sleep,
        BottomNavItem.Analytics
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
