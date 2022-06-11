package com.aatec.bit.ui.fragments.attendance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentAttendanceBinding
import com.aatec.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.aatec.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.utils.AttendanceEvent
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.attendance.AttendanceSave
import com.aatec.core.data.room.attendance.IsPresent
import com.aatec.core.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {


    private val binding: FragmentAttendanceBinding by viewBinding()
    private val viewModel: AttendanceViewModel by viewModels()
    private val preferenceViewModel: PreferenceManagerViewModel by activityViewModels()
    private val communicator: CommunicatorViewModel by activityViewModels()
    private var defPercentage = 75
    private lateinit var attendanceAdapter: AttendanceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        attendanceAdapter = AttendanceAdapter(
            { onItemClickListener(it) },
            { onCheckClick(it) },
            { onWrongClick(it) },
            { onDotsClick(it) }
        )

        //        Percentage
        setUpTopView()

        setUpViews()

        populateViewsAndSetPercentage()

        listenForUndoMessage()

        setTopView()
        addSubjectFromSyllabus()
        detectScroll()
        addSubject()
        setHasOptionsMenu(true)
    }

    private fun listenForUndoMessage() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            communicator.attendanceEvent.collect { attendanceEvent ->
                when (attendanceEvent) {
                    is AttendanceEvent.ShowUndoDeleteMessage -> {
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
        viewModel.attendance.observe(viewLifecycleOwner) { it ->
            attendanceAdapter.submitList(it)
            binding.emptyAnimation.isVisible = it.isEmpty()
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
            binding.attendanceTopBar.apply {
                tvPercentage.text = "${finalPercentage.toInt()}%"
                progressBarOuter.progress = finalPercentage.toInt()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                tvOverAll.text = resources.getString(
                    R.string.overallAttendance,
                    "${df.format(finalPercentage)}%"
                )
            }
        }
    }

    private fun setUpViews() {
        binding.apply {
            showAtt.apply {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            attendanceAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

            //            ..List All
            attendanceTopBar.tvAddSub.setOnClickListener {
                val action =
                    AttendanceFragmentDirections.actionAttendanceFragmentToListAllBottomSheet()
                try {
                    findNavController().navigate(action)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun setUpTopView() {
        preferenceViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            binding.attendanceTopBar.tvGoal.text =
                resources.getString(R.string.goal, it.defPercentage.toString())
            binding.attendanceTopBar.progressCircularInner.progress = it.defPercentage
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


    private fun addSubjectFromSyllabus() {
        binding.attendanceTopBar.apply {
            tvAddSubSyllabus.setOnClickListener {
                navigateToEdit()
            }
        }
    }

    private fun navigateToEdit() {
        val directions =
            NavGraphDirections.actionGlobalEditSubjectBottomSheet(REQUEST_ADD_SUBJECT_FROM_SYLLABUS)
        findNavController().navigate(directions)
    }

    private fun setTopView() {
        binding.attendanceTopBar.apply {
            tvSetting.setOnClickListener {
                try {
                    val action =
                        NavGraphDirections.actionGlobalChangePercentageDialog(defPercentage)
                    findNavController().navigate(action)
                } catch (e: Exception) {

                }
            }
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
        )
    }



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
        )
    }

    private fun onDotsClick(attendance: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAttendanceMenu(attendance)
        findNavController().navigate(action)
    }


    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        binding.showAtt.onScrollChange(
            {
                binding.extendedFab.apply {
                    show()
                    extend()
                }
//                       Status bar
                activity?.changeStatusBarToolbarColor(
                    R.id.toolbar,
                    com.google.android.material.R.attr.colorSurface
                )

            },
            {
                binding.extendedFab.shrink()
//                        Color change
                activity?.changeStatusBarToolbarColor(
                    R.id.toolbar,
                    R.attr.bottomBar
                )
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}