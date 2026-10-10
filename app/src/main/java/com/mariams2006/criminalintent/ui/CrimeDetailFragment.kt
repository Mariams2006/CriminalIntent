package com.mariams2006.criminalintent.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.mariams2006.criminalintent.R
import com.mariams2006.criminalintent.data.Crime
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.util.Date
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private lateinit var crimeId: UUID

    private val viewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(crimeId)
    }

    private lateinit var toolbar: MaterialToolbar
    private lateinit var titleInput: TextInputEditText
    private lateinit var solvedCheckBox: MaterialCheckBox
    private lateinit var dateButton: MaterialButton
    private lateinit var crimePhoto: ImageView
    private lateinit var takePhotoButton: MaterialButton
    private lateinit var suspectName: TextView
    private lateinit var chooseSuspectButton: MaterialButton
    private lateinit var shareReportButton: MaterialButton
    private lateinit var deleteCrimeButton: MaterialButton

    private var currentCrime: Crime? = null
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var isUpdatingUi = false

    // CAMERA FEATURE

    private val takePhotoLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {

                val crime =
                    currentCrime ?: return@registerForActivityResult

                val file =
                    photoFile ?: return@registerForActivityResult

                crime.photoFileName = file.name

                viewModel.updateCrime(crime)

                updatePhoto()
            }
        }

    // CHOOSE SUSPECT FEATURE

    private val chooseSuspectLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }

            val contactUri =
                result.data?.data
                    ?: return@registerForActivityResult

            val queryFields =
                arrayOf(
                    ContactsContract.Contacts.DISPLAY_NAME
                )

            requireActivity().contentResolver
                .query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null
                )
                ?.use { cursor ->

                    if (cursor.moveToFirst()) {

                        val nameIndex =
                            cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME
                            )

                        if (nameIndex >= 0) {

                            val name =
                                cursor.getString(nameIndex)

                            currentCrime?.let { crime ->

                                crime.suspect = name

                                viewModel.updateCrime(crime)

                                suspectName.text = name
                            }
                        }
                    }
                }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        crimeId = UUID.fromString(
            requireArguments().getString(ARG_CRIME_ID)
        )

        parentFragmentManager.setFragmentResultListener(
            DatePickerFragment.REQUEST_DATE,
            this
        ) { _, bundle ->

            val date =
                Date(
                    bundle.getLong(
                        DatePickerFragment.RESULT_DATE
                    )
                )

            currentCrime?.let { crime ->

                crime.date = date

                viewModel.updateCrime(crime)

                updateDateButton(date)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_crime_detail,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        toolbar =
            view.findViewById(R.id.crime_detail_toolbar)

        titleInput =
            view.findViewById(R.id.crime_title_input)

        solvedCheckBox =
            view.findViewById(R.id.crime_solved_checkbox)

        dateButton =
            view.findViewById(R.id.crime_date_button)

        crimePhoto =
            view.findViewById(R.id.crime_photo)

        takePhotoButton =
            view.findViewById(R.id.take_photo_button)

        suspectName =
            view.findViewById(R.id.suspect_name)

        chooseSuspectButton =
            view.findViewById(R.id.choose_suspect_button)

        shareReportButton =
            view.findViewById(R.id.share_report_button)

        deleteCrimeButton =
            view.findViewById(R.id.delete_crime_button)

        // BACK BUTTON

        toolbar.setNavigationOnClickListener {

            parentFragmentManager.popBackStack()
        }

        // EDIT CRIME TITLE

        titleInput.doAfterTextChanged { editable ->

            if (!isUpdatingUi) {

                currentCrime?.let { crime ->

                    crime.title =
                        editable?.toString().orEmpty()

                    viewModel.updateCrime(crime)
                }
            }
        }

        // MARK CRIME AS SOLVED

        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->

            if (!isUpdatingUi) {

                currentCrime?.let { crime ->

                    crime.isSolved = isChecked

                    viewModel.updateCrime(crime)
                }
            }
        }

        // SELECT CRIME DATE

        dateButton.setOnClickListener {

            val crime =
                currentCrime ?: return@setOnClickListener

            DatePickerFragment
                .newInstance(crime.date)
                .show(
                    parentFragmentManager,
                    DATE_PICKER_TAG
                )
        }

        // TAKE CRIME PHOTO

        takePhotoButton.setOnClickListener {

            takePhoto()
        }

        // CHOOSE SUSPECT

        chooseSuspectButton.setOnClickListener {

            chooseSuspect()
        }

        // SHARE CRIME REPORT

        shareReportButton.setOnClickListener {

            shareCrimeReport()
        }

        // DELETE CRIME

        deleteCrimeButton.setOnClickListener {

            val crime =
                currentCrime ?: return@setOnClickListener

            viewModel.deleteCrime(crime)

            parentFragmentManager.popBackStack()
        }

        // OBSERVE CRIME DATA

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.crime.collect { crime ->

                    if (crime != null) {

                        currentCrime = crime

                        displayCrime(crime)
                    }
                }
            }
        }
    }

    // DISPLAY CRIME INFORMATION

    private fun displayCrime(crime: Crime) {

        isUpdatingUi = true

        if (titleInput.text?.toString() != crime.title) {

            titleInput.setText(crime.title)
        }

        solvedCheckBox.isChecked = crime.isSolved

        updateDateButton(crime.date)

        suspectName.text =
            crime.suspect.ifBlank {
                "No suspect selected"
            }

        photoFile =
            viewModel.getPhotoFile(crime)

        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile!!
            )

        updatePhoto()

        isUpdatingUi = false
    }

    // UPDATE DATE BUTTON

    private fun updateDateButton(date: Date) {

        dateButton.text =
            DateFormat
                .getDateInstance(DateFormat.FULL)
                .format(date)
    }

    // DISPLAY CRIME PHOTO
    // IMPROVED WITH BITMAP DOWNSAMPLING

    private fun updatePhoto() {

        val file = photoFile

        // Clear the image when no valid photo exists

        if (
            file == null ||
            !file.exists() ||
            file.length() == 0L
        ) {

            crimePhoto.setImageDrawable(null)
            return
        }

        // First decode only the image dimensions

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(
            file.absolutePath,
            options
        )

        // Validate image dimensions

        if (
            options.outWidth <= 0 ||
            options.outHeight <= 0
        ) {

            crimePhoto.setImageDrawable(null)
            return
        }

        // Calculate a suitable downsampling factor

        var sampleSize = 1

        while (
            options.outWidth / sampleSize > 1024 ||
            options.outHeight / sampleSize > 1024
        ) {

            sampleSize *= 2
        }

        // Decode the smaller bitmap

        options.inJustDecodeBounds = false
        options.inSampleSize = sampleSize

        val bitmap = BitmapFactory.decodeFile(
            file.absolutePath,
            options
        )

        // Display the optimized photo

        crimePhoto.setImageBitmap(bitmap)
    }

    // OPEN CAMERA

    private fun takePhoto() {

        val crime =
            currentCrime ?: return

        val file =
            viewModel.getPhotoFile(crime)

        photoFile = file

        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

        takePhotoLauncher.launch(photoUri!!)
    }

    // OPEN CONTACTS PICKER

    private fun chooseSuspect() {

        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI
        )

        try {

            chooseSuspectLauncher.launch(intent)

        } catch (e: ActivityNotFoundException) {

            Toast.makeText(
                requireContext(),
                "No Contacts app is available on this device.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // SHARE CRIME REPORT

    private fun shareCrimeReport() {

        val crime =
            currentCrime ?: return

        val solvedText =
            if (crime.isSolved) {
                "Solved"
            } else {
                "Unsolved"
            }

        val suspectText =
            if (crime.suspect.isBlank()) {
                "No suspect"
            } else {
                "Suspect: ${crime.suspect}"
            }

        val dateText =
            DateFormat
                .getDateInstance(DateFormat.FULL)
                .format(crime.date)

        val report =
            """
            Crime Report

            Title: ${crime.title.ifBlank { "Untitled Crime" }}
            Date: $dateText
            Status: $solvedText
            $suspectText
            """.trimIndent()

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    report
                )

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "CriminalIntent Crime Report"
                )
            }

        startActivity(
            Intent.createChooser(
                shareIntent,
                "Share crime report"
            )
        )
    }

    companion object {

        private const val ARG_CRIME_ID = "crimeId"

        private const val DATE_PICKER_TAG = "DatePicker"

        fun newInstance(
            crimeId: UUID
        ): CrimeDetailFragment {

            return CrimeDetailFragment().apply {

                arguments =
                    Bundle().apply {

                        putString(
                            ARG_CRIME_ID,
                            crimeId.toString()
                        )
                    }
            }
        }
    }
}