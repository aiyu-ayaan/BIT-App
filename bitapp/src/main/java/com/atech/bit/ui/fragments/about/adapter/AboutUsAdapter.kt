package com.atech.bit.ui.fragments.about.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowAboutUsBottomViewBinding
import com.atech.bit.databinding.RowAboutUsTopViewBinding
import com.atech.bit.databinding.RowDevlopersBinding
import com.atech.theme.databinding.RowTitleBinding

class AboutUsAdapter : RecyclerView.Adapter<AboutUsViewHolder>() {

    var list = listOf<AboutUsItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutUsViewHolder =
        when (viewType) {
            R.layout.row_about_us_top_view -> AboutUsViewHolder.AppVersionHolder(
                RowAboutUsTopViewBinding.inflate(
                    android.view.LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_about_us_bottom_view -> AboutUsViewHolder.BottomView(
                RowAboutUsBottomViewBinding.inflate(
                    android.view.LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_devlopers -> AboutUsViewHolder.DevViewHolder(
                RowDevlopersBinding.inflate(
                    android.view.LayoutInflater.from(parent.context), parent, false
                )
            )

            com.atech.theme.R.layout.row_title -> AboutUsViewHolder.TitleHolder(
                RowTitleBinding.inflate(
                    android.view.LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }


    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AboutUsViewHolder, position: Int) =
        when (holder) {
            is AboutUsViewHolder.AppVersionHolder -> holder.bind(list[position] as AboutUsItem.AppVersion)
            is AboutUsViewHolder.BottomView -> Unit
            is AboutUsViewHolder.DevViewHolder -> holder.bind(list[position] as AboutUsItem.Dev)
            is AboutUsViewHolder.TitleHolder -> holder.bind(list[position] as AboutUsItem.Title)
        }

    override fun getItemViewType(position: Int): Int =
        when (list[position]) {
            is AboutUsItem.AppVersion -> R.layout.row_about_us_top_view
            AboutUsItem.BottomView -> R.layout.row_about_us_bottom_view
            is AboutUsItem.Dev -> R.layout.row_devlopers
            is AboutUsItem.Title -> com.atech.theme.R.layout.row_title
        }
}