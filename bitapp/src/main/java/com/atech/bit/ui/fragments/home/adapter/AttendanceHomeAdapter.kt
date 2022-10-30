package com.atech.bit.ui.fragments.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowAttendanceHomeBinding
import com.atech.bit.ui.fragments.attendance.AttendanceAdapter
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.utils.findPercentage
import java.math.RoundingMode
import java.text.DecimalFormat

class AttendanceHomeAdapter(
    private val defPercentage: Int = 75,
) :
    ListAdapter<AttendanceModel, AttendanceHomeAdapter.AttendanceHomeViewHolder>(AttendanceAdapter.DiffUtilAttendance()) {

    inner class AttendanceHomeViewHolder(
        private val binding: RowAttendanceHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(attendanceModel: AttendanceModel) {
            binding.apply {
                textViewSubjectName.text = attendanceModel.subject
                textViewClasses.text = binding.root.context.getString(
                    R.string.present_by_total,
                    attendanceModel.present.toString(),
                    attendanceModel.total.toString()
                )
                val percentage =
                    findPercentage(
                        attendanceModel.present.toFloat(),
                        attendanceModel.total.toFloat()
                    ) { present, total ->
                        when (total) {
                            0.0F -> 0.0F
                            else -> ((present / total) * 100)
                        }
                    }
                circularProgressIndicator.progress = defPercentage
                progressBarShowAttendanceProgress.progress = percentage.toInt()

                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                textViewAttendancePercentage.text = df.format(percentage) + "%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHomeViewHolder =
        AttendanceHomeViewHolder(
            RowAttendanceHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AttendanceHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}