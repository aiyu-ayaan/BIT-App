/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 9:19 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/24/22, 10:04 PM
 */

package com.aatec.bit.ui.fragments.timetable

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowTimeTableDefaultBinding
import com.aatec.core.data.ui.timeTable.TimeTableModel
import com.aatec.core.utils.convertLongToTime
import com.aatec.core.utils.loadImageDefault

class TimeTableAdapter(
    private val listener: (link: String, title: String) -> Unit
) :
    RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {

    private var list: List<TimeTableModel> = emptyList()

    inner class TimeTableViewHolder(
        private val binding: RowTimeTableDefaultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    val item = list[pos]
                    listener.invoke(
                        item.imageLink,
                        binding.root.context.resources.getString(
                            R.string.time_table_title,
                            item.course,
                            item.sem,
                            item.gender,
                            item.section
                        )
                    )
                }
            }
        }

        fun bind(tt: TimeTableModel) {
            binding.apply {
                tt.imageLink.loadImageDefault(
                    binding.root,
                    binding.imageViewTimeTable,
                    binding.progressBarDev,
                    R.drawable.ic_running_error
                )
                textViewTimeTableName.text = binding.root.context.resources.getString(
                    R.string.time_table_title, tt.course, tt.sem, tt.gender, tt.section
                )
                textViewTimeTableUpdated.text = binding.root.context.resources.getString(
                    R.string.time_table_update, tt.created.convertLongToTime("dd/MM/yyyy")
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder =
        TimeTableViewHolder(
            RowTimeTableDefaultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(tt: List<TimeTableModel>) {
        list = tt
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int =
        list.size
}