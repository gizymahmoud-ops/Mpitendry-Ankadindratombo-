package dev.vmail.mpitendry.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.vmail.mpitendry.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek

data class UiState(
    val musicians: List<Musician> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
    val selectedDateIso: String = nextSundayIso(),
    val selectedScheduleId: Long? = null
)

private fun nextSundayIso(): String {
    var d = LocalDate.now()
    while (d.dayOfWeek != DayOfWeek.SUNDAY) d = d.plusDays(1)
    return d.toString()
}

class AppVm(app: Application) : AndroidViewModel(app) {
    private val db = AppDb.get(app)
    private val repo = Repository(db.musicianDao(), db.scheduleDao(), db.assignmentDao())

    private val selectedDateIso = MutableStateFlow(nextSundayIso())
    private val selectedScheduleId = MutableStateFlow<Long?>(null)

    val state: StateFlow<UiState> =
        combine(repo.musicians(), repo.schedules(), selectedDateIso, selectedScheduleId) { mus, sch, date, sid ->
            UiState(
                musicians = mus,
                schedules = sch,
                selectedDateIso = date,
                selectedScheduleId = sid
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    val morning = selectedScheduleId.filterNotNull()
        .flatMapLatest { repo.assignments(it, ServiceSlot.MATIN) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evening = selectedScheduleId.filterNotNull()
        .flatMapLatest { repo.assignments(it, ServiceSlot.SOIR) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDateIso(dateIso: String) {
        selectedDateIso.value = dateIso
        viewModelScope.launch {
            val schedule = repo.upsertSchedule(dateIso)
            selectedScheduleId.value = schedule.id
        }
    }

    fun addMusician(name: String, phone: String, instruments: Set<Instrument>) {
        viewModelScope.launch {
            val csv = instruments.joinToString(",") { it.name }
            db.musicianDao().insert(Musician(name = name.trim(), phone = phone.trim(), instrumentsCsv = csv))
        }
    }

    fun deleteMusician(m: Musician) {
        viewModelScope.launch { db.musicianDao().delete(m) }
    }

    fun toggleActive(m: Musician) {
        viewModelScope.launch { db.musicianDao().update(m.copy(active = !m.active)) }
    }

    fun setAssignment(slot: ServiceSlot, instrument: Instrument, musicianId: Long) {
        val sid = selectedScheduleId.value ?: return
        viewModelScope.launch { repo.setAssignment(sid, slot, instrument, musicianId) }
    }

    fun clearSlot(slot: ServiceSlot) {
        val sid = selectedScheduleId.value ?: return
        viewModelScope.launch { repo.clearSlot(sid, slot) }
    }

    fun autoFill(slot: ServiceSlot) {
        val sid = selectedScheduleId.value ?: return
        viewModelScope.launch {
            val mus = state.value.musicians.filter { it.active }
            val instruments = listOf(Instrument.CLAVIER, Instrument.GUITAR_BASS, Instrument.BATTERIE)

            instruments.forEach { inst ->
                val candidates = mus.filter { it.instruments().contains(inst) }
                    .sortedWith(compareBy<Musician> { it.rotationIndex }.thenBy { it.name.lowercase() })

                val pick = candidates.firstOrNull() ?: return@forEach
                repo.setAssignment(sid, slot, inst, pick.id)
                db.musicianDao().update(pick.copy(rotationIndex = pick.rotationIndex + 1))
            }
        }
    }
}
