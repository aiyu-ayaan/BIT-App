package com.atech.bit.ui.fragments.universal_dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.atech.bit.databinding.FragmentUniversalAlertBinding
import com.atech.core.utils.openCustomChromeTab
import com.atech.core.utils.openPlayStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class UniversalDialogFragment : DialogFragment() {

    private val viewModel: UniversalDialogViewModel by viewModels()
    private lateinit var binding: FragmentUniversalAlertBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val data = viewModel.data ?: return super.onCreateDialog(savedInstanceState)
        binding = FragmentUniversalAlertBinding.inflate(layoutInflater).also {
            it.bindData(data)
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun FragmentUniversalAlertBinding.bindData(data: UniversalDialogData) = this.apply {
        textViewTitle.text = data.title.trim()
        textViewMessage.text = data.message.trim()
        buttonActionCancel.apply {
            text = data.negativeButtonText
            setOnClickListener {
                dismiss()
            }
        }
        buttonActionOk.apply {
            text = data.positiveButtonText
            setOnClickListener {
                handleClick(data.link)
                dismiss()
            }
        }
    }


    private fun handleClick(link: String) {
        if (link.contains("https://play.google.com/store/apps/details")) {
            val packageName = link.substringAfter("id=")
            println(packageName)
            requireActivity().openPlayStore(packageName)
        } else {
            requireActivity().openCustomChromeTab(link)
        }
    }

    @Keep
    @Parcelize
    data class UniversalDialogData(
        val title: String = "Update Available",
        val message: String = "Please update the app to continue",
        val positiveButtonText: String = "Update",
        val negativeButtonText: String = "Cancel",
        val link: String = "https://play.google.com/store/apps/details?id=com.atech.bit"
    ) : Parcelable
}