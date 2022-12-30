package com.atech.bit.ui.fragments.universal_dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.atech.core.utils.openCustomChromeTab
import com.atech.core.utils.openPlayStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class UniversalDialogFragment : DialogFragment() {

    private val viewModel: UniversalDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val data = viewModel.data ?: return super.onCreateDialog(savedInstanceState)
        val dialog = MaterialAlertDialogBuilder(requireContext()).setTitle(data.title)
            .setMessage(data.message).setNegativeButton(data.negativeButtonText) { _, _ ->
                dismiss()
            }.setPositiveButton(data.positiveButtonText) { _, _ ->
                handleClick(data.link)
                dismiss()
            }.create()
        return dialog
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