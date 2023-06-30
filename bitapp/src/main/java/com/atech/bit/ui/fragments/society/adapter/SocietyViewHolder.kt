package com.atech.bit.ui.fragments.society.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.bit.databinding.RowSocietyBinding
import com.atech.theme.databinding.RowTitleBinding
import com.atech.theme.loadCircular

sealed class SocietyViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    class TitleHolder(
        private val binding: RowTitleBinding,
    ) : SocietyViewHolder(binding) {
        fun bind(title: SocietyItem.Title) {
            binding.root.text = title.title
        }
    }

    class SocietyHolder(
        private val binding: RowSocietyBinding,
        onClick: ((Int) -> Unit)? = null
    ) : SocietyViewHolder(binding) {
        init {
            binding.root.setOnClickListener {
                onClick?.invoke(adapterPosition)
            }
        }

        fun bind(society: SocietyItem.SocietyData) {
            binding.apply {
                imageViewEvent.loadCircular(society.data.logo)
                textViewSocietyName.text = society.data.name
            }
        }
    }


}