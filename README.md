# CriminalIntent

### Android Mobile Application | Arkansas State University

CriminalIntent is an Android application developed using Kotlin for my Mobile Application Development course at Arkansas State University.

The application allows users to create, manage, and track crime records. Each record can include a title, date, solved status, photograph, and suspect information.

## Features

- **Crime Management:** Add, view, edit, and delete crime records.
- **Crime Details:** View and modify individual crime information.
- **Solved Status:** Mark crimes as solved or unsolved, with a visual indicator in the crime list.
- **Date Selection:** Select crime dates using a DatePickerDialog.
- **Camera Integration:** Capture and attach photographs to crime records.
- **Suspect Selection:** Select suspects from the device's Contacts application.
- **Crime Reports:** Share crime information through Android's sharing functionality.
- **Local Storage:** Save crime records using Room Database.
- **Data Persistence:** Preserve crime records after closing and reopening the application.
- **Screen Rotation:** Support portrait and landscape orientations.

## Technologies Used

- Kotlin
- Android Studio
- Android SDK
- Room Database
- MVVM Architecture
- ViewModel and StateFlow
- Kotlin Coroutines
- RecyclerView
- Fragments and DialogFragment
- Material Design Components
- Android Activity Result APIs
- FileProvider

## Application Architecture

The application follows the Model-View-ViewModel (MVVM) architecture.

**Model**
- Crime data class
- Room Database
- CrimeDao
- CrimeRepository

**View**
- CrimeListFragment
- CrimeDetailFragment
- DatePickerFragment
- XML layouts

**ViewModel**
- CrimeListViewModel
- CrimeDetailViewModel

## How to Run

1. Clone this repository.
2. Open the CriminalIntent project in Android Studio.
3. Allow Gradle to synchronize.
4. Start an Android emulator or connect an Android device.
5. Run the application.

## Developer

**Mariam Sow**

Arkansas State University  
Information Systems and Business Analytics