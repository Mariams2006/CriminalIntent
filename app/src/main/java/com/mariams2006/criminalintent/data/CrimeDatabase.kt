package com.mariams2006.criminalintent.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Crime::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object {

        @Volatile
        private var INSTANCE: CrimeDatabase? = null

        fun getDatabase(context: Context): CrimeDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrimeDatabase::class.java,
                    "crime-database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}