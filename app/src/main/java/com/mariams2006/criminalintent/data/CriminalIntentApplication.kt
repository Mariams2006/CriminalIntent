package com.mariams2006.criminalintent

import android.app.Application
import com.mariams2006.criminalintent.data.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CrimeRepository.initialize(this)
    }
}