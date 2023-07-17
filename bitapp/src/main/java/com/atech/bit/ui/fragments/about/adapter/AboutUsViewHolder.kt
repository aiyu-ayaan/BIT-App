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
        private val onClick: (Int) -> Unit
    ) : AboutUsViewHolder(binding) {
        init {
            binding.root.setOnClickListener {
                onClick(absoluteAdapterPosition)
            }
        }

        fun bind(dev: AboutUsItem.Dev) {
            val dev1 = dev.devs
            binding.apply {
                photo.loadCircular(dev1.imageLink)
                name.text = dev1.name
                textViewDes.isVisible = dev1.des.isNotEmpty()
                if (dev1.des.isEmpty())
                    binding.constraintLayoutDev.setHorizontalBias(
                        binding.name.id,
                        0.50f
                    )
                textViewDes.text = dev1.des
            }
        }
    }

    class BottomView(
        binding: RowAboutUsBottomViewBinding,
        private val onComponentUseClick: () -> Unit,
        private val onPrivacyClick: (String) -> Unit,
        private val onPlayStoreClick: () -> Unit
    ) : AboutUsViewHolder(binding) {
        init {
            binding.textViewComponent.setOnClickListener {
                onComponentUseClick.invoke()
            }
            binding.textViewPrivacy.setOnClickListener {
                onPrivacyClick.invoke(
                    binding.root.context
                        .getString(com.atech.theme.R.string.privacy_policy_link)
                )
            }
            binding.textViewPlayStore.setOnClickListener {
                onPlayStoreClick.invoke()
            }
        }
    }
}