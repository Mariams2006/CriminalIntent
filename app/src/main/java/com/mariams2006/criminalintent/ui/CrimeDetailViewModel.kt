package com.mariams2006.criminalintent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mariams2006.criminalintent.data.Crime
import com.mariams2006.criminalintent.data.CrimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class CrimeDetailViewModel(
    private val crimeId: UUID
) : ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    // Observe the selected crime from the Room database

    val crime: StateFlow<Crime?> =
        crimeRepository.getCrime(crimeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    // Save changes to an existing crime

    fun updateCrime(crime: Crime) {

        viewModelScope.launch {
            crimeRepository.updateCrime(crime)
        }
    }

    // Delete a crime from the database

    fun deleteCrime(crime: Crime) {

        viewModelScope.launch {
            crimeRepository.deleteCrime(crime)
        }
    }

    // Retrieve the file used to store the crime photograph

    fun getPhotoFile(crime: Crime): File {

        return crimeRepository.getPhotoFile(crime)
    }
}

// Factory for creating a ViewModel with a specific crime ID

class CrimeDetailViewModelFactory(
    private val crimeId: UUID
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        require(
            modelClass.isAssignableFrom(
                CrimeDetailViewModel::class.java
            )
        ) {
            "Unknown ViewModel class: ${modelClass.name}"
        }

        return CrimeDetailViewModel(crimeId) as T
    }
}