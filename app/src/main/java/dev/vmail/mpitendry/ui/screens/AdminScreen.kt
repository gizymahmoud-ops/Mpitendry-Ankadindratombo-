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
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AdminPrefs
import dev.vmail.mpitendry.ui.AppVm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AdminScreen(vm: AppVm = viewModel()) {
    val ctx = LocalContext.current
    val isAdmin = remember { AdminPrefs.isAdmin(ctx) }

    if (!isAdmin) {
        Column(Modifier.padding(16.dp)) {
            Text("Admin ihany no mahazo miditra.", style = MaterialTheme.typography.titleLarge)
            Text("Mankanesa any amin'ny Paramètres → ampidirio ny Code Admin.")
        }
        return
    }

    val scope = rememberCoroutineScope()

    var year by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var month by remember { mutableStateOf(LocalDate.now().monthValue.toString().padStart(2, '0')) }
    var status by remember { mutableStateOf("Vonona.") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Admin", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = year,
            onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) },
            label = { Text("Taona (YYYY)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = month,
            onValueChange = { month = it.filter { c -> c.isDigit() }.take(2) },
            label = { Text("Volana (01-12)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                loading = true
                status = "Mamorona planning..."

                scope.launch {
                    try {
                        val y = year.toInt()
                        val m = month.toInt().coerceIn(1, 12)
                        val ym = YearMonth.of(y, m)

                        val sundays = allSundaysOfMonth(ym)
                        if (sundays.isEmpty()) {
                            status = "Tsy misy Alahady."
                            loading = false
                            return@launch
                        }

                        // Pour chaque dimanche : MATIN puis SOIR
                        sundays.forEach { date ->
                            val iso = date.toString()

                            // MATIN
                            vm.setDateIso(iso)
                            delay(30)
                            vm.clearSlot(ServiceSlot.MATIN)
                            vm.autoFill(ServiceSlot.MATIN)

                            // SOIR
                            vm.setDateIso(iso)
                            delay(30)
                            vm.clearSlot(ServiceSlot.SOIR)
                            vm.autoFill(ServiceSlot.SOIR)
                        }

                        status = "✅ Vita: ${sundays.size} Alahady (volana $m/$y)"
                    } catch (e: Exception) {
                        status = "❌ Error: ${e.message}"
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text(if (loading) "Mamorona..." else "Générer ny Alahady rehetra")
        }

        AssistChip(onClick = {}, label = { Text(status) })

        Text(
            "Fanamarihana: Rehefa vita ny génération, ataovy mise à jour ny planning.json ao amin'ny GitHub.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun allSundaysOfMonth(ym: YearMonth): List<LocalDate> {
    val list = mutableListOf<LocalDate>()
    var d = ym.atDay(1)
    while (d.monthValue == ym.monthValue) {
        if (d.dayOfWeek == DayOfWeek.SUNDAY) list.add(d)
        d = d.plusDays(1)
    }
    return list
}
