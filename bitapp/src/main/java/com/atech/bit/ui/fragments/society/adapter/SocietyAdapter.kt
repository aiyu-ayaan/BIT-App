package com.atech.bit.ui.fragments.society.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowSocietyBinding
import com.atech.theme.databinding.RowTitleBinding

class SocietyAdapter(
    private val onClick: ((SocietyItem) -> Unit)? = null
) : RecyclerView.Adapter<SocietyViewHolder>() {

    var item = mutableListOf<SocietyItem>()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocietyViewHolder =
        when (viewType) {
            com.atech.theme.R.layout.row_title -> SocietyViewHolder.TitleHolder(
                RowTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.row_society -> SocietyViewHolder.SocietyHolder(
                RowSocietyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ) { pos ->
                onClick?.invoke(item[pos])
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: SocietyViewHolder, position: Int) {
        when (holder) {
            is SocietyViewHolder.TitleHolder -> holder.bind(item[position] as SocietyItem.Title)
            is SocietyViewHolder.SocietyHolder -> holder.bind(item[position] as SocietyItem.SocietyData)
        }
    }

    override fun getItemViewType(position: Int): Int = when (item[position]) {
        is SocietyItem.Title -> com.atech.theme.R.layout.row_title
        is SocietyItem.SocietyData -> R.layout.row_society
    }

}