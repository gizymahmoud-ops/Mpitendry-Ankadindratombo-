package dev.vmail.mpitendry.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import dev.vmail.mpitendry.R
import dev.vmail.mpitendry.ui.screens.MusiciansScreen
import dev.vmail.mpitendry.ui.screens.PlanningScreen
import dev.vmail.mpitendry.ui.screens.ServicesScreen
import dev.vmail.mpitendry.ui.screens.SettingsScreen

private sealed class Tab(val route: String, val label: String, val icon: Int) {
    data object Planning : Tab("planning", "Drafitra", R.drawable.ic_calendar)
    data object Musicians : Tab("musicians