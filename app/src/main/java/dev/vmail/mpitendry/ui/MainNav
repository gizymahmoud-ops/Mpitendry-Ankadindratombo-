package dev.vmail.mpitendry.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import dev.vmail.mpitendry.R
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen
import dev.vmail.mpitendry.ui.screens.ServicesScreen

private sealed class Tab(
    val route: String,
    val label: String,
    val icon: Int
) {
    data object Planning : Tab("planning", "Drafitra", R.drawable.ic_calendar)
    data object Musicians : Tab("musicians", "Mpitendry", R.drawable.ic_people)
    data object Services : Tab("services", "Fizaràna", R.drawable.ic_share)
    data object Settings : Tab("settings", "Fikirana", R.drawable.ic_settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNav(vm: AppVm) {
    val nav = rememberNavController()
    val tabs = listOf(Tab.Planning, Tab.Musicians, Tab.Services, Tab.Settings)

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Tab.Planning.route
    val currentTab = tabs.firstOrNull { it.route == currentRoute } ?: Tab.Planning

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mpitendry Ankadindratombo") },
                actions = { /* afaka ampiarahana search eto raha tianao */ }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(tab.icon),
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        AnimatedContent(
            targetState = currentTab.route,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab"
        ) {
            NavHost(
                navController = nav,
                startDestination = Tab.Planning.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Tab.Planning.route) { PlanningScreen(vm = vm) }
                composable(Tab.Musicians.route) { MusiciansScreen(vm = vm) }
                composable(Tab.Services.route) { ServicesScreen(vm = vm) }
                composable(Tab.Settings.route) { SettingsScreen(vm = vm) }
            }
        }
    }
}