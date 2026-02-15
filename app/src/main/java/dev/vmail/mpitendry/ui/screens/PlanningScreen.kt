package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vmail.mpitendry.data.Instrument
import dev.vmail.mpitendry.data.ServiceSlot
import dev.vmail.mpitendry.ui.AppVm

@OptIn(ExperimentalMaterial3Api::class)
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

    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Mpitendry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Ankadindratombo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                DateCard(
                    value = st.selectedDateIso,
                    onChange = { vm.setDateIso(it.trim()) }
                )
            }

            item {
                ServiceCardPro(
                    title = "Alahady maraina",
                    slot = ServiceSlot.MATIN,
                    assignments = morningMap,
                    vm = vm,
                    onCopied = { snackbar.showSnackbar("Nadika ao amin'ny clipboard ✅") }
                )
            }

            item {
                ServiceCardPro(
                    title = "Alahady hariva",
                    slot = ServiceSlot.SOIR,
                    assignments = eveningMap,
                    vm = vm,
                    onCopied = { snackbar.showSnackbar("Nadika ao amin'ny clipboard ✅") }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DateCard(
    value: String,
    onChange: (String) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Daty",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                label = { Text("YYYY-MM-DD") },
                supportingText = { Text("Ohatra: 2026-02-15") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ServiceCardPro(
    title: String,
    slot: ServiceSlot,
    assignments: Map<Instrument, Long>,
    vm: AppVm,
    onCopied: (String) -> Unit
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

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Safidio ny mpitendry isaky ny zava-maneno",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = { vm.autoFill(slot) },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Auto")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { vm.clearSlot(slot) },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Fafao")
                }
            }

            Divider()

            Instrument.values().forEach { inst ->
                AssignmentRowPro(
                    instrument = inst.label,
                    selectedId = assignments[inst],
                    musicians = st.musicians.filter { it.active && it.instruments().contains(inst) },
                    onPick = { vm.setAssignment(slot, inst, it) }
                )
            }

            val shareText = buildShareText(title, assignments, st.musicians)

            Button(
                onClick = {
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    val clip = ClipData.newPlainText("planning", shareText)
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                    onCopied(shareText)
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text("Adika (WhatsApp)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentRowPro(
    instrument: String,
    selectedId: Long?,
    musicians: List<dev.vmail.mpitendry.data.Musician>,
    onPick: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = musicians.firstOrNull { it.id == selectedId }?.name ?: "Safidio…"

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(instrument, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)

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
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
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