package com.aatec.bit.ui.fragments.attendance

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
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
import com.google.android.material.snackbar.Snackbar
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
    private var actionMode: ActionMode? = null
    private val attendanceList: Lazy<MutableList<AttendanceModel>> = lazy { arrayListOf() }
    private val views: Lazy<MutableList<CardView>> = lazy { arrayListOf() }
    private val attendanceLiveData: Lazy<MutableLiveData<List<AttendanceModel>>> =
        lazy { MutableLiveData() }
    private val deletedAttendance: Lazy<MutableList<AttendanceModel>> =
        lazy { arrayListOf() }


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
            { attendance, cardView ->
                addItemToList(attendance, cardView)
            },
            {
                setActionBar()
            }
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


    //    TODO Edit
    private fun setActionBar() {
        val callback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.menu_attendance, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =
                false

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean =
                when (item?.itemId) {
                    R.id.menu_undo -> {
                        undoEntry(attendanceList.value.last())
                        true
                    }
                    R.id.menu_edit -> {
                        navigateToAddEditFragment(attendanceList.value.last())
                        actionMode?.finish()
                        true
                    }
                    R.id.menu_delete -> {
                        deleteList()
                        true
                    }

                    R.id.menu_delete_all -> {
                        findNavController().navigate(
                            NavGraphDirections.actionGlobalDeleteAllDialog()
                        )
                        actionMode?.finish()
                        true
                    }
                    else -> {
                        false
                    }
                }

            override fun onDestroyActionMode(mode: ActionMode?) {
                attendanceAdapter.setIsMenuActive(false)
                resetListAndViews()
                actionMode = null
            }
        }
        actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)

        attendanceLiveData.value.observe(viewLifecycleOwner) {
            actionMode?.title = "${if (it.isEmpty()) "Deleted All" else it.size}"
            actionMode?.menu?.findItem(R.id.menu_delete)?.isVisible = it.isNotEmpty()
            actionMode?.menu?.findItem(R.id.menu_undo)?.isVisible = it.isNotEmpty() && it.size == 1
            actionMode?.menu?.findItem(R.id.menu_edit)?.isVisible = it.isNotEmpty() && it.size == 1
            actionMode?.menu?.findItem(R.id.menu_delete_all)?.isVisible = it.isEmpty()
        }
    }


    private fun deleteList() {
        attendanceList.value.forEach {
            viewModel.delete(it)
        }
        deletedAttendance.value.clear()
        deletedAttendance.value.addAll(attendanceList.value)
        lifecycleScope.launchWhenStarted {
            communicator._attendanceEvent.send(
                AttendanceEvent.ShowUndoDeleteMessageList()
            )
        }
        resetListAndViews()
        actionMode?.finish()
    }

    private fun resetListAndViews() {
        views.value.onEach {
            it.changeCardColor(requireContext(), android.viewbinding.library.R.attr.colorSurface)
            it.isLongClickable = true
        }
        views.value.clear()
        attendanceList.value.clear()
        attendanceLiveData.value.postValue(attendanceList.value)
    }

    private fun addItemToList(attendance: AttendanceModel, cardView: CardView) {
        views.value.add(cardView)
        if (attendanceList.value.contains(attendance)) {
            cardView.changeCardColor(
                requireContext(),
                com.google.android.material.R.attr.colorSurface
            )
            attendanceList.value.remove(attendance)
        } else {
            cardView.changeCardColor(requireContext(), R.attr.bottomBar)
            attendanceList.value.add(attendance)
        }

        attendanceLiveData.value.postValue(attendanceList.value)
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
                    is AttendanceEvent.ShowUndoDeleteMessageList -> {
                        deletedAttendance.value.showUndoMessage(binding.root) { list ->
                            list.onEach { deletedAttendance ->
                                viewModel.add(deletedAttendance, REQUEST_ADD_SUBJECT_FROM_SYLLABUS)
                            }
                            deletedAttendance.value.clear()
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


    private fun undoEntry(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val save = stack.peekFirst()
        if (save != null) {
            stack.pop()
            val att = attendance.copy(
                present = save.present,
                total = save.total,
                days = save.days,
                stack = stack,
            )
            viewModel.update(att)
            binding.root.showSnackBar(
                "Done !!",
                Snackbar.LENGTH_SHORT
            )
        } else {
            binding.root.showSnackBar(
                "Stack is empty !!",
                Snackbar.LENGTH_SHORT
            )
        }
        actionMode?.finish()
    }

    private fun navigateToAddEditFragment(attendance: AttendanceModel) {
        val action = NavGraphDirections.actionGlobalAddEditSubjectBottomSheet(
            attendance,
            UPDATE_REQUEST
        )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        actionMode?.finish()
        actionMode = null
    }
}