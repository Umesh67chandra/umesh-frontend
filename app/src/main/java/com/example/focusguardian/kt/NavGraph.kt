package com.example.focusguardian.kt

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.focusguardian.navigation.Routes
import com.example.focusguardian.ui.theme.screens.*
import com.example.focusguardian.viewmodel.AppUsageViewModel
import com.example.focusguardian.viewmodel.UserViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {

        // Authentication Flow
        composable(Routes.SPLASH) {
            SplashScreen {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                userViewModel = userViewModel,
                onSignIn = {
                    // After signing in, if the role is not set, go to role selection.
                    // Otherwise, go to the main dashboard.
                    if (userViewModel.role == null) {
                        navController.navigate(Routes.ROLE) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.MAIN) { // Navigate to the Main App Graph
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onCreateAccount = {
                    navController.navigate(Routes.CREATE)
                }
            )
        }

        composable(Routes.CREATE) {
            CreateAccountScreen(
                navController = navController,
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onCreateAccount = {
                    // Account created and token stored. Proceed to Role Selection logic.
                    // Note: If we want to force login, we go to LOGIN. 
                    // But since register() logs us in, we can direct to ROLE.
                    // However, original request said "Enter correct details it is shoing invalid credentials"
                    // implies they were forced to login. 
                    // Let's improve flow: Go to ROLE directly.
                     navController.navigate(Routes.ROLE) {
                        popUpTo(Routes.CREATE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ROLE) {
            ChooseYourRoleScreen(
                navController = navController,
                userViewModel = userViewModel,
                onRoleSelected = { role ->
                    when (role) {
                        "child" -> navController.navigate(Routes.CHILD_INTEREST)
                        "adult" -> navController.navigate(Routes.ADULT_INTEREST)
                        "parent" -> navController.navigate(Routes.PARENT_INTEREST)
                        else -> navController.navigate(Routes.INTEREST) // Fallback
                    }
                },
                onSignIn = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.CHILD_INTEREST) {
            ChildInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        composable(Routes.ADULT_INTEREST) {
            AdultInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        composable(Routes.PARENT_INTEREST) {
            ParentInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        composable(Routes.INTEREST) {
            InterestSelectionScreen(
                onBack = { navController.popBackStack() },
                onContinue = { interests: Set<String> ->
                    // Legacy flow, maybe update or keep as is for generic fallback
                    val interestsString = interests.joinToString(",")
                    navController.navigate("${Routes.REFINE_INTEREST}/$interestsString")
                }
            )
        }

        composable("${Routes.REFINE_INTEREST}/{interests}") { backStackEntry ->
            val interests = backStackEntry.arguments?.getString("interests")?.split(",") ?: emptyList()
            RefineYourInterestsScreen(
                interests = interests,
                onBack = { navController.popBackStack() },
                onContinue = {
                    // After refining interests, the onboarding is complete. Go to the main dashboard.
                    navController.navigate(Routes.MAIN) {
                        // Clear the entire auth flow from the back stack.
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Main App Navigation Graph (Nested)
        navigation(startDestination = Routes.DASHBOARD, route = Routes.MAIN) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(navController, appUsageViewModel, userViewModel)
            }
            composable(Routes.ADDICTION_SCORE) {
                AddictionScoreScreen(navController, appUsageViewModel)
            }
            composable(Routes.SLEEP) {
                SleepCycleScreen(navController)
            }
            composable(Routes.CHALLENGES) {
                ChallengesScreen(navController)
            }
            composable(Routes.ANALYTICS) {
                AnalyticsScreen(navController)
            }
            composable(Routes.TIME_USED) {
                TimeUsedScreen(navController, appUsageViewModel)
            }
            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(navController, appUsageViewModel)
            }
            composable(Routes.WALLPAPER) {
                SmartWallpaperScreen()
            }
            composable(Routes.PROFILE_SETTINGS) {
                ProfileSettingsScreen(navController, userViewModel = userViewModel)
            }
            composable(Routes.NO_SCROLL) {
                NoScrollChallengeScreen()
            }
            composable(Routes.DEEP_FOCUS) {
                DeepFocusChallengeScreen()
            }
            composable(Routes.EARLY_SLEEP) {
                EarlySleepChallengeScreen()
            }
            composable(Routes.FIRST_STEP_BADGE) {
                FirstStepBadgeScreen(navController)
            }
            composable(Routes.SLEEP_GUARDIAN_BADGE) {
                SleepGuardianBadgeScreen(navController)
            }
            composable(Routes.THREE_DAY_STREAK_BADGE) {
                ThreeDayStreakBadgeScreen(navController)
            }
            composable(Routes.ZEN_MASTER_BADGE) {
                ZenMasterBadgeScreen(navController)
            }
            composable(Routes.MANAGE_APPS) {
                ManageAppsScreen(appUsageViewModel)
            }
        }
    }
}