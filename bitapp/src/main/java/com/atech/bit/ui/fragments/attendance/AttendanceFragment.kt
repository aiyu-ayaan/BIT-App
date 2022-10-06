package com.atech.bit.ui.fragments.attendance

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentAttendanceBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.utils.AttendanceEvent
import com.atech.bit.utils.getUid
import com.atech.bit.utils.loadAdds
import com.atech.core.data.network.user.AttendanceUploadModel
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.attendance.AttendanceSave
import com.atech.core.data.room.attendance.IsPresent
import com.atech.core.utils.KEY_ATTENDANCE_UPLOAD_FIRST_TIME
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import com.atech.core.utils.TAG
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.convertLongToTime
import com.atech.core.utils.countTotalClass
import com.atech.core.utils.findPercentage
import com.atech.core.utils.onScrollChange
import com.atech.core.utils.showUndoMessage
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Deque
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {


    private val binding: FragmentAttendanceBinding by viewBinding()
    private val viewModel: AttendanceViewModel by viewModels()
    private val preferenceViewModel: PreferenceManagerViewModel by activityViewModels()
    private val communicator: CommunicatorViewModel by activityViewModels()
    private val userDataViewModel by activityViewModels<UserDataViewModel>()
    private var defPercentage = 75
    private lateinit var attendanceAdapter: AttendanceAdapter
    private var attendanceList: List<AttendanceUploadModel> = listOf()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        attendanceAdapter = AttendanceAdapter(
            {
                onItemClickListener(it)
            },
            { onCheckClick(it) },
            { onWrongClick(it) },
            {
                navigateToMenu(it)
            }
        )

        //        Percentage
        setUpTopView()

        setUpViews()

        populateViewsAndSetPercentage()

        listenForUndoMessage()

        detectScroll()
        addSubject()

        requireContext().loadAdds(binding.attendanceView.adView)
        setUpBottomAppBar()
    }

    private fun setUpBottomAppBar() = binding.bottomAppBar.apply {
        setNavigationOnClickListener {
            navigateToListAll()
        }
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_book -> navigateToAddSubjectFromSyllabus().let {
                    true
                }

                R.id.menu_archive ->
                    navigateToArchive()
                        .let {
                            true
                        }

                R.id.menu_setting ->
                    navigateToSetting().let {
                        true
                    }

                else -> false
            }
        }
    }

    private fun uploadWhenNewLogin() {
        val isUploadFirstTime = pref.getBoolean(KEY_ATTENDANCE_UPLOAD_FIRST_TIME, true)
        if (auth.currentUser != null)
            if (isUploadFirstTime && attendanceList.isNotEmpty()) {
                uploadAttendanceData {
                    pref.edit().putBoolean(KEY_ATTENDANCE_UPLOAD_FIRST_TIME, false).apply()
                }
            }
    }

    private fun navigateToMenu(attendanceModel: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAttendanceMenu(attendanceModel)
        findNavController().navigate(action)
    }


    private fun listenForUndoMessage() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            communicator.attendanceEvent.collect { attendanceEvent ->
                when (attendanceEvent) {
                    is AttendanceEvent.ShowUndoDeleteMessage -> {
//                        Single attendance
                        attendanceEvent.attendance.showUndoMessage(
                            binding.root
                        ) {
                            viewModel.add(it, REQUEST_ADD_SUBJECT_FROM_SYLLABUS)
                        }

                    }
                }
            }
        }
    }

    private fun populateViewsAndSetPercentage() {
        viewModel.unArchive.observe(viewLifecycleOwner) { it ->
            attendanceAdapter.submitList(it)
//            binding.attendanceView.emptyAnimation.isVisible = it.isEmpty()
            var sumPresent = 0
            var sumTotal = 0
            it.forEach {
                sumPresent += it.present
                sumTotal += it.total
            }
            val finalPercentage =
                findPercentage(sumPresent.toFloat(), sumTotal.toFloat()) { present, total ->
                    when (total) {
                        0.0F -> 0.0F
                        else -> ((present / total) * 100)
                    }
                }
            binding.attendanceView.apply {
                tvPercentage.text = "${finalPercentage.toInt()}%"
                progressCircularOuter.progress = finalPercentage.toInt()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                tvOverAll.text = resources.getString(
                    R.string.overallAttendance,
                    df.format(finalPercentage)
                )
                tv4.text = resources.getString(
                    R.string.overallAttendance,
                    df.format(finalPercentage)
                )
            }
        }
        viewModel.allAttendance.observe(viewLifecycleOwner) { it ->
            convertingData(it)
        }
    }

    private fun convertingData(list: List<AttendanceModel>) =
        lifecycleScope.launchWhenStarted {
            if (list.isNotEmpty()) {
                attendanceList = list.map { a ->
                    AttendanceUploadModel(
                        a.subject,
                        a.total,
                        a.present,
                        a.teacher,
                        a.fromSyllabus,
                        a.isArchive,
                        a.created
                    )
                }
            }
            if (communicator.isDataSet) {
                communicator.attendanceManagerSize = attendanceList.size
                communicator.isDataSet = false
            }
            uploadWhenNewLogin()
        }

    private fun setUpViews() {
        binding.attendanceView.apply {
            showAtt.apply {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            attendanceAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        }
    }

    private fun navigateToListAll() {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToListAllBottomSheet()
        try {
            findNavController().navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpTopView() {
        preferenceViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            binding.attendanceView.tvGoal.text =
                resources.getString(R.string.goal, it.defPercentage.toString())
            binding.attendanceView.tv3.text =
                resources.getString(R.string.goal, it.defPercentage.toString())
            binding.attendanceView.progressCircularInner.progress = it.defPercentage
            attendanceAdapter.setProgress(it.defPercentage)
            defPercentage = it.defPercentage
        }
    }

    /**
     * @version 4.0.4
     * @author Ayaan
     */
    private fun addSubject() {
        binding.extendedFab.setOnClickListener {
            val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet()
            findNavController().navigate(action)
        }
    }


    private fun navigateToAddSubjectFromSyllabus() {
        val directions =
            NavGraphDirections.actionGlobalEditSubjectBottomSheet(REQUEST_ADD_SUBJECT_FROM_SYLLABUS)
        findNavController().navigate(directions)
    }


    private fun navigateToSetting() {
        try {
            val action =
                NavGraphDirections.actionGlobalChangePercentageDialog(defPercentage)
            findNavController().navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onItemClickListener(attendance: AttendanceModel) {
        try {
            val action =
                AttendanceFragmentDirections.actionAttendanceFragmentToCalenderViewBottomSheet(
                    attendance,
                    attendance.subject,
                    defPercentage
                )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Click one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onCheckClick(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val presentDays = attendance.days.presetDays.clone() as ArrayList<Long>
        val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
        stack.push(
            AttendanceSave(
                attendance.total,
                attendance.present,
                attendance.days.copy(
                    presetDays = attendance.days.presetDays,
                    totalDays = ArrayList(attendance.days.totalDays.map {
                        IsPresent(
                            it.day,
                            it.isPresent,
                            it.totalClasses
                        )
                    })
                )
            )
        )
        presentDays.add(System.currentTimeMillis())
        /**
         * @since 4.0.3
         * @author Ayaan
         */
        when {
            totalDays.isEmpty() ||
                    totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || !totalDays.last().isPresent ->//new Entry or new day or new session
                totalDays.add(IsPresent(System.currentTimeMillis(), true, totalClasses = 1))

            totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
                totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, true)

            else ->//same day
                totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
        }
        viewModel.update(
            AttendanceModel(
                id = attendance.id,
                subject = attendance.subject,
                present = attendance.present + 1,
                total = attendance.total + 1,
                days = attendance.days.copy(presetDays = presentDays, totalDays = totalDays),
                stack = stack,
                fromSyllabus = attendance.fromSyllabus,
                teacher = attendance.teacher,
            )
        ).also {
            communicator.hasChange = true
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onWrongClick(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val absentDays = attendance.days.absentDays.clone() as ArrayList<Long>
        val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
        stack.push(
            AttendanceSave(
                attendance.total,
                attendance.present,
                attendance.days.copy(
                    absentDays = attendance.days.absentDays,
                    totalDays = ArrayList(attendance.days.totalDays.map {
                        IsPresent(
                            it.day,
                            it.isPresent,
                            it.totalClasses
                        )
                    })
                ),
            )
        )
        absentDays.add(System.currentTimeMillis())
        /**
         * @since 4.0.3
         * @author Ayaan
         */
        when {
            totalDays.isEmpty() || totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || totalDays.last().isPresent ->//new Entry or new day
                totalDays.add(IsPresent(System.currentTimeMillis(), false, totalClasses = 1))

            totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
                totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, false)

            else ->//same day
                totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
        }
        viewModel.update(
            AttendanceModel(
                id = attendance.id,
                subject = attendance.subject,
                present = attendance.present,
                total = attendance.total + 1,
                days = attendance.days.copy(
                    absentDays = absentDays,
                    totalDays = totalDays
                ),
                stack = stack,
                fromSyllabus = attendance.fromSyllabus,
                teacher = attendance.teacher
            )
        ).also {
            communicator.hasChange = true
        }

    }

    private fun navigateToArchive() {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToArchiveBottomSheet(defPercentage)
        findNavController().navigate(action)
    }


    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        binding.attendanceView.showAtt.onScrollChange(
            {
                binding.extendedFab.apply {
                    show()
//                    extend()
                }
//                       Status bar
                activity?.changeStatusBarToolbarColor(
                    R.id.toolbar,
                    com.google.android.material.R.attr.colorSurface
                )

            },
            {
//                binding.extendedFab.shrink()
//                        Color change
                activity?.changeStatusBarToolbarColor(
                    R.id.toolbar,
                    R.attr.bottomBar
                )
            })
    }


    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null) {
            checkForHasChange()
            if (communicator.maxTimeToUploadAttendanceData <= 2
                && communicator.attendanceManagerSize != attendanceList.size
            )
                uploadAttendanceData {
                    Log.d(TAG, "onPause: Done")
                    communicator.maxTimeToUploadAttendanceData =
                        communicator.maxTimeToUploadAttendanceData.plus(1)
                    Log.d(TAG, "onPause: ${communicator.maxTimeToUploadAttendanceData}")
                }.also {
                    communicator.attendanceManagerSize = attendanceList.size
                }
        }
    }

    private fun checkForHasChange() {
        if (communicator.hasChange)
            uploadAttendanceData()
    }

    private fun uploadAttendanceData(action: () -> Unit = {}) {
        userDataViewModel.setAttendance(getUid(auth)!!, attendanceList, {
            action.invoke().also {
                communicator.hasChange = false
            }
        }) {
            Log.d(TAG, "onPause: Failed")
        }
    }
}