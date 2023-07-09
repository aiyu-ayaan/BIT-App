package com.atech.bit.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.attendance.utils.findPercentage
import com.atech.bit.databinding.RowAttendanceHomeBinding
import com.atech.bit.databinding.RowCarouselBinding
import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.core.room.attendance.AttendanceModel
import com.atech.theme.loadImage
import java.math.RoundingMode
import java.text.DecimalFormat


class EventAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    var items: List<HomeViewModelExr.EventHomeModel>? = null
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class EventViewHolder(
        private val binding: RowCarouselBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            items?.let { nonNullItems ->
                binding.root.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION)
                        onClick(nonNullItems[absoluteAdapterPosition].path)
                }
                binding.root.setOnMaskChangedListener { maskRect ->
                    binding.carouselTextView.translationX = maskRect.left;
                    binding.carouselTextView.alpha = 1 - maskRect.left / 100f
                }
            }
        }

        fun bind(model: HomeViewModelExr.EventHomeModel) {
            binding.apply {
                carouselTextView.text = model.title
                carouselImageView.loadImage(model.posterLink?.ifBlank { model.iconLink })
                carouselImageView.loadImage(model.posterLink?.ifBlank { model.iconLink }) {
                    it?.let { bitmap ->
                        bitmap.isDark { isDark ->
                            if (isDark)
                                carouselTextView.setTextColor(0xFFFFFFFF.toInt())
                            else
                                carouselTextView.setTextColor(0xFF000000.toInt())
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(
            RowCarouselBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        items?.let { holder.bind(it[position]) } ?: Unit
}


class AttendanceHomeAdapter(
    private val defPercentage: Int
) :
    ListAdapter<AttendanceModel, AttendanceHomeAdapter.AttendanceHomeViewHolder>(DiffUtilAttendance()) {

    inner class AttendanceHomeViewHolder(
        private val binding: RowAttendanceHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnMaskChangedListener { maskRect ->
                binding.apply {
                    val translateX = maskRect.left
                    val alpha = 1 - maskRect.left / 100f
                    textViewSubjectName.translationX = translateX
                    textViewSubjectName.alpha = alpha
                    textViewClasses.translationX = translateX
                    textViewClasses.alpha = alpha
                    textViewAttendancePercentage.translationX = translateX
                    textViewAttendancePercentage.alpha = alpha
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(attendanceModel: AttendanceModel) {
            binding.apply {
                textViewSubjectName.text = attendanceModel.subject
                textViewClasses.text = binding.root.context.getString(
                    com.atech.theme.R.string.present_by_total,
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

class DiffUtilAttendance : DiffUtil.ItemCallback<AttendanceModel>() {
    override fun areItemsTheSame(oldItem: AttendanceModel, newItem: AttendanceModel): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: AttendanceModel,
        newItem: AttendanceModel
    ): Boolean =
        oldItem == newItem
}
