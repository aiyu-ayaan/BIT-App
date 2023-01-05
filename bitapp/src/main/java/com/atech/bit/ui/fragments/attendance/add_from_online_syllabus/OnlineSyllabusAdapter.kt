package com.atech.bit.ui.fragments.attendance.add_from_online_syllabus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.databinding.RowSyllabusHomeBinding
import com.atech.core.api.syllabus.DiffUtilTheorySyllabusCallback
import com.atech.core.api.syllabus.SubjectModel
import com.atech.core.data.room.attendance.AttendanceModel

class OnlineSyllabusAdapter(
    private val attendanceList: LiveData<List<AttendanceModel>>,
    private val owner: LifecycleOwner,
    private val editListener: ((SubjectModel) -> Unit)? = null,
    private val listener: ((SubjectModel, Boolean) -> Unit)? = null
) :
    ListAdapter<SubjectModel, OnlineSyllabusAdapter.OnlineSyllabusViewHolder>(
        DiffUtilTheorySyllabusCallback()
    ) {


    inner class OnlineSyllabusViewHolder(
        private val binding: RowSyllabusHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    val item = getItem(absoluteAdapterPosition)
                    binding.ibEdit.isVisible = binding.checkBox.isChecked
                    listener?.invoke(item, binding.checkBox.isChecked)
                }
            }
            binding.ibEdit.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(absoluteAdapterPosition)
                    editListener?.invoke(item)
                }
            }
        }

        fun bind(model: SubjectModel) {
            binding.apply {
                tvSem.isVisible = false
                tvSubjectName.text = model.subjectName
                tvSubjectCode.text = model.code
                checkBox.apply {
                    isVisible = true
                    isClickable = false
                }

                attendanceList.observe(owner) {
                    it.forEach { attendanceModel ->
                        if (attendanceModel.subject.equals(model.subjectName, false)) {
                            checkBox.isChecked = true
                            ibEdit.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineSyllabusViewHolder =
        OnlineSyllabusViewHolder(
            RowSyllabusHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OnlineSyllabusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}