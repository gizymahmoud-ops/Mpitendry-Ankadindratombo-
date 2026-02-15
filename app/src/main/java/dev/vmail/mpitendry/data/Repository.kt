package dev.vmail.mpitendry.data

import kotlinx.coroutines.flow.Flow

class Repository(
    private val musicianDao: MusicianDao,
    private val scheduleDao: ScheduleDao,
    private val assignmentDao: AssignmentDao
) {
    fun musicians(): Flow<List<Musician>> = musicianDao.observeAll()
    fun schedules(): Flow<List<Schedule>> = scheduleDao.observeAll()

    fun assignments(scheduleId: Long, slot: ServiceSlot): Flow<List<Assignment>> =
        assignmentDao.observeFor(scheduleId, slot.name)

    suspend fun upsertSchedule(dateIso: String): Schedule {
        val existing = scheduleDao.findByDate(dateIso)
        if (existing != null) return existing
        val id = scheduleDao.insert(Schedule(dateIso = dateIso))
        return Schedule(id = id, dateIso = dateIso)
    }

    suspend fun setAssignment(
        scheduleId: Long,
        slot: ServiceSlot,
        instrument: Instrument,
        musicianId: Long
    ) {
        val existing = assignmentDao.findOne(scheduleId, slot.name, instrument.name)
        if (existing == null) {
            assignmentDao.insert(
                Assignment(
                    scheduleId = scheduleId,
                    slot = slot.name,
                    instrument = instrument.name,
                    musicianId = musicianId
                )
            )
        } else {
            assignmentDao.update(existing.copy(musicianId = musicianId))
        }
    }

    suspend fun clearSlot(scheduleId: Long, slot: ServiceSlot) {
        assignmentDao.clearSlot(scheduleId, slot.name)
    }
}
