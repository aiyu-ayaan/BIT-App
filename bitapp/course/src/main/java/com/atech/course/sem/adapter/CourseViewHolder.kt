package com.atech.course.sem.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.course.databinding.RowSubjectsBinding
import com.atech.theme.databinding.RowAdsViewBinding
import com.atech.theme.databinding.RowTitleBinding
import com.atech.theme.setAdsUnit

sealed class CourseViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    class TitleHolder(
        private val binding: RowTitleBinding,
    ) : CourseViewHolder(binding) {
        fun bind(title: CourseItem.Title) {
            binding.root.text = title.title
        }
    }

    class SubjectHolder(
        private val binding: RowSubjectsBinding,
        onClick: ((Int) -> Unit)? = null
    ) : CourseViewHolder(binding) {
        init {
            binding.root.setOnClickListener {
                onClick?.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(model: CourseItem.Subject) {
            binding.apply {
                binding.root.transitionName = model.data.subject
                subjectTextView.text = model.data.subject
                creditTextView.text = String.format("Credits: %d", model.data.credits)
                creditTextView.visibility =
                    if (model.data.credits == -1) View.GONE else View.VISIBLE
                subjectCodeTextView.text = model.data.code
                subjectCodeTextView.visibility =
                    if (model.data.code.isEmpty()) View.GONE else View.VISIBLE

            }
        }
    }

    class AdsViewHolder(
        private val binding: RowAdsViewBinding
    ) : CourseViewHolder(binding) {
        fun bind(adsUnit: CourseItem.Ads) {
            binding.setAdsUnit(
                adsUnit.ads
            )
        }
    }

}