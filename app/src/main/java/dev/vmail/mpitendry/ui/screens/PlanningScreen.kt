package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.setDateIso(st.selectedDateIso)
    }

    val morningMap = remember(morningList) {
        morningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    val eveningMap = remember(eveningList) {
        eveningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Planning",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = st.selectedDateIso,
            onValueChange = { vm.setDateIso(it.trim()) },
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ServiceCard(
            title = "Alahady maraina",
            slot = ServiceSlot.MATIN,
            assignments = morningMap,
            vm = vm,
            context = context
        )

        ServiceCard(
            title = "Alahady hariva",
            slot = ServiceSlot.SOIR,
            assignments = eveningMap,
            vm = vm,
            context = context
        )

        Divider()

        Text(
            "Créer par : Iantsa",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            "Contact : 0387290972",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ServiceCard(
    title: String,
    slot: ServiceSlot,
    assignments: Map<Instrument, Long>,
    vm: AppVm,
    context: Context
) {

    val st by vm.state.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.autoFill(slot) }) {
                    Text("Auto")
                }

                OutlinedButton(onClick = { vm.clearSlot(slot) }) {
                    Text("Clear")
                }
            }

            Instrument.values().forEach { inst ->

                val selectedId = assignments[inst]

                AssignmentRow(
                    instrument = inst.label,
                    selectedId = selectedId,
                    musicians = st.musicians.filter {
                        it.active && it.instruments().contains(inst)
                    },
                    onPick = { vm.setAssignment(slot, inst, it) }
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val shareText = buildShareText(title, assignments, st.musicians)
                    val clip = ClipData.newPlainText("planning", shareText)
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(clip)
                }
            ) {
                Text("Copier pour WhatsApp")
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
        musicians.firstOrNull { it.id == selectedId }?.name ?: "Sélectionner..."

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(instrument, style = MaterialTheme.typography.labelLarge)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("Musicien") }
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
        all.firstOrNull { it.id == id }?.name ?: "-"

    return buildString {
        appendLine("🎶 $title")
        appendLine("Clavier: ${nameOf(map[Instrument.CLAVIER])}")
        appendLine("Guitar Bass: ${nameOf(map[Instrument.GUITAR_BASS])}")
        appendLine("Batterie: ${nameOf(map[Instrument.BATTERIE])}")
    }
}
