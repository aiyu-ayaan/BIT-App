package com.atech.bit.ui.fragments.global_search.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atech.bit.R
import com.atech.bit.databinding.LayoutTitleBinding
import com.atech.bit.databinding.RowHolidayHomeBinding
import com.atech.bit.databinding.RowNotice3Binding
import com.atech.bit.databinding.RowSyllabusHomeBinding
import com.atech.bit.ui.fragments.global_search.model.SearchItem
import com.atech.bit.utils.getDate
import com.atech.core.utils.DEFAULT_CORNER_RADIUS
import com.atech.core.utils.getImageLinkNotification
import com.atech.core.utils.loadImage
import com.atech.core.utils.loadImageCircular

sealed class SearchRecyclerViewHolder(
    binding: ViewBinding,
    onClick : ((view : View, item : SearchRecyclerViewHolder, position : Int) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {

    class Title(
        private val binding: LayoutTitleBinding,
    ) : SearchRecyclerViewHolder(binding,null) {
        fun bind(title: SearchItem.Title) {
            binding.textTitle.text = title.title
        }
    }

    class Syllabus(
        private val binding: RowSyllabusHomeBinding,
        private val onClick: (view: View, item: SearchRecyclerViewHolder, position: Int) -> Unit
    ) : SearchRecyclerViewHolder(binding, onClick) {

        init {
            binding.root.setOnClickListener {
                onClick(it, this, absoluteAdapterPosition)
            }
        }
        fun bind(syllabus: SearchItem.Syllabus) {
            binding.apply {
                binding.root.transitionName = syllabus.data.openCode
                checkBox.isVisible = false
                tvSubjectName.text = syllabus.data.subject
                tvSubjectCode.text = syllabus.data.code
                tvSem.text =
                    if (syllabus.data.openCode.length == 6) syllabus.data.openCode.dropLast(2)
                    else syllabus.data.openCode.dropLast(1)
            }
        }
    }

    class Holiday(
        private val binding: RowHolidayHomeBinding
    ) : SearchRecyclerViewHolder(binding) {
        fun bind(holiday: SearchItem.Holiday) {
            binding.apply {
                binding.apply {
                    date.text = holiday.data.date
                    day.text = holiday.data.day
                    occasion.text = holiday.data.occasion
                    type.text = holiday.data.type
                }
            }
        }
    }

    class Notice(
        private val binding: RowNotice3Binding,
        private val onClick: (view: View, item: SearchRecyclerViewHolder, position: Int) -> Unit
    ) : SearchRecyclerViewHolder(binding) {

        init {
            binding.root.setOnClickListener {
                onClick.invoke(it, this, absoluteAdapterPosition)
            }
        }
        fun bind(notice: SearchItem.Notice) {
            binding.apply {
                binding.root.transitionName = notice.data.title
                subjectTextView.text = notice.data.title
                bodyPreviewTextView.text = notice.data.body.replace("<br/>", "\n")
                senderTextView.text = binding.root.context.resources.getString(
                    R.string.notice_sender,
                    notice.data.sender
                )
                textViewDate.text =
                    notice.data.created.getDate()

                notice.data.getImageLinkNotification().loadImage(
                    binding.root,
                    binding.senderProfileImageView,
                    binding.progressBarNoticePreview,
                    DEFAULT_CORNER_RADIUS,
                    R.drawable.ic_running_error
                )
            }
        }
    }

    class Event(
        private val binding: RowNotice3Binding,
        private val onClick: (view: View, item: SearchRecyclerViewHolder, position: Int) -> Unit
    ) : SearchRecyclerViewHolder(binding) {

        init {
            binding.root.setOnClickListener {
                onClick.invoke(it, this, absoluteAdapterPosition)
            }
        }
        fun bind(event: SearchItem.Event) {
            binding.apply {
                binding.root.transitionName = event.data.path
                binding.textViewDate.text = event.data.created.getDate()
                binding.bodyPreviewTextView.text = event.data.content
                binding.senderTextView.text = event.data.society
                binding.subjectTextView.text = event.data.title
                event.data.logo_link.loadImageCircular(
                    binding.root,
                    binding.senderProfileImageView,
                    binding.progressBarNoticePreview,
                    R.drawable.ic_running_error
                )
            }
        }
    }

}
