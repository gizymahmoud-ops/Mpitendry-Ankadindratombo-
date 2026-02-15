package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.data.Musician
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AppVm

@Composable
fun PlanningScreen(vm: AppVm = viewModel()) {
    val st by vm.state.collectAsState()
    val morningList by vm.morning.collectAsState()
    val eveningList by vm.evening.collectAsState()

    LaunchedEffect(Unit) { vm.setDateIso(st.selectedDateIso) }

    val morningMap = remember(morningList) {
        morningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }
    val eveningMap = remember(eveningList) {
        eveningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Mpitendry Ankadindratombo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            OutlinedTextField(
                value = st.selectedDateIso,
                onValueChange = { vm.setDateIso(it.trim()) },
                label = { Text("Daty (YYYY-MM-DD) - Alahady") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ServiceCard(
                title = "Alahady maraina",
                slot = ServiceSlot.MATIN,
                assignments = morningMap,
                musicians = st.musicians,
                vm = vm
            )
        }

        item {
            ServiceCard(
                title = "Alahady hariva",
                slot = ServiceSlot.SOIR,
                assignments = eveningMap,
                musicians = st.musicians,
                vm = vm
            )
        }

        item {
            Divider()
            Text("Créer par : Iantsa")
            Text("Contact : 0387290972")
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    slot: ServiceSlot,
    assignments: Map<Instrument, Long>,
    musicians: List<Musician>,
    vm: AppVm
) {
    val ctx = LocalContext.current

    Card {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.autoFill(slot) }) { Text("Fenoy ho azy") }
                OutlinedButton(onClick = { vm.clearSlot(slot) }) { Text("Fafao") }
            }

            Instrument.values().forEach { inst ->
                AssignmentRow(
                    instrument = inst.label,
                    selectedId = assignments[inst],
                    musicians = musicians.filter { it.active && it.instruments().contains(inst) },
                    onPick = { pickedId -> vm.setAssignment(slot, inst, pickedId) }
                )
            }

            val shareText = buildShareText(title, assignments, musicians)

            OutlinedButton(
                onClick = {
                    val clip = ClipData.newPlainText("planning", shareText)
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                }
            ) { Text("Adika ho WhatsApp") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentRow(
    instrument: String,
    selectedId: Long?,
    musicians: List<Musician>,
    onPick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(instrument, style = MaterialTheme.typography.labelLarge)

        var expanded by remember { mutableStateOf(false) }
        val selectedName = musicians.firstOrNull { it.id == selectedId }?.name ?: "Safidio…"

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mpitendry") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                musicians.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m.name) },
                        onClick = {
                            onPick(m.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun buildShareText(
    title: String,
    map: Map<Instrument, Long>,
    all: List<Musician>
): String {
    fun nameOf(id: Long?): String = all.firstOrNull { it.id == id }?.name ?: "—"

    return buildString {
        appendLine("🎶 $title")
        appendLine("• Clavier: ${nameOf(map[Instrument.CLAVIER])}")
        appendLine("• Guitar Bass: ${nameOf(map[Instrument.GUITAR_BASS])}")
        appendLine("• Batterie: ${nameOf(map[Instrument.BATTERIE])}")
    }
}
