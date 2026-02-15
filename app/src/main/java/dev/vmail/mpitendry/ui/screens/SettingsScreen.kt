package dev.vmail.mpitendry.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Fanovana", style = MaterialTheme.typography.titleLarge)
        Text("Version 1.0")
        Text("Torohevitra: Ao amin\'ny fandaharam-potoana, azonao adika ilay soratra dia apetaho amin\'ny WhatsApp.")
    }
}
