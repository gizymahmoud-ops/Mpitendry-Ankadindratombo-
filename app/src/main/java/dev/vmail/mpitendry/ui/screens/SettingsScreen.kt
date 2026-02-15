package dev.vmail.mpitendry.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.PackDownloader
import dev.vmail.mpitendry.ui.AppVm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(vm: AppVm = viewModel()) {

    var url by remember {
        mutableStateOf(
            // Mets ici TON lien direct vers planningpack.json
            "https://raw.githubusercontent.com/TON_COMPTE/TON_REPO/main/planningpack.json"
        )
    }

    var status by remember { mutableStateOf("Aucune mise à jour.") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Paramètres", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = url,
            onValueChange = { url = it.trim() },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Lien planningpack.json") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            onClick = {
                loading = true
                status = "Téléchargement..."
                scope.launch {
                    try {
                        val pack = withContext(Dispatchers.IO) {
                            PackDownloader.downloadPack(url)
                        }
                        vm.importPack(pack)
                        status = "✅ Mise à jour OK (${pack.assignments.size} affectations)"
                    } catch (e: Exception) {
                        status = "❌ Erreur: ${e.message}"
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text(if (loading) "Mise à jour..." else "Mettre à jour")
        }

        AssistChip(
            onClick = {},
            label = { Text(status) }
        )

        Divider(Modifier.padding(top = 10.dp))

        Text(
            "Créer par : Iantsa\nContact : 0387290972",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
