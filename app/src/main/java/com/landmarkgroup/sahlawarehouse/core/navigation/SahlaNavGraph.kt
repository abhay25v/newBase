package com.landmarkgroup.sahlawarehouse.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.landmarkgroup.sahlawarehouse.core.common.ComingSoonScreen
import com.landmarkgroup.sahlawarehouse.core.common.ScanType
import com.landmarkgroup.sahlawarehouse.feature.home.ui.HomeScreen
import com.landmarkgroup.sahlawarehouse.feature.login.ui.LoginScreen

/**
 * Root navigation graph, replaces MvvmCross's ViewModel-first navigation
 * (`IMvxNavigationService.Navigate<TViewModel>()`). Every top-level screen in the old app's
 * `SahlaWH.Core.Pages`/`ViewModels` tree gets a route here as it is migrated.
 */
@Composable
fun SahlaNavGraph(
    navController: NavHostController,
    startDestination: String = SahlaDestinations.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(SahlaDestinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(SahlaDestinations.HOME) {
                        popUpTo(SahlaDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(SahlaDestinations.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(SahlaDestinations.LOGIN) {
                        popUpTo(SahlaDestinations.HOME) { inclusive = true }
                    }
                },
                onNavigateToFeature = { scanType ->
                    navController.navigate(SahlaDestinations.comingSoon(scanType))
                }
            )
        }

        composable(
            route = SahlaDestinations.COMING_SOON_PATTERN,
            arguments = listOf(navArgument("scanType") { type = NavType.StringType })
        ) { backStackEntry ->
            val scanTypeName = backStackEntry.arguments?.getString("scanType").orEmpty()
            val scanType = runCatching { ScanType.valueOf(scanTypeName) }.getOrNull()
            ComingSoonScreen(
                title = scanType?.name ?: scanTypeName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
