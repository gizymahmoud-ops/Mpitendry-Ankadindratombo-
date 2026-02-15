@file:OptIn(ExperimentalMaterial3Api::class)

package dev.vmail.mpitendry.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    LaunchedEffect(Unit) {
        vm.setDateIso(st.selectedDateIso)
    }

    val morningMap = remember(morningList) {
        morningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    val eveningMap = remember(eveningList) {
        eveningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                "Mpitendry Ankadindratombo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = st.selectedDateIso,
                onValueChange = { vm.setDateIso(it) },
                label = { Text("Daty (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ServiceCard(
                title = "Alahady maraina",
                slot = ServiceSlot.MATIN,
                assignments = morningMap,
                vm = vm
            )
        }

        item {
            ServiceCard(
                title = "Alahady hariva",
                slot = ServiceSlot.SOIR,
                assignments = eveningMap,
                vm = vm
            )
        }

        item {
            SignatureCard()
        }

        item { Spacer(Modifier.height(30.dp)) }
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
    val ctx = LocalContext.current

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(title, style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.autoFill(slot) }) {
                    Text("Fenoy ho azy")
                }
                OutlinedButton(onClick = { vm.clearSlot(slot) }) {
                    Text("Fafao")
                }
            }

            Instrument.values().forEach { inst ->

                val musicians =
                    st.musicians.filter { it.active && it.instruments().contains(inst) }

                var expanded by remember { mutableStateOf(false) }

                val selectedName =
                    musicians.firstOrNull { it.id == assignments[inst] }?.name
                        ?: "Safidio..."

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = selectedName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(inst.label) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        musicians.forEach { musician ->
                            DropdownMenuItem(
                                text = { Text(musician.name) },
                                onClick = {
                                    vm.setAssignment(slot, inst, musician.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    val text = buildShareText(title, assignments, st.musicians)
                    val clip = ClipData.newPlainText("planning", text)
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(clip)
                }
            ) {
                Text("Adika ho WhatsApp")
            }
        }
    }
}

@Composable
private fun SignatureCard() {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Créer par : Iantsa", fontWeight = FontWeight.SemiBold)
            Text("Contact : 0387290972")
            Text(
                "Application 100% Offline",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

    return """
🎶 $title
• Clavier: ${nameOf(map[Instrument.CLAVIER])}
• Guitar Bass: ${nameOf(map[Instrument.GUITAR_BASS])}
• Batterie: ${nameOf(map[Instrument.BATTERIE])}
""".trimIndent()
}