package com.aatec.bit.ui.fragments.notice.choose_image

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.R
import com.aatec.bit.databinding.BottomSheetChooseImageBinding
import com.aatec.bit.ui.Fragments.Notice.NoticeMain.ImageGridAdapter
import com.aatec.core.utils.openShareImageDeepLink
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
            btDismiss.setOnClickListener {
                dismiss()
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
            arg.notice.notice.title,
            arg.notice.notice.path,
            link,
            "notice"
        )
    }
}