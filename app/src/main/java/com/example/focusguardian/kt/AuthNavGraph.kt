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
                onBack = { navController.popBackStack() },
                onContinue = { /* TODO: Navigate to dashboard */ }
            )
        }

        composable(AuthRoutes.ADULT_INTERESTS) {
            AdultInterestScreen(
                onBack = { navController.popBackStack() },
                onContinue = { /* TODO: Navigate to dashboard */ }
            )
        }

        composable(AuthRoutes.PARENT_INTERESTS) {
            ParentInterestScreen(
                onBack = { navController.popBackStack() },
                onContinue = { /* TODO: Navigate to dashboard */ }
            )
        }
    }
}
