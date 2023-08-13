package com.atech.bit.ui.fragments.holiday.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowHolidayBinding
import com.atech.core.retrofit.client.DiffCallbackHoliday
import com.atech.core.retrofit.client.Holiday

class HolidayAdapter :
    ListAdapter<Holiday, HolidayAdapter.HolidayViewHolder>(DiffCallbackHoliday()) {

    class HolidayViewHolder(private val binding: RowHolidayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }

        fun bind(holiday: Holiday) {
            binding.apply {
                date.text = holiday.date
                day.text = holiday.day
                occasion.text = holiday.occasion
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder =
        HolidayViewHolder(
            RowHolidayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

}