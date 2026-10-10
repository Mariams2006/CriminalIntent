package com.mariams2006.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mariams2006.criminalintent.ui.CrimeListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CrimeListFragment.newInstance()
                )
                .commit()
        }
    }
}