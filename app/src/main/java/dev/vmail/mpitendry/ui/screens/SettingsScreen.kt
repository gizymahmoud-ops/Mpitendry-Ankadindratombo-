package dev.vmail.mpitendry.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AdminPrefs
import dev.vmail.mpitendry.ui.AppVm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@Composable
fun SettingsScreen(vm: AppVm = viewModel()) {

    val st by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var urlInput by remember {
        mutableStateOf("https://raw.githubusercontent.com/gizymahmoud-ops/Mpitendry-Ankadindratombo-/main/planning.json")
    }

    var status by remember { mutableStateOf("Tsy mbola nanao mise à jour.") }
    var loading by remember { mutableStateOf(false) }

    // Admin
    var adminCode by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(AdminPrefs.isAdmin(ctx)) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Paramètres", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Lien planning.json") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            onClick = {
                loading = true
                status = "Préparation du lien..."

                scope.launch {
                    try {
                        val normalized = normalizeGithubUrl(urlInput)
                        status = "Téléchargement...\n$normalized"

                        val jsonText = withContext(Dispatchers.IO) { downloadText(normalized) }
                        val result = applyPlanningJson(vm, st.musicians, jsonText)

                        status = buildString {
                            append("✅ Mise à jour OK.\n")
                            append("Dates: ${result.updatedDates}\n")
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
        ) { Text(if (loading) "Mise à jour..." else "Mettre à jour") }

        AssistChip(onClick = {}, label = { Text(status) })

        Divider()

        Text("Admin", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = adminCode,
            onValueChange = { adminCode = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Code Admin") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // ✅ Change ce code comme tu veux
                if (adminCode.trim() == "IANTSADMIN") {
                    AdminPrefs.setAdmin(ctx, true)
                    isAdmin = true
                    status = "✅ Mode admin activé"
                } else {
                    status = "❌ Code admin diso"
                }
            }
        ) { Text("Activer Admin") }

        if (isAdmin) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    AdminPrefs.setAdmin(ctx, false)
                    isAdmin = false
                    status = "Mode admin désactivé"
                }
            ) { Text("Désactiver Admin") }
        }

        Divider()

        Text("Créer par : Iantsa")
        Text("Contact : 0387290972")
    }
}

private fun normalizeGithubUrl(input: String): String {
    var s = input.trim().removePrefix("\"").removeSuffix("\"").trim()
    if (s.startsWith("github.com/")) s = "https://$s"
    if (s.startsWith("raw.githubusercontent.com/")) s = "https://$s"
    if (!s.startsWith("http://") && !s.startsWith("https://")) s = "https://$s"

    if (s.contains("github.com/") && s.contains("/blob/")) {
        s = s.replace("https://github.com/", "https://raw.githubusercontent.com/")
        s = s.replace("/blob/", "/")
    }
    s = s.replace("/refs/heads/", "/")
    return s
}

private data class ApplyResult(val updatedDates: Int, val missingNames: List<String>)

private suspend fun applyPlanningJson(
    vm: AppVm,
    musicians: List<dev.vmail.mpitendry.data.Musician>,
    jsonText: String
): ApplyResult {
    val missing = linkedSetOf<String>()
    var updatedDates = 0

    val root = JSONObject(jsonText)
    val plans = root.getJSONArray("plans")

    fun findIdByName(name: String): Long? {
        val n = name.trim()
        return musicians.firstOrNull { it.name.trim().equals(n, ignoreCase = true) }?.id
    }

    for (i in 0 until plans.length()) {
        val item = plans.getJSONObject(i)
        val dateIso = item.getString("dateIso")
        val slot = ServiceSlot.valueOf(item.getString("slot"))

        vm.setDateIso(dateIso)
        delay(20)

        vm.clearSlot(slot)

        val clavier = item.optString("clavier", "").trim()
        if (clavier.isNotEmpty()) {
            val id = findIdByName(clavier)
            if (id != null) vm.setAssignment(slot, Instrument.CLAVIER, id) else missing.add(clavier)
        }

        val bass = item.optString("guitarBass", "").trim()
        if (bass.isNotEmpty()) {
            val id = findIdByName(bass)
            if (id != null) vm.setAssignment(slot, Instrument.GUITAR_BASS, id) else missing.add(bass)
        }

        val bat = item.optString("batterie", "").trim()
        if (bat.isNotEmpty()) {
            val id = findIdByName(bat)
            if (id != null) vm.setAssignment(slot, Instrument.BATTERIE, id) else missing.add(bat)
        }

        updatedDates++
    }

    return ApplyResult(updatedDates, missing.toList())
}

private fun downloadText(url: String): String {
    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
        connectTimeout = 15000
        readTimeout = 20000
        requestMethod = "GET"
    }
    conn.connect()
    if (conn.responseCode !in 200..299) throw IllegalStateException("HTTP ${conn.responseCode}")
    val bytes = conn.inputStream.use { it.readBytes() }
    return bytes.toString(Charset.forName("UTF-8"))
}
