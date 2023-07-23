package com.atech.bit.ui.fragments.notice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.utils.ImagePreviewAdapter
import com.atech.bit.utils.getImageLinkNotification
import com.atech.bit.utils.navigateToViewImage
import com.atech.core.firebase.firestore.NoticeModel
import com.atech.core.utils.TAGS
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.adapters.NoticeEventAdapter
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.databinding.RowNoticeEventBinding
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.getDate
import com.atech.theme.loadImage
import com.atech.theme.navigate
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : BaseFragment(com.atech.theme.R.layout.layout_recycler_view,Axis.Z) {

    private val viewModel: NoticeViewModel by viewModels()
    private val binding: LayoutRecyclerViewBinding by viewBinding()

    private lateinit var noticeAdapter: NoticeEventAdapter<NoticeModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
        observeData()
    }

    private fun observeData() {
        try {
            viewModel.allNotices.observe(viewLifecycleOwner) {
                binding.empty.isVisible = it.isNullOrEmpty()
                it?.let {
                    noticeAdapter.items = it.toCollection(ArrayList())
                }
            }
        } catch (e: Exception) {
            toast(e.message ?: "Error")
            Log.e(TAGS.BIT_ERROR.name, "observeData: $e")
        }
    }

    private fun LayoutRecyclerViewBinding.setRecyclerView() = this.recyclerView.apply {
        adapter = NoticeEventAdapter<NoticeModel> { this.setView(it) }.also { noticeAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun RowNoticeEventBinding.setView(notice: NoticeModel) = this.apply {
        val hor = requireContext().resources.getDimensionPixelSize(com.atech.theme.R.dimen.grid_1_5)
        val ver = requireContext().resources.getDimensionPixelSize(com.atech.theme.R.dimen.grid_0_5)
        binding.root.setPadding(
            hor, ver, hor, ver
        )
        bodyPreviewTextView.text = notice.body
        senderTextView.text = notice.sender
        subjectTextView.text = notice.title
        textViewDate.text = notice.created?.getDate()
        senderProfileImageView.loadImage(
            notice.getImageLinkNotification()
        )
        root.setOnClickListener {
            navigateToDetailScreen(notice.path!!)
        }
        notice.path?.let {
            setPreviewAdapter(it)
        }
    }

    private fun navigateToDetailScreen(path: String) {
        exitTransition(Axis.X)
        val action = NavGraphDirections.actionGlobalNoticeDetailFragment(path)
        navigate(action)
    }

    private fun RowNoticeEventBinding.setPreviewAdapter(path: String) {
        val ipAdapter = ImagePreviewAdapter {
            navigateToViewImage(
                it to ""
            )
        }
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
    ) {
        try {
            viewModel.getAttach(path).observe(viewLifecycleOwner) {
                ipAdapter.items = it
                attachmentRecyclerView.isVisible = it.isNotEmpty()
            }
        } catch (e: Exception) {
            toast(e.message ?: "Error")
            Log.e(TAGS.BIT_ERROR.name, "observeData: $e")
        }
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.notice,
                action = findNavController()::navigateUp
            )
        )
    }
}