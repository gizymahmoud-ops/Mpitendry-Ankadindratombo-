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
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AppVm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@Composable
fun SettingsScreen(vm: AppVm = viewModel()) {

    val st by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    var url by remember {
        mutableStateOf(
            // 🔁 Remplace par TON lien RAW GitHub vers planning.json
            "https://raw.githubusercontent.com/TON_COMPTE/TON_REPO/main/planning.json"
        )
    }

    var status by remember { mutableStateOf("Tsy mbola nanao mise à jour.") }
    var loading by remember { mutableStateOf(false) }

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
            label = { Text("Lien planning.json") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            onClick = {
                loading = true
                status = "Téléchargement..."

                scope.launch {
                    try {
                        val jsonText = withContext(Dispatchers.IO) { downloadText(url) }
                        val result = applyPlanningJson(vm, st.musicians, jsonText)

                        status = buildString {
                            append("✅ Mise à jour OK. ")
                            append("Dates: ${result.updatedDates}. ")
                            if (result.missingNames.isNotEmpty()) {
                                append("⚠️ Tsy hita: ${result.missingNames.joinToString(", ")}")
                            }
                        }
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

        AssistChip(onClick = {}, label = { Text(status) })

        Divider()

        Text("Créer par : Iantsa")
        Text("Contact : 0387290972")
    }
}

private data class ApplyResult(
    val updatedDates: Int,
    val missingNames: List<String>
)

private suspend fun applyPlanningJson(
    vm: AppVm,
    musicians: List<dev.vmail.mpitendry.data.Musician>,
    jsonText: String
): ApplyResult {

    val missing = linkedSetOf<String>()
    var updatedDates = 0

    val root = JSONObject(jsonText)
    val plans = root.getJSONArray("plans")

    // helper: trouver musicianId par nom (ignore case + trim)
    fun findIdByName(name: String): Long? {
        val n = name.trim()
        return musicians.firstOrNull { it.name.trim().equals(n, ignoreCase = true) }?.id
    }

    // On applique ligne par ligne
    for (i in 0 until plans.length()) {
        val item = plans.getJSONObject(i)

        val dateIso = item.getString("dateIso")
        val slotStr = item.getString("slot") // "MATIN" ou "SOIR"
        val slot = ServiceSlot.valueOf(slotStr)

        // change date sélectionnée pour que tes fonctions existantes utilisent la bonne date
        vm.setDateIso(dateIso)
        delay(10) // petite pause pour laisser l'état se mettre à jour

        // on efface le slot puis on remplit
        vm.clearSlot(slot)

        // clavier
        val nClavier = item.optString("clavier", "").trim()
        if (nClavier.isNotEmpty()) {
            val id = findIdByName(nClavier)
            if (id != null) vm.setAssignment(slot, Instrument.CLAVIER, id) else missing.add(nClavier)
        }

        // guitar bass
        val nBass = item.optString("guitarBass", "").trim()
        if (nBass.isNotEmpty()) {
            val id = findIdByName(nBass)
            if (id != null) vm.setAssignment(slot, Instrument.GUITAR_BASS, id) else missing.add(nBass)
        }

        // batterie
        val nBat = item.optString("batterie", "").trim()
        if (nBat.isNotEmpty()) {
            val id = findIdByName(nBat)
            if (id != null) vm.setAssignment(slot, Instrument.BATTERIE, id) else missing.add(nBat)
        }

        updatedDates += 1
    }

    return ApplyResult(updatedDates = updatedDates, missingNames = missing.toList())
}

private fun downloadText(url: String): String {
    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
        connectTimeout = 15000
        readTimeout = 20000
        requestMethod = "GET"
    }
    conn.connect()
    if (conn.responseCode !in 200..299) {
        throw IllegalStateException("HTTP ${conn.responseCode}")
    }
    val bytes = conn.inputStream.use { it.readBytes() }
    return bytes.toString(Charset.forName("UTF-8"))
}
