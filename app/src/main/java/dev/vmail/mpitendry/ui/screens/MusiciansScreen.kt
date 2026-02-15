package dev.vmail.mpitendry.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.ui.AppVm

@Composable
fun MusiciansScreen(vm: AppVm = viewModel()) {
    val st by vm.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var clavier by remember { mutableStateOf(false) }
    var bass by remember { mutableStateOf(false) }
    var batt by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Mpitendry", style = MaterialTheme.typography.titleLarge)

        Card {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Anarana") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Finday (tsy voatery)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = clavier, onClick = { clavier = !clavier }, label = { Text("Clavier") })
                    FilterChip(selected = bass, onClick = { bass = !bass }, label = { Text("Guitar Bass") })
                    FilterChip(selected = batt, onClick = { batt = !batt }, label = { Text("Batterie") })
                }

                Button(
                    enabled = name.trim().isNotEmpty() && (clavier || bass || batt),
                    onClick = {
                        val inst = buildSet {
                            if (clavier) add(Instrument.CLAVIER)
                            if (bass) add(Instrument.GUITAR_BASS)
                            if (batt) add(Instrument.BATTERIE)
                        }
                        vm.addMusician(name, phone, inst)
                        name = ""; phone = ""; clavier = false; bass = false; batt = false
                    }
                ) { Text("Ampidiro") }
            }
        }

        Divider()

        st.musicians.forEach { m ->
            Card {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(m.name, style = MaterialTheme.typography.titleMedium)
                        val inst = m.instruments().joinToString(" • ") { it.label }
                        Text(inst, style = MaterialTheme.typography.bodyMedium)
                        if (m.phone.isNotBlank()) Text(m.phone, style = MaterialTheme.typography.bodySmall)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { vm.toggleActive(m) }) { Text(if (m.active) "Miasa" else "Tsy miasa") }
                        TextButton(onClick = { vm.deleteMusician(m) }) { Text("Fafàna") }
                    }
                }
            }
        }
    }
}
