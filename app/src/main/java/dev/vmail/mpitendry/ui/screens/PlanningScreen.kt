@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
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

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Mpitendry Ankadindratombo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = st.selectedDateIso,
            onValueChange = { vm.setDateIso(it.trim()) },
            label = { Text("Daty (YYYY-MM-DD) - Alahady") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ServiceCard(
            title = "Alahady maraina",
            slot = ServiceSlot.MATIN,
            assignments = morningMap,
            vm = vm
        )

        ServiceCard(
            title = "Alahady hariva",
            slot = ServiceSlot.SOIR,
            assignments = eveningMap,
            vm = vm
        )
    }
}

@Composable
private fun ServiceCard(
    title: String,
    slot: ServiceSlot,
    assignments: Map<Instrument, Long>,
    vm: AppVm
) {
    val st by vm.state.collectAsState()

    // ✅ IMPORTANT: alaina eto ivelan'ny onClick ny context (tsy ao anaty lambda)
    val ctx = LocalContext.current

    Card {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.autoFill(slot) }) { Text("Fenoy ho azy") }
                OutlinedButton(onClick = { vm.clearSlot(slot) }) { Text("Fafao") }
            }

            Instrument.values().forEach { inst ->
                AssignmentRow(
                    instrument = inst.label,
                    selectedId = assignments[inst],
                    musicians = st.musicians.filter { it.active && it.instruments().contains(inst) },
                    onPick = { vm.setAssignment(slot, inst, it) }
                )
            }

            val shareText = buildShareText(title, assignments, st.musicians)

            OutlinedButton(
                onClick = {
                    val clip = ClipData.newPlainText("planning", shareText)
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(clip)
                }
            ) { Text("Adika ho WhatsApp") }
        }
    }
}

@Composable
private fun AssignmentRow(
    instrument: String,
    selectedId: Long?,
    musicians: List<dev.vmail.mpitendry.data.Musician>,
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
                modifier = Modifier.menuAnchor().fillMaxWidth()
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
    all: List<dev.vmail.mpitendry.data.Musician>
): String {
    fun nameOf(id: Long?): String = all.firstOrNull { it.id == id }?.name ?: "—"

    return buildString {
        appendLine("🎶 $title")
        appendLine("• Clavier: ${nameOf(map[Instrument.CLAVIER])}")
        appendLine("• Guitar Bass: ${nameOf(map[Instrument.GUITAR_BASS])}")
        appendLine("• Batterie: ${nameOf(map[Instrument.BATTERIE])}")
    }
}