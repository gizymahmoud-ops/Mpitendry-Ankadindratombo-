package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AppVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningScreen(vm: AppVm = viewModel()) {

    val context = LocalContext.current   // ✅ IMPORTANT (ici seulement)

    val st by vm.state.collectAsState()
    val morningList by vm.morning.collectAsState()
    val eveningList by vm.evening.collectAsState()

    LaunchedEffect(Unit) {
        vm.setDateIso(st.selectedDateIso)
    }

    val morningMap = remember(morningList) {
        morningList.associate {
            Instrument.valueOf(it.instrument) to it.musicianId
        }
    }

    val eveningMap = remember(eveningList) {
        eveningList.associate {
            Instrument.valueOf(it.instrument) to it.musicianId
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mpitendry Ankadindratombo",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                OutlinedTextField(
                    value = st.selectedDateIso,
                    onValueChange = { vm.setDateIso(it.trim()) },
                    label = { Text("Daty (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ServiceCard(
                    title = "Alahady maraina",
                    slot = ServiceSlot.MATIN,
                    assignments = morningMap,
                    vm = vm,
                    context = context
                )
            }

            item {
                ServiceCard(
                    title = "Alahady hariva",
                    slot = ServiceSlot.SOIR,
                    assignments = eveningMap,
                    vm = vm,
                    context = context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceCard(
    title: String,
    slot: ServiceSlot,
    assignments: Map<Instrument, Long>,
    vm: AppVm,
    context: Context
) {

    val st by vm.state.collectAsState()

    ElevatedCard(
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(title, fontWeight = FontWeight.SemiBold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { vm.autoFill(slot) }) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Auto")
                }

                OutlinedButton(onClick = { vm.clearSlot(slot) }) {
                    Icon(Icons.Default.DeleteOutline, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Fafao")
                }
            }

            Instrument.values().forEach { inst ->
                AssignmentRow(
                    instrument = inst.label,
                    selectedId = assignments[inst],
                    musicians = st.musicians.filter {
                        it.active && it.instruments().contains(inst)
                    },
                    onPick = { vm.setAssignment(slot, inst, it) }
                )
            }

            val shareText = buildShareText(title, assignments, st.musicians)

            Button(
                onClick = {
                    val clip = ClipData.newPlainText("planning", shareText)
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(clip)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ContentCopy, null)
                Spacer(Modifier.width(8.dp))
                Text("Adika (WhatsApp)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentRow(
    instrument: String,
    selectedId: Long?,
    musicians: List<dev.vmail.mpitendry.data.Musician>,
    onPick: (Long) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val selectedName =
        musicians.firstOrNull { it.id == selectedId }?.name ?: "Safidio…"

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(instrument)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mpitendry") },
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
    all: List<dev.vmail.mpitendry.data.Musician>
): String {

    fun nameOf(id: Long?): String =
        all.firstOrNull { it.id == id }?.name ?: "—"

    return buildString {
        appendLine("🎶 $title")
        appendLine("• Clavier: ${nameOf(map[Instrument.CLAVIER])}")
        appendLine("• Guitar Bass: ${nameOf(map[Instrument.GUITAR_BASS])}")
        appendLine("• Batterie: ${nameOf(map[Instrument.BATTERIE])}")
    }
}