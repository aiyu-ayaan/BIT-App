package com.atech.bit.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.attendance.utils.findPercentage
import com.atech.bit.databinding.RowAttendanceHomeBinding
import com.atech.bit.databinding.RowCarouselBinding
import com.atech.bit.databinding.RowLibrarySubjectHomeBinding
import com.atech.bit.ui.fragments.home.viewmodel.HomeViewModelExr
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.library.DiffUtilCallbackLibrary
import com.atech.core.room.library.LibraryModel
import com.atech.core.utils.TAGS
import com.atech.theme.loadImage
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
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
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        try {
                            onClick(nonNullItems[absoluteAdapterPosition].path)
                        } catch (e: Exception) {
                            Log.e(TAGS.BIT_ERROR.name, "EventViewHolder: $e")
                        }
                    }
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
                            if (isDark) carouselTextView.setTextColor(0xFFFFFFFF.toInt())
                            else carouselTextView.setTextColor(0xFF000000.toInt())
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

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = try {
        items?.let { holder.bind(it[position]) } ?: Unit
    } catch (e: Exception) {
        Log.e(TAGS.BIT_ERROR.name, "onBindViewHolder: $e")
        Unit
    }
}


class AttendanceHomeAdapter(
    private val defPercentage: Int
) : ListAdapter<AttendanceModel, AttendanceHomeAdapter.AttendanceHomeViewHolder>(DiffUtilAttendance()) {

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
                val percentage = findPercentage(
                    attendanceModel.present.toFloat(), attendanceModel.total.toFloat()
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
                LayoutInflater.from(parent.context), parent, false
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
        oldItem: AttendanceModel, newItem: AttendanceModel
    ): Boolean = oldItem == newItem
}


class HomeLibraryAdapter(
    private val onDeleteClick: (LibraryModel) -> Unit = {},
    private val onMarkAsReturnClick: (LibraryModel) -> Unit = { },
) : ListAdapter<LibraryModel, HomeLibraryAdapter.LibraryViewHolder>(
    DiffUtilCallbackLibrary()
) {
    inner class LibraryViewHolder(
        private val binding: RowLibrarySubjectHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonMarkAsReturned.setOnClickListener {
                absoluteAdapterPosition.let { position ->
                    if (position != RecyclerView.NO_POSITION) {
                        val library = getItem(position)
                        collapseView()
                        onMarkAsReturnClick(library)
                    }
                }
            }
            binding.buttonDelete.setOnClickListener {
                absoluteAdapterPosition.let { position ->
                    if (position != RecyclerView.NO_POSITION) {
                        val library = getItem(position)
                        collapseView()
                        onDeleteClick(library)
                    }
                }
            }

            binding.floatingActionButton.setOnClickListener {
                absoluteAdapterPosition.let {
                    val transform = MaterialContainerTransform().apply {
                        startView = binding.floatingActionButton
                        endView = binding.constraintLayoutMenu
                        addTarget(endView)
                        pathMotion = MaterialArcMotion()
                        scrimColor = Color.TRANSPARENT
                        duration =
                            binding.root.resources.getInteger(com.atech.theme.R.integer.duration_medium)
                                .toLong()
                        endContainerColor = MaterialColors.getColor(
                            binding.root.context,
                            com.google.android.material.R.attr.colorPrimaryContainer,
                            Color.TRANSPARENT
                        )
                        endElevation = 10f
                    }
                    TransitionManager.beginDelayedTransition(binding.root, transform)
                    binding.floatingActionButton.visibility = View.INVISIBLE
                    binding.view.visibility = View.VISIBLE
                    binding.constraintLayoutMenu.visibility = View.VISIBLE
                }
            }
            binding.view.setOnClickListener {
                absoluteAdapterPosition.let {
                    if (it != RecyclerView.NO_POSITION) {
                        collapseView()
                    }
                }
            }
        }

        private fun collapseView() {
            val transform = MaterialContainerTransform().apply {
                startView = binding.constraintLayoutMenu
                endView = binding.floatingActionButton
                duration =
                    binding.root.resources.getInteger(com.atech.theme.R.integer.duration_medium)
                        .toLong()
                addTarget(endView)
                pathMotion = MaterialArcMotion()
                scrimColor = Color.TRANSPARENT
                startElevation = 10f
            }
            TransitionManager.beginDelayedTransition(binding.root, transform)
            binding.floatingActionButton.visibility = View.VISIBLE
            binding.view.visibility = View.GONE
            binding.constraintLayoutMenu.visibility = View.GONE
        }

        fun bind(libraryModel: LibraryModel) {
            binding.apply {
                textViewBookId.text = libraryModel.bookId.ifBlank { "No Id" }
                textViewIssueBookName.text = libraryModel.bookName
                textViewIssueBookName.setCompoundDrawablesWithIntrinsicBounds(
                    when {
                        libraryModel.eventId != -1L -> com.atech.theme.R.drawable.ic_bell_active
                        libraryModel.markAsReturn -> com.atech.theme.R.drawable.ic_check
                        else -> 0
                    }, 0, 0, 0
                )
//                buttonMarkAsReturned set icon image button
                buttonMarkAsReturned.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        if (libraryModel.markAsReturn) com.atech.theme.R.drawable.ic_close else com.atech.theme.R.drawable.ic_check
                    )
                )

                textViewIssueDate.text =
                    String.format("Issued on : %s", libraryModel.issueFormatData)
                floatingActionButton.transitionName = libraryModel.bookName
                textViewReturnDate.text = libraryModel.returnFormatData
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder =
        LibraryViewHolder(
            RowLibrarySubjectHomeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}