package com.atech.bit.ui.fragments.login.loading_data

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.core.data.network.user.AttendanceUploadModel
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.attendance.Days
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.utils.KEY_FIRST_TIME_LOGIN
import com.atech.core.utils.KEY_IS_USER_LOG_IN
import com.atech.core.utils.KEY_USER_DONE_SET_UP
import com.atech.core.utils.TAG
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class LoadingDataFragment : Fragment(R.layout.fragment_loading_data) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }


    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val args: LoadingDataFragmentArgs by navArgs()
    private val userDataViewModel: UserDataViewModel by activityViewModels()

    @Inject
    lateinit var attendanceDao: AttendanceDao

    @Inject
    lateinit var pref: SharedPreferences


    @Inject
    lateinit var syllabusDao: SyllabusDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() = lifecycleScope.launchWhenStarted {
        userDataViewModel.getCourseSem(args.uid, {
            val split = it.split(" ")
            if (split.size == 2) {
                setCourse(split[0], split[1])
            } else {
                Toast.makeText(requireContext(), "Something went wrong!!", Toast.LENGTH_SHORT)
                    .show()
                navigateToStartUp()
            }
        }) {
            Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
        }
        userDataViewModel.getCGPA(args.uid, {
            preferencesManagerViewModel.updateCgpa(it)
        }) {
            Log.e(TAG, "onViewCreated: $it")
        }
        userDataViewModel.getAttendance(
            args.uid,
            {
                addAttendanceToDatabase(it)
            },
            {
                Log.e(TAG, "onViewCreated: $it")
            })
        delay(500)
        navigateToHome()
        updateIsLogIn()
    }

    private fun updateIsLogIn() {
        pref.edit().putBoolean(KEY_IS_USER_LOG_IN, true).apply()
    }

    private fun addAttendanceToDatabase(attendanceUploadModel: List<AttendanceUploadModel>) {
        lifecycleScope.launchWhenStarted {
            val list = attendanceUploadModel.map {
                AttendanceModel(
                    subject = it.subject,
                    total = it.total,
                    present = it.present,
                    teacher = it.teacher,
                    fromSyllabus = it.fromSyllabus,
                    isArchive = it.isArchive,
                    created = it.created,
                    days = Days(
                        presetDays = arrayListOf(),
                        absentDays = arrayListOf(),
                        totalDays = arrayListOf()
                    ),
                )
            }
            list.forEach {
                if (it.fromSyllabus == true) {
                    syllabusDao.getSyllabus(it.subject)?.let { syllabus ->
                        syllabusDao.updateSyllabus(syllabus.copy(isAdded = true))
                    }
                }
            }
            attendanceDao.insertAll(list)
        }
    }

    private fun setCourse(course: String, sem: String) {
        preferencesManagerViewModel.updateCourse(course)
        preferencesManagerViewModel.updateSem(sem)
    }

    private fun navigateToHome() {
        pref.edit().putBoolean(
            KEY_USER_DONE_SET_UP,
            true
        ).apply()

        pref.edit().putBoolean(
            KEY_FIRST_TIME_LOGIN,
            true
        ).apply()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController()
            .navigate(
                LoadingDataFragmentDirections.actionLoadingDataFragmentToHomeFragment()
            )
    }

    private fun navigateToStartUp() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController()
            .navigate(
                NavGraphDirections.actionGlobalStartUpFragment()
            )
    }

}