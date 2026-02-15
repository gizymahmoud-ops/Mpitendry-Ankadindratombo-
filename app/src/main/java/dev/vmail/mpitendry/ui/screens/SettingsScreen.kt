package dev.vmail.mpitendry.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Fikirana", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        Card {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Offline 100%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Ity app ity tsy mila Internet. Ny angona rehetra dia voatahiry ao anaty finday.")
            }
        }

        Card {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Version", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Mpitendry Ankadindratombo v1.0")
            }
        }
    }
}