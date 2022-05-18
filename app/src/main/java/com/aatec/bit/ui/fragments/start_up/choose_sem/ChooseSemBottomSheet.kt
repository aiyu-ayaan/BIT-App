package com.aatec.bit.ui.fragments.start_up.choose_sem

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aatec.bit.R
import com.aatec.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.BottomSheetChooseSemBinding
import com.aatec.core.utils.KEY_FIRST_TIME_TOGGLE
import com.aatec.core.utils.REQUEST_UPDATE_SEM
import com.aatec.core.utils.REQUEST_UPDATE_SEM_TIME_TABLE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseSemBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetChooseSemBinding
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val args: ChooseSemBottomSheetArgs by navArgs()

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    //
    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog
    private lateinit var course: String
    private lateinit var sem: String


    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChooseSemBinding.inflate(inflater)

        setViews()
        if (args.request == REQUEST_UPDATE_SEM) {
            activity?.findViewById<Toolbar>(R.id.toolbar)?.let {
                it.isVisible = true
            }
            activity?.findViewById<LinearLayout>(R.id.bottomLayout)?.let {
                it.isVisible = true
            }
        }
        courseClick()
        semClick()
        binding.apply {
            btSave.setOnClickListener {
                if (args.request != REQUEST_UPDATE_SEM) {
                    pref.edit()
                        .putBoolean(KEY_FIRST_TIME_TOGGLE, true)
                        .apply()
                    exitTransition = MaterialFadeThrough()
                    if (args.type == REQUEST_UPDATE_SEM_TIME_TABLE)
                        dismiss()
                    else
                        navigationToHome()
                }
                saveSettings(getCourse(), getSem())
            }
            bottomSheetTitle.setOnClickListener {
                dismiss()
            }
        }
        courseClick()
        return binding.root
    }

    private fun navigationToHome() {
        val action = ChooseSemBottomSheetDirections.actionChooseSemBottomSheetToHomeFragment()
        findNavController().navigate(action)
    }

    private fun saveSettings(course: String, sem: String) {
//        Reset data
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            preferencesManagerViewModel.reset("${it.course}${it.sem}")
        }
        preferencesManagerViewModel.updateCourse(course)
        preferencesManagerViewModel.updateSem(sem)
        dialog?.dismiss()
    }

    private fun setViews() {
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            course = when (it.course) {
                "BBA" -> {
                    binding.chipGroupCourse.check(R.id.bt_bba)
                    resources.getString(R.string.bba)
                }
                "BCA" -> {
                    binding.chipGroupCourse.check(R.id.bt_bca)
                    resources.getString(R.string.bca)
                }
                else -> {
                    ""
                }
            }
            sem = when (it.sem) {
                "1" -> {
                    binding.chipGroupSem.check(R.id.bt1)
                    resources.getString(R.string.sem1)
                }
                "2" -> {
                    binding.chipGroupSem.check(R.id.bt2)
                    resources.getString(R.string.sem2)
                }
                "3" -> {
                    binding.chipGroupSem.check(R.id.bt3)
                    resources.getString(R.string.sem3)
                }
                "4" -> {
                    binding.chipGroupSem.check(R.id.bt4)
                    resources.getString(R.string.sem4)
                }
                "5" -> {
                    binding.chipGroupSem.check(R.id.bt5)
                    resources.getString(R.string.sem5)
                }
                "6" -> {
                    binding.chipGroupSem.check(R.id.bt6)
                    resources.getString(R.string.sem6)
                }
                else -> {
                    ""
                }
            }
        }
    }

    //
    private fun courseClick() {
        binding.btBba.setOnClickListener {
            course = resources.getString(R.string.bba)
        }
        binding.btBca.setOnClickListener {
            course = resources.getString(R.string.bca)
        }
    }

    private fun getCourse() =
        course

    private fun getSem() =
        sem


    private fun semClick() {
        binding.bt1.setOnClickListener {
            sem = resources.getString(R.string.sem1)
        }
        binding.bt2.setOnClickListener {
            sem = resources.getString(R.string.sem2)
        }
        binding.bt3.setOnClickListener {
            sem = resources.getString(R.string.sem3)
        }
        binding.bt4.setOnClickListener {
            sem = resources.getString(R.string.sem4)
        }
        binding.bt5.setOnClickListener {
            sem = resources.getString(R.string.sem5)
        }
        binding.bt6.setOnClickListener {
            sem = resources.getString(R.string.sem6)
        }

    }

}