package com.aatec.bit.fragments.attendance.change_percentage

import android.app.Dialog
import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.DialogChangePercentageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePercentageDialog : DialogFragment() {
    private lateinit var binding: DialogChangePercentageBinding
    private val args: ChangePercentageDialogArgs by navArgs()
    private val viewModel: PreferenceManagerViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DialogChangePercentageBinding.inflate(layoutInflater)
        binding.apply {
            binding.seekBar.progress = args.percentage
            tvProgress.text = seekBar.progress.toString()
            progressBar.progress = seekBar.progress
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    tvProgress.text = progress.toString()
                    progressBar.progress = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.changePercentage))
            .setView(binding.root)
            .setPositiveButton(resources.getString(R.string.done)) { dialog, which ->
                viewModel.updatePercentage(binding.seekBar.progress)
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->

            }

        return dialog.create()
    }
}