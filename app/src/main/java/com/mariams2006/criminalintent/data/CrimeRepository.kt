package com.mariams2006.criminalintent.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.UUID

class CrimeRepository private constructor(
    private val context: Context
) {

    private val database: CrimeDatabase =
        CrimeDatabase.getDatabase(context.applicationContext)

    private val crimeDao = database.crimeDao()

    fun getCrimes(): Flow<List<Crime>> {
        return crimeDao.getCrimes()
    }

    fun getCrime(id: UUID): Flow<Crime?> {
        return crimeDao.getCrime(id)
    }

    suspend fun addCrime(crime: Crime) {
        crimeDao.addCrime(crime)
    }

    suspend fun updateCrime(crime: Crime) {
        crimeDao.updateCrime(crime)
    }

    suspend fun deleteCrime(crime: Crime) {
        crimeDao.deleteCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        val fileName = crime.photoFileName
            ?: "IMG_${crime.id}.jpg"

        return File(context.filesDir, fileName)
    }

    companion object {

        @Volatile
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = CrimeRepository(
                            context.applicationContext
                        )
                    }
                }
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE
                ?: throw IllegalStateException(
                    "CrimeRepository must be initialized first."
                )
        }
    }
}