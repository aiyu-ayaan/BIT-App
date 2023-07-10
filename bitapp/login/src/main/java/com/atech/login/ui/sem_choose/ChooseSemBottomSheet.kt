package com.atech.login.ui.sem_choose

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.view.isVisible
import com.atech.core.datastore.DataStoreCases
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.firebase.auth.UpdateDataType
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.fromJSON
import com.atech.login.R
import com.atech.login.databinding.BottomSheetChooseSemBinding
import com.atech.theme.BaseBottomSheet
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenCreated
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ChooseSemBottomSheet : BaseBottomSheet() {
    private lateinit var binding: BottomSheetChooseSemBinding
    private lateinit var course: String
    private lateinit var sem: String

    @Inject
    lateinit var cases: DataStoreCases

    @Inject
    lateinit var authCase: AuthUseCases

    @Inject
    lateinit var pref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChooseSemBinding.inflate(inflater)
        setViews()
        setButtonVisibility()
        binding.apply {
            semClick()
            courseClick()
            destroy()
            save()
        }
        return binding.root
    }

    private fun BottomSheetChooseSemBinding.save() = this.btSave.setOnClickListener {
        saveSettings(getCourse(), getSem())
    }

    private fun saveSettings(course: String, sem: String) = launchWhenCreated {
//        Reset data
        cases.getAll.invoke().observe(viewLifecycleOwner) {
            runBlocking {
                cases.reset.invoke("${it.course}${it.sem}")
            }
        }
        cases.updateCourse.invoke(course)
        cases.updateSem.invoke(sem)
        pref.edit().apply {
            putBoolean(SharePrefKeys.SetUpDone.name, true)
        }.apply()
        authCase.uploadData.invoke(
            UpdateDataType.CourseSem(course, sem)
        ) { error ->
            if (error != null) {
                toast(error.message.toString())
                Log.d(TAGS.BIT_ERROR.name, "saveSettings: $error")
            }

        }
        navigateToHome()
        dialog?.dismiss()
    }

    private fun navigateToHome() {
        enterTransition()
        navigateWithInAppDeepLink(BASE_IN_APP_NAVIGATION_LINK + Destination.Home.value)
    }


    private fun setButtonVisibility() {
        pref.getString(
            SharePrefKeys.SyllabusVisibility.name,
            resources.getString(com.atech.theme.R.string.def_value_syllabus_visibility)
        )?.let {
            fromJSON(
                it, SyllabusVisibility::class.java
            ).also {
                binding.apply {
                    btBba.isVisible = it!!.bba
                    btBca.isVisible = it.bca
                    btMba.isVisible = it.mba
                    btMca.isVisible = it.mca
                }
            }
        }
    }

    private fun BottomSheetChooseSemBinding.destroy() {
        bottomSheetTitle.setOnClickListener {
            dismiss()
        }
    }

    private fun setViews() {
        cases.getAll.invoke().observe(viewLifecycleOwner) {
            course = when (it.course) {
                "BBA" -> {
                    binding.bt5.isVisible = true
                    binding.bt6.isVisible = true
                    binding.chipGroupCourse.check(R.id.bt_bba)
                    resources.getString(com.atech.theme.R.string.bba)
                }

                "BCA" -> {
                    binding.bt5.isVisible = true
                    binding.bt6.isVisible = true
                    binding.chipGroupCourse.check(R.id.bt_bca)
                    resources.getString(com.atech.theme.R.string.bca)
                }

                "MCA" -> {
                    binding.bt5.isVisible = false
                    binding.bt6.isVisible = false
                    binding.chipGroupCourse.check(R.id.bt_mca)
                    resources.getString(com.atech.theme.R.string.mca)
                }

                "MBA" -> {
                    binding.bt5.isVisible = false
                    binding.bt6.isVisible = false
                    binding.chipGroupCourse.check(R.id.bt_mba)
                    resources.getString(com.atech.theme.R.string.mba)
                }

                else -> {
                    ""
                }
            }
            sem = when (it.sem) {
                "1" -> {
                    binding.chipGroupSem.check(R.id.bt1)
                    resources.getString(com.atech.theme.R.string.sem1)
                }

                "2" -> {
                    binding.chipGroupSem.check(R.id.bt2)
                    resources.getString(com.atech.theme.R.string.sem2)
                }

                "3" -> {
                    binding.chipGroupSem.check(R.id.bt3)
                    resources.getString(com.atech.theme.R.string.sem3)
                }

                "4" -> {
                    binding.chipGroupSem.check(R.id.bt4)
                    resources.getString(com.atech.theme.R.string.sem4)
                }

                "5" -> {
                    binding.chipGroupSem.check(R.id.bt5)
                    resources.getString(com.atech.theme.R.string.sem5)
                }

                "6" -> {
                    binding.chipGroupSem.check(R.id.bt6)
                    resources.getString(com.atech.theme.R.string.sem6)
                }

                else -> {
                    ""
                }
            }
        }
    }

    //
    private fun BottomSheetChooseSemBinding.courseClick() = this.apply {
        btBba.setOnClickListener {
            course = resources.getString(com.atech.theme.R.string.bba)
            bt5.isVisible = true
            bt6.isVisible = true
        }
        btBca.setOnClickListener {
            course = resources.getString(com.atech.theme.R.string.bca)
            bt5.isVisible = true
            bt6.isVisible = true
        }
        btMba.setOnClickListener {
            course = resources.getString(com.atech.theme.R.string.mba)
            bt5.isVisible = false
            bt6.isVisible = false
            binding.chipGroupSem.check(R.id.bt1)
            sem = resources.getString(com.atech.theme.R.string.sem1)
        }
        btMca.setOnClickListener {
            course = resources.getString(com.atech.theme.R.string.mca)
            bt5.isVisible = false
            bt6.isVisible = false
            binding.chipGroupSem.check(R.id.bt1)
            sem = resources.getString(com.atech.theme.R.string.sem1)
        }
    }

    private fun getCourse() = course

    private fun getSem() = sem


    private fun BottomSheetChooseSemBinding.semClick() = this.apply {
        bt1.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem1)
        }
        bt2.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem2)
        }
        bt3.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem3)
        }
        bt4.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem4)
        }
        bt5.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem5)
        }
        bt6.setOnClickListener {
            sem = resources.getString(com.atech.theme.R.string.sem6)
        }

    }

    @Keep
    data class SyllabusVisibility(
        val bca: Boolean, val bba: Boolean, val mca: Boolean, val mba: Boolean
    )
}