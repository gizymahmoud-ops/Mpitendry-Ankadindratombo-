package dev.vmail.mpitendry.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Musician::class, Schedule::class, Assignment::class],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract fun musicianDao(): MusicianDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "mpitendry.db"
                ).build().also { INSTANCE = it }
            }
    }
}
