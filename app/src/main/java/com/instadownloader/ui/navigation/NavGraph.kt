package com.instadownloader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.instadownloader.data.preferences.UserPreferences
import com.instadownloader.service.AuthResult
import com.instadownloader.ui.screens.LoginScreen
import com.instadownloader.ui.screens.MainScreen
import com.instadownloader.ui.screens.TwoFactorScreen
import com.instadownloader.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    viewModel: AuthViewModel = hiltViewModel(),
    prefs: UserPreferences
) {
    val navController = rememberNavController()
    val isLoggedIn by prefs.isLoggedIn.collectAsState(initial = false)
    val isAnonymous by prefs.isAnonymous.collectAsState(initial = false)

    val startDestination = if (isLoggedIn || isAnonymous) Routes.MAIN else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onTwoFactorRequired = { identifier, method ->
                    navController.navigate("${Routes.TWO_FACTOR}/$identifier/${method.name}")
                }
            )
        }
        composable("${Routes.TWO_FACTOR}/{identifier}/{method}") { backStackEntry ->
            val identifier = backStackEntry.arguments?.getString("identifier") ?: ""
            val methodStr = backStackEntry.arguments?.getString("method") ?: ""
            val method = AuthResult.TwoFactorMethod.valueOf(methodStr)

            TwoFactorScreen(
                identifier = identifier,
                method = method,
                onSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}