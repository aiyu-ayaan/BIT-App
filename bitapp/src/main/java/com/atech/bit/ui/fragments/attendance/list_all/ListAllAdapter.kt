/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/28/21, 11:16 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/28/21, 10:35 PM
 */



package com.atech.bit.ui.fragments.attendance.list_all

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowListAllBinding
import com.atech.core.data.room.attendance.AttendanceModel

class ListAllAdapter(private val listener: ClickListenerListAll) :
    ListAdapter<AttendanceModel, ListAllAdapter.ListAllViewModel>(ListAllDiffUtil()) {
    inner class ListAllViewModel(
        private val binding: RowListAllBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                ivEdit.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val attendance = getItem(position)
                        if (attendance != null) {
                            listener.setEditClick(attendance)
                        }
                    }
                }
                ivDelete.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val attendance = getItem(position)
                        if (attendance != null) {
                            listener.setDeleteClick(attendance)
                        }
                    }
                }
                ivReset.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val attendance = getItem(position)
                        if (attendance != null) {
                            listener.setResetClick(attendance)
                        }
                    }
                }
            }
        }

        fun bind(attendance: AttendanceModel) {
            binding.apply {
                tvSubject.text = attendance.subject
                tvTotal.text = attendance.total.toString()
                tvPresent.text = attendance.present.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAllViewModel =
        ListAllViewModel(
            RowListAllBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ListAllViewModel, position: Int) {
        holder.bind(getItem(position))
    }

    class ListAllDiffUtil : DiffUtil.ItemCallback<AttendanceModel>() {
        override fun areItemsTheSame(oldItem: AttendanceModel, newItem: AttendanceModel): Boolean =
            newItem.id == oldItem.id

        override fun areContentsTheSame(
            oldItem: AttendanceModel,
            newItem: AttendanceModel
        ): Boolean =
            newItem == oldItem
    }

    interface ClickListenerListAll {
        fun setEditClick(attendance: AttendanceModel)
        fun setResetClick(attendance: AttendanceModel)
        fun setDeleteClick(attendance: AttendanceModel)
    }
}