package com.mariams2006.criminalintent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariams2006.criminalintent.data.Crime
import com.mariams2006.criminalintent.data.CrimeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    val crimes: StateFlow<List<Crime>> =
        crimeRepository.getCrimes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _isAddingCrime = MutableStateFlow(false)

    val isAddingCrime: StateFlow<Boolean> =
        _isAddingCrime.asStateFlow()

    private val _newCrime = MutableStateFlow<Crime?>(null)

    val newCrime: StateFlow<Crime?> =
        _newCrime.asStateFlow()

    private val _addCrimeError = MutableStateFlow<String?>(null)

    val addCrimeError: StateFlow<String?> =
        _addCrimeError.asStateFlow()

    fun addCrime() {

        if (_isAddingCrime.value) {
            return
        }

        _isAddingCrime.value = true
        _addCrimeError.value = null

        viewModelScope.launch {

            try {

                val crime = Crime()

                crimeRepository.addCrime(crime)

                _newCrime.value = crime

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _addCrimeError.value =
                    "Unable to save the crime. Please try again."

            } finally {

                _isAddingCrime.value = false
            }
        }
    }

    fun onNewCrimeOpened() {
        _newCrime.value = null
    }

    fun onAddCrimeErrorShown() {
        _addCrimeError.value = null
    }
}