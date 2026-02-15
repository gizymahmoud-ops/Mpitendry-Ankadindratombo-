package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun AppRoot() {

    val darkColors = darkColorScheme(
        primary = Color(0xFF4F8CFF),
        secondary = Color(0xFF00C2A8),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        onPrimary = Color.White,
        onBackground = Color(0xFFE2E8F0),
        onSurface = Color(0xFFE2E8F0)
    )

    MaterialTheme(
        colorScheme = darkColors
    ) {

        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryAsState()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "planning",
                        onClick = { navController.navigate("planning") },
                        icon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        label = { Text("Planning") }
                    )

                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "musicians",
                        onClick = { navController.navigate("musicians") },
                        icon = {
                            Icon(Icons.Default.People, contentDescription = null)
                        },
                        label = { Text("Mpitendry") }
                    )

                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "settings",
                        onClick = { navController.navigate("settings") },
                        icon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        },
                        label = { Text("Settings") }
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
}