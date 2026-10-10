package com.mariams2006.criminalintent.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dateMilliseconds =
            requireArguments().getLong(ARG_DATE)

        val date = Date(dateMilliseconds)

        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedDate =
                    Calendar.getInstance().apply {
                        set(
                            selectedYear,
                            selectedMonth,
                            selectedDay,
                            0,
                            0,
                            0
                        )

                        set(Calendar.MILLISECOND, 0)
                    }.time

                parentFragmentManager.setFragmentResult(
                    REQUEST_DATE,
                    Bundle().apply {
                        putLong(
                            RESULT_DATE,
                            selectedDate.time
                        )
                    }
                )
            },
            year,
            month,
            day
        )
    }

    companion object {

        const val REQUEST_DATE = "requestDate"
        const val RESULT_DATE = "resultDate"

        private const val ARG_DATE = "argDate"

        fun newInstance(date: Date): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATE, date.time)
                }
            }
        }
    }
}