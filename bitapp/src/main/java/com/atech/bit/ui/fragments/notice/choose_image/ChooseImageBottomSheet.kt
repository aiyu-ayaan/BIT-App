package com.atech.bit.ui.fragments.notice.choose_image

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetChooseImageBinding
import com.atech.bit.ui.fragments.notice.ImageGridAdapter
import com.atech.bit.utils.openShareDeepLink
import com.atech.bit.utils.openShareImageDeepLink
import com.atech.core.utils.SHARE_EVENT
import com.atech.core.utils.SHARE_NOTICE
import com.atech.core.utils.SHARE_TYPE_EVENT
import com.atech.core.utils.SHARE_TYPE_NOTICE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseImageBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetChooseImageBinding
    private val arg: ChooseImageBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)


    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChooseImageBinding.inflate(inflater)
        val imageGridAdapter = ImageGridAdapter { attach ->
            shareImage(attach)
        }
        binding.apply {
            recyclerViewSelect.apply {
                adapter = imageGridAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
            textViewNoImage.setOnClickListener {
                requireActivity().openShareDeepLink(
                    arg.notice.notice?.title ?: arg.notice.event?.title ?: "",
                    arg.notice.notice?.path ?: arg.notice.event?.path ?: "",
                    if (arg.notice.notice != null) SHARE_TYPE_NOTICE else SHARE_TYPE_EVENT
                )
            }
        }
        imageGridAdapter.submitList(arg.notice.attach)
        return binding.root
    }

    private fun shareImage(link: String) {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.loading),
            Toast.LENGTH_SHORT
        ).show()
        requireActivity().openShareImageDeepLink(
            requireContext(),
            arg.notice.notice?.title ?: arg.notice.event?.title ?: "",
            arg.notice.notice?.path ?: arg.notice.event?.path ?: "",
            link,
            if (arg.notice.notice != null) SHARE_NOTICE else SHARE_EVENT
        )
    }
}