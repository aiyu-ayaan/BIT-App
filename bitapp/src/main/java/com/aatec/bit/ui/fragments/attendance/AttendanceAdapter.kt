package com.aatec.bit.ui.fragments.attendance

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.R
import com.aatec.bit.databinding.RowAttendanceSubBinding
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.utils.calculatedDays
import com.aatec.core.utils.findPercentage
import com.aatec.core.utils.setResources
import kotlin.math.ceil


class AttendanceAdapter(
    private val onItemClickListener: (attendance: AttendanceModel) -> Unit,
    private val onCheckClick: (attendance: AttendanceModel) -> Unit,
    private val onWrongClick: (attendance: AttendanceModel) -> Unit,
    private val onLongClick : (attendance: AttendanceModel) -> Unit
) :
    ListAdapter<AttendanceModel, AttendanceAdapter.AttendanceViewHolder>(DiffUtilAttendance()) {
    private var minPercentage = 75

    inner class AttendanceViewHolder(private val binding: RowAttendanceSubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                binding.root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        getItem(position)?.let {
                            onItemClickListener.invoke(it)
                        }
                }
                ivCheck.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        onCheckClick.invoke(getItem(position))
                }
                ivWrong.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        onWrongClick.invoke(getItem(position))
                }
                root.setOnLongClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        onLongClick.invoke(getItem(position))
                    true
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: AttendanceModel) {
            binding.apply {
                total.text = model.total.toString()
                present.text = model.present.toString()
                subjectName.text = model.subject
                teacherName.text = model.teacher
                teacherName.isVisible = model.teacher != ""

//                Percentage Logic

                val percentage = findPercentage(
                    model.present.toFloat(),
                    model.total.toFloat()
                ) { present, total ->
                    when (total) {
                        0.0F -> 0.0F
                        else -> (present / total) * 100
                    }
                }

                textViewPercentage.text = "${percentage.toInt()}%"
                progressCircular.progress = percentage.toInt()

                setResources(percentage.toInt()) { per ->
                    when {
                        per == 0 -> {
                            ivStatus.setImageResource(R.drawable.white)
                            status.text = ""
                        }
                        per >= minPercentage -> {
                            ivStatus.setImageResource(R.drawable.recgreen)
                            progressCircular.setIndicatorColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.green
                                )
                            )

                            val day = calculatedDays(model.present, model.total) { present, total ->
                                (((100 * present) - (minPercentage * total)) / minPercentage)
                            }.toInt()
                            status.text = when {
                                per == minPercentage || day <= 0 ->
                                    binding.root.context.resources.getString(R.string.on_track)
                                day != 0 -> binding.root.context.resources.getString(
                                    R.string.leave_class,
                                    day.toString()
                                )
                                else -> binding.root.context.resources.getString(R.string.error)
                            }
                        }
                        per < minPercentage -> {
                            ivStatus.setImageResource(R.drawable.recred)
                            progressCircular.setIndicatorColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    R.color.red
                                )
                            )
                            val day = calculatedDays(model.present, model.total) { present, total ->
                                (((minPercentage * total) - (100 * present)) / (100 - minPercentage))
                            }
                            status.text = binding.root.context.resources.getString(
                                R.string.attend_class,
                                (ceil(day).toInt()).toString()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder =
        AttendanceViewHolder(
            RowAttendanceSubBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilAttendance : DiffUtil.ItemCallback<AttendanceModel>() {
        override fun areItemsTheSame(oldItem: AttendanceModel, newItem: AttendanceModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AttendanceModel,
            newItem: AttendanceModel
        ): Boolean =
            oldItem == newItem
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProgress(progress: Int) {
        this.minPercentage = progress
        notifyDataSetChanged()
    }
}

