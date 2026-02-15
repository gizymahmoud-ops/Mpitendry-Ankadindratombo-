package dev.vmail.mpitendry.data

import kotlinx.serialization.Serializable

@Serializable
data class PlanningPack(
    val version: Int = 1,
    val generatedAt: String,
    val musicians: List<PackMusician>,
    val assignments: List<PackAssignment>
)

@Serializable
data class PackMusician(
    val id: Long,
    val name: String,
    val active: Boolean,
    val instruments: List<String>
)

@Serializable
data class PackAssignment(
    val dateIso: String,         // "2026-02-15"
    val slot: String,            // "MATIN" or "SOIR"
    val instrument: String,      // "CLAVIER" ...
    val musicianId: Long
)
