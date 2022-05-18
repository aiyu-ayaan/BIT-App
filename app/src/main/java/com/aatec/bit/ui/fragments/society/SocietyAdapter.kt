/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/2/21, 1:15 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/2/21, 1:15 PM
 */



package com.aatec.bit.ui.fragments.society

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowSocietyBinding
import com.aatec.core.utils.loadImage
import com.aatec.core.data.network.society.SocietyNetworkEntity

class SocietyAdapter(
    private val listener: (society: SocietyNetworkEntity, view: View) -> Unit
) :
    ListAdapter<SocietyNetworkEntity, SocietyAdapter.SocietyViewHolder>(SocietyDiff()) {

    inner class SocietyViewHolder(
        private val binding: RowSocietyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.invoke(getItem(position), binding.root)
                }
            }
        }

        fun onBind(societyNetworkEntity: SocietyNetworkEntity) {
            binding.apply {
                binding.root.transitionName = societyNetworkEntity.name
                textViewAbout.text = societyNetworkEntity.name

                societyNetworkEntity.logo_link.loadImage(
                    itemView, eventImg, progressBarEvent,
                    DEFAULT_BUFFER_SIZE,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocietyViewHolder =
        SocietyViewHolder(
            RowSocietyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SocietyViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private class SocietyDiff : DiffUtil.ItemCallback<SocietyNetworkEntity>() {
        override fun areItemsTheSame(
            oldItem: SocietyNetworkEntity,
            newItem: SocietyNetworkEntity
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: SocietyNetworkEntity,
            newItem: SocietyNetworkEntity
        ): Boolean = oldItem.name == newItem.name &&
                oldItem.des == newItem.des &&
                oldItem.ins == newItem.ins &&
                oldItem.logo_link == newItem.logo_link
    }
}