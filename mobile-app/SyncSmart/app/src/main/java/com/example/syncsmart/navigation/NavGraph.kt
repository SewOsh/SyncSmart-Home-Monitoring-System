package com.example.syncsmart.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.ui.screens.auth.LoginScreen
import com.example.syncsmart.ui.screens.dashboard.DashboardScreen
import com.example.syncsmart.ui.screens.device.DeviceControlScreen
import com.example.syncsmart.ui.screens.devices.DevicesListScreen
import com.example.syncsmart.ui.screens.floor.FloorPlanScreen
import com.example.syncsmart.ui.screens.onboarding.WelcomeScreen
import com.example.syncsmart.ui.screens.reports.ReportsScreen
import com.example.syncsmart.ui.screens.splash.SplashScreen

@Composable
fun SyncSmartNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Welcome.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }
        composable(Routes.Login.route) {
            // UI only — no auth backend wired up. The button just moves you on to
            // Dashboard, same as every other screen transition in this flow.
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onFloorClick = { floorId ->
                    navController.navigate(Routes.FloorPlan.with(floorId))
                },
                onDevicesTabClick = {
                    navController.navigate(Routes.Devices.route) {
                        launchSingleTop = true
                    }
                },
                onReportsRowClick = {
                    navController.navigate(Routes.Reports.route)
                },
                onReportsTabClick = {
                    navController.navigate(Routes.Reports.route)
                }
            )
        }
        composable(
            route = Routes.FloorPlan.route,
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val floorId = backStackEntry.arguments?.getString("floorId") ?: "ground"
            val floorName = if (floorId == "first") "First floor" else "Ground floor"
            FloorPlanScreen(
                floorId = floorId,
                floorName = floorName,
                onBack = { navController.popBackStack() },
                onDeviceClick = { deviceId ->
                    // Every marker opens Device Control on that specific device,
                    // per the floor plan's own hint banner.
                    navController.navigate(Routes.DeviceControl.with(deviceId))
                },
                onDevicesTabClick = {
                    navController.navigate(Routes.Devices.route) {
                        launchSingleTop = true
                    }
                },
                onReportsTabClick = {
                    navController.navigate(Routes.Reports.route)
                }
            )
        }
        composable(Routes.Devices.route) {
            DevicesListScreen(
                onBack = { navController.popBackStack() },
                onDeviceClick = { deviceId ->
                    navController.navigate(Routes.DeviceControl.with(deviceId))
                },
                onDashboardTabClick = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route)
                        launchSingleTop = true
                    }
                },
                onFloorsTabClick = {
                    navController.navigate(Routes.FloorPlan.with("ground"))
                },
                onReportsTabClick = {
                    navController.navigate(Routes.Reports.route)
                }
            )
        }
        composable(
            route = Routes.DeviceControl.route,
            arguments = listOf(
                navArgument("deviceId") {
                    type = NavType.StringType
                    defaultValue = DeviceCatalog.DefaultDeviceId
                }
            )
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: DeviceCatalog.DefaultDeviceId
            DeviceControlScreen(
                deviceId = deviceId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.Reports.route) {
            ReportsScreen(
                onBack = { navController.popBackStack() },
                onDashboardTabClick = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route)
                        launchSingleTop = true
                    }
                },
                onFloorsTabClick = {
                    navController.navigate(Routes.FloorPlan.with("ground"))
                },
                onDevicesTabClick = {
                    navController.navigate(Routes.Devices.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
