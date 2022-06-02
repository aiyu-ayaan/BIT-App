/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 9:19 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/25/22, 2:09 AM
 */



package com.aatec.bit.ui.fragments.about_us

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowDevlopersBinding
import com.aatec.core.data.network.aboutus.Devs
import com.aatec.core.utils.loadImageCircular

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