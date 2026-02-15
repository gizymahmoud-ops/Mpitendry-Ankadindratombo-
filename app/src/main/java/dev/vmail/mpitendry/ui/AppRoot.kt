package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun AppRoot() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("planning") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Planning") },
                    label = { Text("Planning") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("musicians") },
                    icon = { Icon(Icons.Default.People, contentDescription = "Musiciens") },
                    label = { Text("Musiciens") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres") },
                    label = { Text("Paramètres") }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "planning",
            modifier = Modifier.padding(padding)
        ) {
            composable("planning") { PlanningScreen() }
            composable("musicians") { MusiciansScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}