package com.atech.theme

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder


data class DialogModel(
        val title: String,
        val message: String,
        val positiveText: String,
        val negativeText: String,
        val positiveAction: (DialogInterface.() -> Unit)? = null,
        val negativeAction: (() -> Unit)? = null,
)

fun Fragment.showDialog(model: DialogModel): AlertDialog =
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(model.title)
                .setMessage(model.message)
                .setPositiveButton(model.positiveText) { dialog, _ ->
                    model.positiveAction?.invoke(dialog)
                }
                .setNegativeButton(model.negativeText) { dialog, _ ->
                    model.negativeAction?.invoke()
                    dialog.dismiss()
                }.show()
