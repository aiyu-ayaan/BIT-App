package com.atech.bit.ui.fragments.course.sem_choose.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowSyllabusOnlineBinding
import com.atech.core.api.syllabus.DiffUtilTheorySyllabusCallback
import com.atech.core.api.syllabus.SubjectModel

class SyllabusOnlineAdapter(
    private val onClick: (SubjectModel) -> Unit
) : ListAdapter<SubjectModel, SyllabusOnlineViewHolder<SubjectModel>>(DiffUtilTheorySyllabusCallback()) {

    private var type = "Theory"
    private var startPos: Int = 0

    fun setType(type: String) {
        this.type = type
    }

    fun setStartPos(pos: Int) {
        startPos = pos
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SyllabusOnlineViewHolder<SubjectModel> = SyllabusOnlineViewHolder(
        RowSyllabusOnlineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) { pos ->
        if (pos != RecyclerView.NO_POSITION)
            onClick(getItem(pos - startPos))
    }

    override fun onBindViewHolder(holder: SyllabusOnlineViewHolder<SubjectModel>, position: Int) {
        holder.bind(getItem(position)) { binding, theory ->
            binding.apply {
                subjectTextView.text = theory.subjectName.trim()
                subjectCodeTextView.text = binding.root.context.resources.getString(
                    R.string.theory_code, type, theory.code
                )
                creditTextView.text = theory.credit.toString()
            }
        }
    }
}