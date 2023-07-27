package com.atech.bit.ui.fragments.notice.choose_image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.utils.ImageGridAdapter
import com.atech.theme.BottomSheetItem
import com.atech.theme.R
import com.atech.theme.ShareType
import com.atech.theme.base_class.BaseBottomSheet
import com.atech.theme.databinding.LayoutBottomSheetBinding
import com.atech.theme.openShareDeepLink
import com.atech.theme.openShareImageDeepLink
import com.atech.theme.setTopView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseImageBottomSheet : BaseBottomSheet() {
    private lateinit var binding: LayoutBottomSheetBinding


    private val arg: ChooseImageBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetBinding.inflate(inflater)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
        return binding.root
    }
    private fun LayoutBottomSheetBinding.setRecyclerView() = this.listAll.apply {
        val imageGridAdapter = ImageGridAdapter(::shareImage)
        adapter = imageGridAdapter
        layoutManager = LinearLayoutManager(requireContext())
        imageGridAdapter.submitList(arg.data.attach)
    }

    private fun LayoutBottomSheetBinding.setToolbar() = this.apply {
        setTopView(BottomSheetItem(
            getString(R.string.choose_image),
            R.drawable.outline_hide_image_24,
            onIconClick = ::shareWithoutImage,
        ) {
            setOnClickListener {
                dismiss()
            }
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_arrow_downward
                ), null, null, null
            )
        })
    }

    private fun shareWithoutImage() {
        val data = arg.data
        requireActivity().openShareDeepLink(
            data.notice?.title ?: data.event?.title ?: "",
            data.notice?.path ?: data.event?.path ?: "",
            if (data.notice != null) ShareType.NOTICE else ShareType.EVENT
        )
    }

    private fun shareImage(link: String) {
        val data = arg.data
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.loading),
            Toast.LENGTH_SHORT
        ).show()
        requireActivity().openShareImageDeepLink(
            requireContext(),
            data.notice?.title ?: data.event?.title ?: "",
            data.notice?.path ?: data.event?.path ?: "",
            link,
            if (data.notice != null) ShareType.NOTICE else ShareType.EVENT
        )
    }
}