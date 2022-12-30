/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 9:19 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/25/22, 2:09 AM
 */



package com.atech.bit.ui.fragments.about_us

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowDevlopersBinding
import com.atech.core.api.aboutus.Devs
import com.atech.core.utils.loadImageCircular
import com.atech.core.utils.setHorizontalBias

class DevsAdapter(
    private val listener: (devs: Devs) -> Unit
) : ListAdapter<Devs, DevsAdapter.DevsViewHolder>(DiffUtilsDevs()) {

    inner class DevsViewHolder(private val binding: RowDevlopersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.invoke(getItem(position))
                }
            }
        }


        fun bind(devs: Devs) {
            binding.root.transitionName = devs.name
            binding.apply {
                devs.img_link.loadImageCircular(
                    itemView,
                    photo,
                    binding.progressBarDev,
                    R.drawable.ic_running_error
                )
                name.text = devs.name
                textViewDes.isVisible = devs.des.isNotEmpty()
                if(devs.des.isEmpty())
                    binding.constraintLayoutDev.setHorizontalBias(
                        binding.name.id,
                        0.50f
                    )
                textViewDes.text = devs.des
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevsViewHolder =
        DevsViewHolder(
            RowDevlopersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: DevsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffUtilsDevs : DiffUtil.ItemCallback<Devs>() {
        override fun areItemsTheSame(oldItem: Devs, newItem: Devs): Boolean =
            oldItem.sno == newItem.sno

        override fun areContentsTheSame(oldItem: Devs, newItem: Devs): Boolean =
            newItem.name == oldItem.name
    }

}