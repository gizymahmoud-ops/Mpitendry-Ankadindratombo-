package dev.vmail.mpitendry.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Musician::class,
        Assignment::class
    ],
    version = 1,
    exportSchema = false // 🔥 IMPORTANT
)
abstract class AppDb : RoomDatabase() {

    abstract fun musicianDao(): MusicianDao
    abstract fun assignmentDao(): AssignmentDao
}
