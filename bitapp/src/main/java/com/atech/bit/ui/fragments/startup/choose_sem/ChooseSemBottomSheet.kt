package com.atech.bit.ui.fragments.startup.choose_sem

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.BottomSheetChooseSemBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.utils.getUid
import com.atech.core.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseSemBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetChooseSemBinding
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val args: ChooseSemBottomSheetArgs by navArgs()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    @Inject
    lateinit var auth: FirebaseAuth

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
        if (args.request == REQUEST_UPDATE_SEM_FROM_CGPA) {
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.let {
                it.isVisible = false
            }
        }
        courseClick()
        semClick()
        binding.apply {
            btSave.setOnClickListener {
                if (args.request != REQUEST_UPDATE_SEM && args.request != REQUEST_UPDATE_SEM_FROM_CGPA) {
                    pref.edit().putBoolean(
                        KEY_USER_DONE_SET_UP,
                        true
                    ).apply()

                    exitTransition = MaterialFadeThrough()
                    if (args.type == REQUEST_UPDATE_SEM_FROM_CGPA)
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
        setButtonVisibility()
        return binding.root
    }

    private fun setButtonVisibility() {
        val source = pref.getString(
            KEY_SYLLABUS_VISIBILITY_PREF_CONFIG,
            resources.getString(R.string.def_value_syllabus_visibility)
        )
        Gson().fromJson(source, SyllabusVisibility::class.java).also {
            binding.apply {
                btBba.isVisible = it.bba
                btBca.isVisible = it.bca
                btMba.isVisible = it.mba
                btMca.isVisible = it.mca
            }
        }
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
        if (auth.currentUser != null) {
            userDataViewModel.addCourseSem(
                getUid(auth)!!,
                course, sem,
                {
                    pref.edit()
                        .putBoolean(KEY_USER_HAS_DATA_IN_DB, true)
                        .apply()
                    updateIsLogIn()
                }
            ) {
                Toast.makeText(requireContext(), "Data upload failed", Toast.LENGTH_SHORT).show()
            }
        }
        pref.edit().putBoolean(
            KEY_FIRST_TIME_LOGIN,
            true
        ).apply()
        dialog?.dismiss()
    }

    private fun updateIsLogIn() {
        pref.edit().putBoolean(KEY_IS_USER_LOG_IN, true).apply()
    }

    private fun setViews() {
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            course = when (it.course) {
                "BBA" -> {
                    binding.bt5.isVisible = true
                    binding.bt6.isVisible = true
                    binding.chipGroupCourse.check(R.id.bt_bba)
                    resources.getString(R.string.bba)
                }

                "BCA" -> {
                    binding.bt5.isVisible = true
                    binding.bt6.isVisible = true
                    binding.chipGroupCourse.check(R.id.bt_bca)
                    resources.getString(R.string.bca)
                }

                "MCA" -> {
                    binding.bt5.isVisible = false
                    binding.bt6.isVisible = false
                    binding.chipGroupCourse.check(R.id.bt_mca)
                    resources.getString(R.string.mca)
                }

                "MBA" -> {
                    binding.bt5.isVisible = false
                    binding.bt6.isVisible = false
                    binding.chipGroupCourse.check(R.id.bt_mba)
                    resources.getString(R.string.mba)
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
            binding.bt5.isVisible = true
            binding.bt6.isVisible = true
        }
        binding.btBca.setOnClickListener {
            course = resources.getString(R.string.bca)
            binding.bt5.isVisible = true
            binding.bt6.isVisible = true
        }
        binding.btMba.setOnClickListener {
            course = resources.getString(R.string.mba)
            binding.bt5.isVisible = false
            binding.bt6.isVisible = false
            binding.chipGroupSem.check(R.id.bt1)
            sem = resources.getString(R.string.sem1)
        }
        binding.btMca.setOnClickListener {
            course = resources.getString(R.string.mca)
            binding.bt5.isVisible = false
            binding.bt6.isVisible = false
            binding.chipGroupSem.check(R.id.bt1)
            sem = resources.getString(R.string.sem1)
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

    override fun onDestroy() {
        super.onDestroy()
        if (args.request == REQUEST_UPDATE_SEM_FROM_CGPA) {
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.let {
                it.isVisible = true
            }
        }
    }

    @Keep
    data class SyllabusVisibility(
        val bca: Boolean,
        val bba: Boolean,
        val mca: Boolean,
        val mba: Boolean
    )

}