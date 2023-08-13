package com.atech.course.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.core.utils.RowSubjectAdapterRequest
import com.atech.course.sem.adapter.SyllabusUIDiffUnit
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.databinding.RowSubjectSelectBinding

class RowSubjectAdapter(
    private val request: RowSubjectAdapterRequest = RowSubjectAdapterRequest.FROM_ATTENDANCE,
    private val onEditClick: (SyllabusUIModel) -> Unit = {},
    private val onItemClick: (SyllabusUIModel, Boolean) -> Unit = { _, _ -> }
) : ListAdapter<SyllabusUIModel, RowSubjectAdapter.RowSubjectViewHolder>(
    SyllabusUIDiffUnit()
) {
    inner class RowSubjectViewHolder(
        private val binding: RowSubjectSelectBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    onItemClick.invoke(
                        getItem(absoluteAdapterPosition),
                        binding.checkBox.isChecked
                    )
                }
            }
            binding.ibEdit.apply {
                setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onEditClick.invoke(getItem(absoluteAdapterPosition))
                    }
                }
            }
        }

        fun bind(model: SyllabusUIModel) {
            binding.apply {
                tvSubjectName.text = model.subject
                tvSubjectCode.text = model.code
                checkBox.isVisible = true
                checkBox.isClickable = false
                checkBox.isChecked = when (request) {
                    RowSubjectAdapterRequest.FROM_ATTENDANCE -> {
                        ibEdit.isVisible = model.isAdded  ?: false
                        model.isAdded ?: false
                    }

                    RowSubjectAdapterRequest.FROM_HOME ->
                        model.isChecked ?: false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowSubjectViewHolder =
        RowSubjectViewHolder(
            RowSubjectSelectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RowSubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}