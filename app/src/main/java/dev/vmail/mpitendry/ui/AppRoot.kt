package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import dev.vmail.mpitendry.R
import dev.vmail.mpitendry.ui.screens.AdminScreen
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val ctx = LocalContext.current
    val isAdmin = AdminPrefs.isAdmin(ctx)

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = route == "planning",
                    onClick = { navController.navigate("planning") { launchSingleTop = true } },
                    icon = { Icon(painterResource(R.drawable.ic_calendar), null) },
                    label = { Text("Planning") }
                )

                if (isAdmin) {
                    NavigationBarItem(
                        selected = route == "musicians",
                        onClick = { navController.navigate("musicians") { launchSingleTop = true } },
                        icon = { Icon(painterResource(R.drawable.ic_people), null) },
                        label = { Text("Mpitendry") }
                    )

                    NavigationBarItem(
                        selected = route == "admin",
                        onClick = { navController.navigate("admin") { launchSingleTop = true } },
                        icon = { Icon(painterResource(R.drawable.ic_settings), null) },
                        label = { Text("Admin") }
                    )
                }

                NavigationBarItem(
                    selected = route == "settings",
                    onClick = { navController.navigate("settings") { launchSingleTop = true } },
                    icon = { Icon(painterResource(R.drawable.ic_settings), null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "planning",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("planning") { PlanningScreen() }
            composable("musicians") { MusiciansScreen() }
            composable("admin") { AdminScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
