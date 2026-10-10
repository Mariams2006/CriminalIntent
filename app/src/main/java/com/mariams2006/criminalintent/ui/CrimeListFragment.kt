package com.mariams2006.criminalintent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mariams2006.criminalintent.R
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeListFragment : Fragment() {

    private val viewModel: CrimeListViewModel by viewModels()

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var addCrimeButton: FloatingActionButton

    private val crimeListAdapter = CrimeListAdapter { crime ->
        openCrimeDetails(crime.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_crime_list,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view)

        emptyState =
            view.findViewById(R.id.empty_state)

        addCrimeButton =
            view.findViewById(R.id.add_crime_button)

        crimeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        crimeRecyclerView.adapter = crimeListAdapter

        // Add a new crime

        addCrimeButton.setOnClickListener {

            viewModel.addCrime()
        }

        // Observe the crime list

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    viewModel.crimes.collect { crimes ->

                        crimeListAdapter.submitList(crimes)

                        if (crimes.isEmpty()) {

                            crimeRecyclerView.visibility = View.GONE
                            emptyState.visibility = View.VISIBLE

                        } else {

                            crimeRecyclerView.visibility = View.VISIBLE
                            emptyState.visibility = View.GONE
                        }
                    }
                }

                // Disable Add while saving

                launch {

                    viewModel.isAddingCrime.collect { isAdding ->

                        addCrimeButton.isEnabled = !isAdding
                    }
                }

                // Open the detail screen after successful insertion

                launch {

                    viewModel.newCrime.collect { crime ->

                        if (crime != null) {

                            viewModel.onNewCrimeOpened()

                            openCrimeDetails(crime.id)
                        }
                    }
                }

                // Display database insertion errors

                launch {

                    viewModel.addCrimeError.collect { error ->

                        if (error != null) {

                            Toast.makeText(
                                requireContext(),
                                error,
                                Toast.LENGTH_LONG
                            ).show()

                            viewModel.onAddCrimeErrorShown()
                        }
                    }
                }
            }
        }
    }

    private fun openCrimeDetails(crimeId: UUID) {

        val crimeDetailFragment =
            CrimeDetailFragment.newInstance(crimeId)

        parentFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(
                R.id.fragment_container,
                crimeDetailFragment
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {

        crimeRecyclerView.adapter = null

        super.onDestroyView()
    }

    companion object {

        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}