package com.atech.bit.ui.fragments.about.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.bit.databinding.RowAboutUsBottomViewBinding
import com.atech.bit.databinding.RowAboutUsTopViewBinding
import com.atech.bit.databinding.RowDevlopersBinding
import com.atech.theme.databinding.RowTitleBinding
import com.atech.theme.loadCircular
import com.atech.theme.setHorizontalBias

sealed class AboutUsViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    class TitleHolder(
        private val binding: RowTitleBinding,
    ) : AboutUsViewHolder(binding) {
        fun bind(title: AboutUsItem.Title) {
            binding.root.text = title.title
        }
    }

    class AppVersionHolder(
        private val binding: RowAboutUsTopViewBinding,
    ) : AboutUsViewHolder(binding) {
        fun bind(version: AboutUsItem.AppVersion) {
            binding.textView4.text = version.version
        }
    }

    class DevViewHolder(
        private val binding: RowDevlopersBinding,
    ) : AboutUsViewHolder(binding) {
        fun bind(dev: AboutUsItem.Dev) {
            val dev = dev.devs
            binding.apply {
                photo.loadCircular(dev.imageLink)
                name.text = dev.name
                textViewDes.isVisible = dev.des.isNotEmpty()
                if (dev.des.isEmpty())
                    binding.constraintLayoutDev.setHorizontalBias(
                        binding.name.id,
                        0.50f
                    )
                textViewDes.text = dev.des
            }
        }
    }

    class BottomView(
        binding: RowAboutUsBottomViewBinding
    ) : AboutUsViewHolder(binding)

}