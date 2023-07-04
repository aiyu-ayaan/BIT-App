package com.atech.bit.ui.fragments.events

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.utils.ImagePreviewAdapter
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.EventCases
import com.atech.core.firebase.firestore.EventModel
import com.atech.theme.R
import com.atech.theme.ToolbarData
import com.atech.theme.adapters.NoticeEventAdapter
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.databinding.RowNoticeEventBinding
import com.atech.theme.enterTransition
import com.atech.theme.getDate
import com.atech.theme.launchWhenStarted
import com.atech.theme.loadCircular
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class EventFragment : Fragment(R.layout.layout_recycler_view) {

    private val binding: LayoutRecyclerViewBinding by viewBinding()

    @Inject
    lateinit var cases: EventCases

    private lateinit var eventAdapter: NoticeEventAdapter<EventModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
        observeData()
    }

    private fun LayoutRecyclerViewBinding.setRecyclerView() = this.recyclerView.apply {
        adapter = NoticeEventAdapter<EventModel> {
            setView(it)
        }.also { eventAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun RowNoticeEventBinding.setView(events: EventModel) = this.apply {
        bodyPreviewTextView.text = events.content
        senderTextView.text = events.society
        subjectTextView.text = events.title
        textViewDate.text = events.created?.getDate()
        senderProfileImageView.loadCircular(events.logo_link)
        events.path?.let {
            setPreviewAdapter(it)
        }
    }

    private fun RowNoticeEventBinding.setPreviewAdapter(path: String) {
        val ipAdapter = ImagePreviewAdapter()
        attachmentRecyclerView.apply {
            attachmentRecyclerView.isVisible = true
            adapter = ipAdapter
            layoutManager = LinearLayoutManager(
                binding.root.context, LinearLayoutManager.HORIZONTAL, false
            )
        }
        observePreviewData(path, ipAdapter, attachmentRecyclerView)
    }

    private fun observePreviewData(
        path: String, ipAdapter: ImagePreviewAdapter, attachmentRecyclerView: RecyclerView
    ) = launchWhenStarted {
        try {
            cases.getAttach.invoke(Db.Event, path).collectLatest {
                ipAdapter.items = it
                attachmentRecyclerView.isVisible = it.isNotEmpty()
            }
        } catch (e: Exception) {
            toast(e.message ?: "Error")
        }
    }

    private fun observeData() {
        launchWhenStarted {
            try {
                cases.getEvent().collectLatest {
                    binding.empty.isVisible = it?.isEmpty() ?: true
                    it?.let {
                        eventAdapter.items = it.toCollection(ArrayList())
                    }
                }
            } catch (e: Exception) {
                toast(e.message ?: "Error")
            }
        }
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(ToolbarData(title = R.string.events, action = {
            findNavController().navigateUp()
        }))
    }
}