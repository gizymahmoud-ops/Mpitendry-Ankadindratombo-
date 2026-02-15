package dev.vmail.mpitendry.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val items = listOf("planning" to "Fandaharam-potoana", "musicians" to "Mpitendry", "settings" to "Fanovana")

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by nav.currentBackStackEntryAsState()
                val route = current?.destination?.route
                items.forEach { (r, label) ->
                    NavigationBarItem(
                        selected = route == r,
                        onClick = { nav.navigate(r) { launchSingleTop = true } },
                        label = { Text(label) },
                        icon = { }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(navController = nav, startDestination = "planning", modifier = Modifier.padding(pad)) {
            composable("planning") { PlanningScreen() }
            composable("musicians") { MusiciansScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
