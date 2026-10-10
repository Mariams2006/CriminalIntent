package com.mariams2006.criminalintent.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariams2006.criminalintent.R
import com.mariams2006.criminalintent.data.Crime
import java.text.DateFormat
import java.util.Date

class CrimeListAdapter(
    private val onCrimeClicked: (Crime) -> Unit
) : ListAdapter<Crime, CrimeListAdapter.CrimeHolder>(CrimeDiffCallback()) {

    override fun submitList(list: List<Crime>?) {

        // Create independent snapshots of incoming crime records.
        // This prevents later changes to mutable Crime objects
        // from silently changing the adapter's existing list.

        val crimeSnapshots = list?.map { crime ->
            crime.copy(
                date = Date(crime.date.time)
            )
        }

        super.submitList(crimeSnapshots)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.list_item_crime,
                parent,
                false
            )

        return CrimeHolder(view, onCrimeClicked)
    }

    override fun onBindViewHolder(
        holder: CrimeHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }

    class CrimeHolder(
        itemView: View,
        private val onCrimeClicked: (Crime) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView =
            itemView.findViewById(R.id.crime_title)

        private val dateTextView: TextView =
            itemView.findViewById(R.id.crime_date)

        private val solvedImageView: ImageView =
            itemView.findViewById(R.id.crime_solved)

        private lateinit var crime: Crime

        init {

            itemView.setOnClickListener {

                if (::crime.isInitialized) {
                    onCrimeClicked(crime)
                }
            }
        }

        fun bind(crime: Crime) {

            this.crime = crime

            // Display the crime title

            titleTextView.text =
                crime.title.ifBlank {
                    "Untitled Crime"
                }

            // Display the crime date

            dateTextView.text =
                DateFormat
                    .getDateInstance(DateFormat.MEDIUM)
                    .format(crime.date)

            // Show the solved icon when appropriate

            solvedImageView.visibility =
                if (crime.isSolved) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private class CrimeDiffCallback :
        DiffUtil.ItemCallback<Crime>() {

        override fun areItemsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {

            return oldItem == newItem
        }
    }
}