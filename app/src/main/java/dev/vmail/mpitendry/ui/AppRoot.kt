package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.vmail.mpitendry.R
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStack = navController.currentBackStackEntryAsState()
    val route = backStack.value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = route == "planning",
                    onClick = {
                        navController.navigate("planning") {
                            popUpTo("planning") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Planning"
                        )
                    },
                    label = { Text("Planning") }
                )

                NavigationBarItem(
                    selected = route == "musicians",
                    onClick = {
                        navController.navigate("musicians") {
                            popUpTo("planning") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_people),
                            contentDescription = "Mpitendry"
                        )
                    },
                    label = { Text("Mpitendry") }
                )

                NavigationBarItem(
                    selected = route == "settings",
                    onClick = {
                        navController.navigate("settings") {
                            popUpTo("planning") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    },
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
            composable("settings") { SettingsScreen() }
        }
    }
}
