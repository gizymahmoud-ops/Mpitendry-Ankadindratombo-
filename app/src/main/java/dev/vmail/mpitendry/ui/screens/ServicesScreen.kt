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
fun ServicesScreen(vm: AppVm = viewModel()) {

    val st by vm.state.collectAsState()
    val morningList by vm.morning.collectAsState()
    val eveningList by vm.evening.collectAsState()

    val ctx = LocalContext.current

    val morningMap = remember(morningList) {
        morningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    val eveningMap = remember(eveningList) {
        eveningList.associate { Instrument.valueOf(it.instrument) to it.musicianId }
    }

    val shareText = buildFullShareText(
        st.selectedDateIso,
        morningMap,
        eveningMap,
        st.musicians
    )

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Fizaràna Planning",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Card {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Daty: ${st.selectedDateIso}")
                Divider()
                Text(shareText)
            }
        }

        Button(
            onClick = {
                val clip = ClipData.newPlainText("planning", shareText)
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(clip)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adika ho WhatsApp")
        }
    }
}

private fun buildFullShareText(
    date: String,
    morning: Map<Instrument, Long>,
    evening: Map<Instrument, Long>,
    all: List<dev.vmail.mpitendry.data.Musician>
): String {

    fun nameOf(id: Long?): String =
        all.firstOrNull { it.id == id }?.name ?: "—"

    return buildString {
        appendLine("🎶 Planning Alahady")
        appendLine("📅 $date")
        appendLine("")
        appendLine("☀️ Maraina")
        appendLine("• Clavier: ${nameOf(morning[Instrument.CLAVIER])}")
        appendLine("• Guitar Bass: ${nameOf(morning[Instrument.GUITAR_BASS])}")
        appendLine("• Batterie: ${nameOf(morning[Instrument.BATTERIE])}")
        appendLine("")
        appendLine("🌙 Hariva")
        appendLine("• Clavier: ${nameOf(evening[Instrument.CLAVIER])}")
        appendLine("• Guitar Bass: ${nameOf(evening[Instrument.GUITAR_BASS])}")
        appendLine("• Batterie: ${nameOf(evening[Instrument.BATTERIE])}")
    }
}