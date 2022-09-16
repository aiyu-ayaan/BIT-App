package com.atech.bit.ui.fragments.course.sem_choose.adapters

import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowSyllabusOnlineBinding

class SyllabusOnlineViewHolder<T>(
    private val binding: RowSyllabusOnlineBinding,
    private val listener: (Int) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            listener.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(theory: T, action: (RowSyllabusOnlineBinding, t: T) -> Unit) {
        action.invoke(binding, theory)
    }
}