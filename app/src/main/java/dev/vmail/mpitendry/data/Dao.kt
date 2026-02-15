package dev.vmail.mpitendry.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicianDao {
    @Query("SELECT * FROM Musician ORDER BY name ASC")
    fun observeAll(): Flow<List<Musician>>

    @Query("SELECT * FROM Musician WHERE id=:id")
    suspend fun getById(id: Long): Musician?

    @Insert
    suspend fun insert(m: Musician): Long

    @Update
    suspend fun update(m: Musician)

    @Delete
    suspend fun delete(m: Musician)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM Schedule ORDER BY dateIso DESC")
    fun observeAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE dateIso=:dateIso LIMIT 1")
    suspend fun findByDate(dateIso: String): Schedule?

    @Insert
    suspend fun insert(s: Schedule): Long
}

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM Assignment WHERE scheduleId=:scheduleId AND slot=:slot")
    fun observeFor(scheduleId: Long, slot: String): Flow<List<Assignment>>

    @Query("SELECT * FROM Assignment WHERE scheduleId=:scheduleId AND slot=:slot AND instrument=:instrument LIMIT 1")
    suspend fun findOne(scheduleId: Long, slot: String, instrument: String): Assignment?

    @Insert
    suspend fun insert(a: Assignment): Long

    @Update
    suspend fun update(a: Assignment)

    @Query("DELETE FROM Assignment WHERE scheduleId=:scheduleId AND slot=:slot")
    suspend fun clearSlot(scheduleId: Long, slot: String)
}
