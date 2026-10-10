package com.mariams2006.criminalintent.data

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisecondsSinceEpoch: Long?): Date? {
        return millisecondsSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String?): UUID? {
        return uuidString?.let { UUID.fromString(it) }
    }
}