package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun MainNav() {

    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == "planning",
                    onClick = {
                        navController.navigate("planning") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Planning"
                        )
                    },
                    label = { Text("Planning") }
                )

                NavigationBarItem(
                    selected = currentRoute == "musicians",
                    onClick = {
                        navController.navigate("musicians") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Mpitendry"
                        )
                    },
                    label = { Text("Mpitendry") }
                )

                NavigationBarItem(
                    selected = currentRoute == "settings",
                    onClick = {
                        navController.navigate("settings") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Paramètres"
                        )
                    },
                    label = { Text("Paramètres") }
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