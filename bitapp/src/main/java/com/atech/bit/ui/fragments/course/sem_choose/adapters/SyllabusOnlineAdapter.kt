package com.atech.bit.ui.fragments.course.sem_choose.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowSyllabusOnlineBinding
import com.atech.core.api.model.DiffUtilLabSyllabusCallback
import com.atech.core.api.model.DiffUtilTheorySyllabusCallback
import com.atech.core.api.model.Lab
import com.atech.core.api.model.Theory

class SyllabusTheoryOnlineAdapter(
    private val onClick: (Theory) -> Unit
) :
    ListAdapter<Theory, SyllabusOnlineViewHolder<Theory>>(DiffUtilTheorySyllabusCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SyllabusOnlineViewHolder<Theory> =
        SyllabusOnlineViewHolder(
            RowSyllabusOnlineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) { pos ->
            if (pos != RecyclerView.NO_POSITION)
                onClick(getItem(pos))
        }

    override fun onBindViewHolder(holder: SyllabusOnlineViewHolder<Theory>, position: Int) {
        holder.bind(getItem(position)) { binding, theory ->
            binding.apply {
                subjectTextView.text = theory.subjectName.trim()
                subjectCodeTextView.text = binding.root.context
                    .resources.getString(R.string.theory_code, theory.code)
                creditTextView.text = theory.credit.toString()
            }
        }
    }
}

// Lab Adapter

class SyllabusLabOnlineAdapter :
    ListAdapter<Lab, SyllabusOnlineViewHolder<Lab>>(DiffUtilLabSyllabusCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SyllabusOnlineViewHolder<Lab> =
        SyllabusOnlineViewHolder(
            RowSyllabusOnlineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SyllabusOnlineViewHolder<Lab>, position: Int) {
        holder.bind(getItem(position)) { binding, lab ->
            binding.apply {

                subjectTextView.text = lab.subjectName.trim()
                subjectCodeTextView.text = binding.root.context
                    .resources.getString(R.string.lab_code, lab.code)
                creditTextView.text = lab.credit.toString()
            }
        }
    }
}