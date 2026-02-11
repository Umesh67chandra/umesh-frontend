package com.example.focusguardian.kt

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.focusguardian.navigation.AuthRoutes
import com.example.focusguardian.ui.theme.screens.*
import com.example.focusguardian.viewmodel.UserViewModel

@Composable
fun AuthNavGraph(navController: NavHostController, userViewModel: UserViewModel) {
    NavHost(
        navController = navController,
        startDestination = AuthRoutes.SIGN_UP
    ) {
        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(navController, userViewModel = userViewModel)
        }

        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                userViewModel = userViewModel,
                onSignIn = { navController.navigate(AuthRoutes.CHOOSE_ROLE) },
                onCreateAccount = { navController.navigate(AuthRoutes.SIGN_UP) }
            )
        }

        composable(AuthRoutes.CHOOSE_ROLE) {
            ChooseYourRoleScreen(
                navController = navController,
                userViewModel = userViewModel,
                onRoleSelected = { role ->
                    when (role) {
                        "child" -> navController.navigate(AuthRoutes.CHILD_INTERESTS)
                        "adult" -> navController.navigate(AuthRoutes.ADULT_INTERESTS)
                        "parent" -> navController.navigate(AuthRoutes.PARENT_INTERESTS)
                    }
                },
                onSignIn = { navController.navigate(AuthRoutes.LOGIN) }
            )
        }

        composable(AuthRoutes.CHILD_INTERESTS) {
            ChildInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("dashboard_screen") } // Using string literal as I suspect AuthRoutes might not have dashboard if it's in another graph
            )
        }

        composable(AuthRoutes.ADULT_INTERESTS) {
            AdultInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("dashboard_screen") }
            )
        }

        composable(AuthRoutes.PARENT_INTERESTS) {
            ParentInterestScreen(
                userViewModel = userViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("dashboard_screen") }
            )
        }
    }
}
