package com.example.focusguardian

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focusguardian.kt.AppNavGraph
import com.example.focusguardian.navigation.Routes
import com.example.focusguardian.ui.theme.FocusGuardianTheme
import com.example.focusguardian.ui.theme.screens.AppMenuDrawer
import com.example.focusguardian.ui.theme.screens.BottomNavigationBar
import com.example.focusguardian.viewmodel.AppUsageViewModel
import com.example.focusguardian.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val appUsageViewModel: AppUsageViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            FocusGuardianTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showTopBar = currentRoute in listOf(
                    Routes.DASHBOARD,
                    Routes.CHALLENGES,
                    Routes.ADDICTION_SCORE,
                    Routes.SLEEP,
                    Routes.NOTIFICATIONS,
                    Routes.ANALYTICS,
                    Routes.WALLPAPER,
                    Routes.PROFILE_SETTINGS,
                    Routes.MANAGE_APPS
                )
                val showBottomBar = currentRoute in listOf(
                    Routes.DASHBOARD,
                    Routes.CHALLENGES,
                    Routes.ADDICTION_SCORE,
                    Routes.SLEEP,
                    Routes.NOTIFICATIONS,
                    Routes.ANALYTICS
                )


                val title = when (currentRoute) {
                    Routes.DASHBOARD -> "Focus Guardian"
                    Routes.NOTIFICATIONS -> "Notifications"
                    Routes.ADDICTION_SCORE -> "Addiction Score"
                    Routes.SLEEP -> "Sleep Cycle"
                    Routes.CHALLENGES -> "Challenges"
                    Routes.ANALYTICS -> "Analytics"
                    Routes.WALLPAPER -> "Smart Wallpaper"
                    Routes.PROFILE_SETTINGS -> "Profile Settings"
                    Routes.MANAGE_APPS -> "Manage Apps"
                    else -> "Focus Guardian"
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                AppMenuDrawer(navController = navController, closeDrawer = {
                                    scope.launch { drawerState.close() }
                                })
                            }
                        }
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Scaffold(
                                topBar = {
                                    if (showTopBar) {
                                        MainTopAppBar(
                                            title = title, 
                                            onMenuClick = {
                                                scope.launch { drawerState.open() }
                                            },
                                            onThemeToggle = { darkTheme = !darkTheme },
                                            onSecurityClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                                        )
                                    }
                                },
                                bottomBar = {
                                    if (showBottomBar) {
                                        BottomNavigationBar(navController)
                                    }
                                },
                                floatingActionButton = {
                                    if (showBottomBar) {
                                        FloatingActionButton(
                                            onClick = { /* TODO */ }
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Add")
                                        }
                                    }
                                }
                            ) { paddingValues ->
                                AppNavGraph(
                                    navController = navController,
                                    userViewModel = userViewModel,
                                    appUsageViewModel = appUsageViewModel,
                                    modifier = Modifier.padding(paddingValues)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(title: String, onMenuClick: () -> Unit, onThemeToggle: () -> Unit, onSecurityClick: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = onSecurityClick) {
                Icon(Icons.Default.Security, contentDescription = "Guardian Status")
            }
            IconButton(onClick = onThemeToggle) {
                Icon(Icons.Default.NightsStay, contentDescription = "Night Mode")
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    )
}
