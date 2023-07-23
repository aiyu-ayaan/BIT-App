package com.atech.bit.ui.fragments.notice.detail

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentNoticeEventDetailBinding
import com.atech.bit.utils.ImageGridAdapter
import com.atech.bit.utils.getImageLinkNotification
import com.atech.bit.utils.navigateToViewImage
import com.atech.core.firebase.firestore.Attach
import com.atech.core.firebase.firestore.NoticeModel
import com.atech.core.utils.DataState
import com.atech.core.utils.MAX_SPAWN
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.getDate
import com.atech.theme.loadImage
import com.atech.theme.openCustomChromeTab
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeDetailFragment : BaseFragment(R.layout.fragment_notice_event_detail,Axis.X) {
    private val binding: FragmentNoticeEventDetailBinding by viewBinding()
    private val viewModel: NoticeDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
        }
        observeData()
    }

    private fun observeData() {
        viewModel.getNotice().observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                DataState.Empty -> {
                    binding.isLoadComplete(false)
                }

                is DataState.Error -> {
                    binding.isLoadComplete(false)
                    dataState.exception.message?.let { toast(it) }
                }

                DataState.Loading -> {}
                is DataState.Success -> {
                    binding.isLoadComplete(true)
                    binding.setViews(dataState.data)
                }
            }
        }
        viewModel.getAttach().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.setAttach(it)
            }
        }
    }
    private fun FragmentNoticeEventDetailBinding.setAttach(
        list: List<Attach>
    ) {
        val imageGridAdapter = ImageGridAdapter {
            navigateToViewImage(
                it to ""
            )
        }
        attachmentRecyclerView.apply {
            binding.textImage.isVisible = true
            isVisible = true
            layoutManager = GridLayoutManager(
                requireContext(), MAX_SPAWN
            ).apply {
                spanSizeLookup = imageGridAdapter.variableSpanSizeLookup
            }
            adapter = imageGridAdapter
            imageGridAdapter.submitList(attachments = list)
        }
    }

    private fun FragmentNoticeEventDetailBinding.setViews(data: NoticeModel) = this.apply {
        subjectTextView.text = data.title
        senderTextView.text = data.sender
        bodyTextView.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = data.body
        }
        textViewDate.text = data.created?.getDate()
        data.link?.let { link ->
            linkIcon.isVisible = link.isNotEmpty()
            linkIcon.setOnClickListener {
                requireActivity().openCustomChromeTab(link)
            }
        }
        senderProfileImageView.loadImage(
            data.getImageLinkNotification()
        )
    }


    private fun FragmentNoticeEventDetailBinding.isLoadComplete(isDone: Boolean) = this.apply {
        progressBarLinear.isVisible = false
        imageViewNoData.isVisible = !isDone
        cardViewEvent.isVisible = isDone
        textViewNoNotice.text = getString(
            com.atech.theme.R.string.no_notice_found
        )
    }


    private fun FragmentNoticeEventDetailBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                com.atech.theme.R.string.notice,
                action = findNavController()::navigateUp,
            ),
        )
    }


}