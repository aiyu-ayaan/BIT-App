package com.atech.bit.ui.fragments.event

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.R
import com.atech.bit.databinding.RowNotice3Binding
import com.atech.bit.ui.fragments.notice.ImagePreviewAdapter
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.ui.events.DiffUtilEvent
import com.atech.core.data.ui.events.Events
import com.atech.core.utils.REQUEST_ADAPTER_EVENT_FROM_HOME
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.loadImageCircular
import com.google.android.material.color.MaterialColors
import com.google.firebase.firestore.FirebaseFirestore


class EventsAdapter(
    private val db: FirebaseFirestore,
    private val imageClick: (String) -> Unit,
    private val request: Int = 0,
    private val listener: (Events, View) -> Unit,
) : ListAdapter<Events, EventsAdapter.EventsViewHolder>(DiffUtilEvent()) {

    inner class EventsViewHolder(
        private val binding: RowNotice3Binding
    ) : RecyclerView.ViewHolder(binding.root) {

        var attach: MutableList<Attach>? = null


        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                    listener(getItem(position), binding.root)
            }
        }

        fun bind(events: Events) = binding.apply {
            val imageAdapter = ImagePreviewAdapter { link ->
                imageClick.invoke(link)
            }

            binding.root.transitionName = events.path
            binding.textViewDate.text = binding.root.context.resources.getString(
                R.string.notice_date,
                events.created.convertLongToTime("dd/MM/yyyy")
            )
            if (request == REQUEST_ADAPTER_EVENT_FROM_HOME)
                binding.materialCardViewNotice.strokeColor = MaterialColors.getColor(
                    binding.root.context,
                    android.viewbinding.library.R.attr.colorSurface,
                    Color.WHITE
                )

            binding.bodyPreviewTextView.text = events.content
            binding.senderTextView.text = events.society
            binding.subjectTextView.text = events.title
            events.logo_link.loadImageCircular(
                binding.root,
                binding.senderProfileImageView,
                binding.progressBarNoticePreview,
                R.drawable.ic_running_error
            )
            attachmentRecyclerView.apply {
                adapter = imageAdapter
                layoutManager = LinearLayoutManager(
                    binding.root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

            db.collection("BIT_Events").document(events.path).collection("attach")
                .addSnapshotListener { value, _ ->
                    try {
                        attach = value?.toObjects(Attach::class.java)
                        attach?.let {
                            attachmentRecyclerView.isVisible = it.isNotEmpty()
                            imageAdapter.submitList(it)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            binding.root.context,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder =
        EventsViewHolder(
            RowNotice3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}