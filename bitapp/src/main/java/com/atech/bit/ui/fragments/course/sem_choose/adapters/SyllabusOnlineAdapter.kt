package com.atech.bit.ui.fragments.course.sem_choose.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowSyllabusOnlineBinding
import com.atech.core.api.syllabus.DiffUtilLabSyllabusCallback
import com.atech.core.api.syllabus.DiffUtilTheorySyllabusCallback
import com.atech.core.api.syllabus.Lab
import com.atech.core.api.syllabus.Theory

class SyllabusTheoryOnlineAdapter(
    private val onClick: (Theory) -> Unit
) : ListAdapter<Theory, SyllabusOnlineViewHolder<Theory>>(DiffUtilTheorySyllabusCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SyllabusOnlineViewHolder<Theory> = SyllabusOnlineViewHolder(
        RowSyllabusOnlineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) { pos ->
        if (pos != RecyclerView.NO_POSITION) onClick(getItem(pos))
    }

    override fun onBindViewHolder(holder: SyllabusOnlineViewHolder<Theory>, position: Int) {
        holder.bind(getItem(position)) { binding, theory ->
            binding.apply {
                subjectTextView.text = theory.subjectName.trim()
                subjectCodeTextView.text = binding.root.context.resources.getString(
                    R.string.theory_code, theory.code, theory.type
                )
                creditTextView.text = theory.credit.toString()
            }
        }
    }
}

// Lab Adapter

class SyllabusLabOnlineAdapter(
    private val onClick: (Lab) -> Unit
) : ListAdapter<Lab, SyllabusOnlineViewHolder<Lab>>(DiffUtilLabSyllabusCallback()) {
    private var startPos: Int? = null

    fun setStartPos(pos: Int) {
        startPos = pos
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SyllabusOnlineViewHolder<Lab> = SyllabusOnlineViewHolder(
        RowSyllabusOnlineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) { pos ->
        if (pos != RecyclerView.NO_POSITION) startPos?.let {
            onClick(getItem(pos - it))
        }
    }

    override fun onBindViewHolder(holder: SyllabusOnlineViewHolder<Lab>, position: Int) {
        holder.bind(getItem(position)) { binding, lab ->
            binding.apply {

                subjectTextView.text = lab.subjectName.trim()
                subjectCodeTextView.text = binding.root.context.resources.getString(
                    R.string.lab_code, lab.code, lab.type
                )
                creditTextView.text = lab.credit.toString()
            }
        }
    }
}