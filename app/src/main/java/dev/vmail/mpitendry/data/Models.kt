package dev.vmail.mpitendry.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Instrument(val label: String) {
    CLAVIER("Clavier"),
    GUITAR_BASS("Guitar Bass"),
    BATTERIE("Batterie")
}

enum class ServiceSlot(val label: String) {
    MATIN("Dimanche matin"),
    SOIR("Dimanche soir")
}

@Entity
data class Musician(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val instrumentsCsv: String,
    val active: Boolean = true,
    val rotationIndex: Long = 0
) {
    fun instruments(): Set<Instrument> =
        instrumentsCsv.split(",").mapNotNull {
            runCatching { Instrument.valueOf(it.trim()) }.getOrNull()
        }.toSet()
}

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String
)

@Entity
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleId: Long,
    val slot: String,
    val instrument: String,
    val musicianId: Long
)
