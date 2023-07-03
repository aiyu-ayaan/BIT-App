package com.atech.attendance

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.attendance.adapter.AttendanceAdapter
import com.atech.attendance.adapter.AttendanceItem
import com.atech.attendance.databinding.FragmentAttendanceBinding
import com.atech.attendance.utils.findPercentage
import com.atech.attendance.utils.showUndoMessage
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import com.atech.theme.DialogModel
import com.atech.theme.ParentActivity
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenStarted
import com.atech.theme.navigate
import com.atech.theme.showDialog
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private val binding: FragmentAttendanceBinding by viewBinding()
    private val viewModel: AttendanceViewModel by activityViewModels()
    private lateinit var attendanceAdapter: AttendanceAdapter
    private var defPercentage = 75

    private val mainActivity by lazy { requireActivity() as ParentActivity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setFab()
            setRecyclerView()
            bottomAppBar()
        }
        observeData()
        listenForUndoMessage()
        handleBackPress()
        observePref()
    }

    private fun observePref() {
        viewModel.getAllPref.observe(viewLifecycleOwner) {
            binding.setDefPercentage(it.defPercentage)
            defPercentage = it.defPercentage
            attendanceAdapter.defPercentage = it.defPercentage
        }
    }

    private fun FragmentAttendanceBinding.setDefPercentage(
        defPercentage: Int
    ) = this.attendanceView.apply {
        attendanceView.tvGoal.text =
            resources.getString(com.atech.theme.R.string.goal, defPercentage.toString())
        binding.attendanceView.tv3.text =
            resources.getString(com.atech.theme.R.string.goal, defPercentage.toString())
        binding.attendanceView.progressCircularInner.progress = defPercentage
    }

    private fun FragmentAttendanceBinding.setRecyclerView() = this.attendanceView.showAtt.apply {
        adapter = AttendanceAdapter(
            ::onItemClick,
            ::onCheckClick,
            ::onWrongClick,
            ::onLongClick
        ).also { attendanceAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onItemClick(model: AttendanceModel) {
        val action = AttendanceFragmentDirections.actionAttendanceFragmentToDetailViewBottomSheet(
            model,
            model.subject,
            defPercentage
        )
        navigate(action)
    }

    private fun onCheckClick(model: AttendanceModel) {
        com.atech.attendance.utils.onCheckClick(viewModel, model)
    }

    private fun onWrongClick(model: AttendanceModel) {
        com.atech.attendance.utils.onWrongClick(viewModel, model)
    }

    private fun onLongClick(model: AttendanceModel) {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToAttendanceMenuBottomSheet(model)
        navigate(action)
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeData() {
        viewModel.unArchive.observe(viewLifecycleOwner) { attendanceList ->
            findPercentage(
                attendanceList.sumOf { it.present }.toFloat(),
                attendanceList.sumOf { it.total }.toFloat()
            ) { present, total ->
                when (total) {
                    0.0F -> 0.0F
                    else -> ((present / total) * 100)
                }
            }.let { binding.setTopView(it) }
            val data: MutableList<AttendanceItem> = attendanceList.map { attendanceModel ->
                AttendanceItem.AttendanceData(attendanceModel)
            } as MutableList<AttendanceItem>
            attendanceAdapter.items = data
            binding.attendanceView.emptyAnimation.isVisible = data.isEmpty()
        }
    }

    private fun FragmentAttendanceBinding.setTopView(finalPercentage: Float) =
        this.attendanceView.apply {
            val emoji = when {
                finalPercentage >= 80F -> resources.getString(com.atech.theme.R.string.moreThan80)
                finalPercentage >= defPercentage -> resources.getString(com.atech.theme.R.string.moreThanDefault)
                finalPercentage < defPercentage && finalPercentage > 60F -> resources.getString(
                    com.atech.theme.R.string.lessThanDefault
                )

                finalPercentage < 60F && finalPercentage != 0F -> resources.getString(com.atech.theme.R.string.lessThan60)
                else -> resources.getString(com.atech.theme.R.string.def_emoji)
            }
            tvPercentage.text = emoji
            materialDivider.text = emoji
            progressCircularOuter.progress = finalPercentage.toInt()
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.FLOOR
            tvOverAll.text = resources.getString(
                com.atech.theme.R.string.overallAttendance, df.format(finalPercentage)
            )
            tv4.text = resources.getString(
                com.atech.theme.R.string.overallAttendance, df.format(finalPercentage)
            )
        }

    private fun FragmentAttendanceBinding.setFab() = this.extendedFab.setOnClickListener {
        navigateToAttendance()
    }

    private fun navigateToAttendance() {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToAddEditAttendanceBottomSheet()
        findNavController().navigate(action)
    }

    private fun FragmentAttendanceBinding.bottomAppBar() = this.bottomAppBar.apply {
        setNavigationOnClickListener {
            mainActivity.toggleDrawer()
        }
        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_book -> true
                R.id.menu_archive -> navigateToArchiveFragment()
                R.id.menu_setting -> navigateToChangePercentageDialog()
                R.id.menu_delete_all -> deleteAll()
                else -> false
            }

        }
    }

    private fun navigateToChangePercentageDialog(): Boolean {
        val direction =
            AttendanceFragmentDirections.actionAttendanceFragmentToChangePercentageDialog(
                defPercentage
            )
        navigate(direction)
        return true
    }

    private fun deleteAll(): Boolean {
        showDialog(
            DialogModel(
                "Delete All",
                "Are you sure you want to delete all attendance?",
                "Yes",
                "No",
                positiveAction = {
                    viewModel.deleteAll()
                    dismiss()
                }
            )
        )
        return true
    }

    private fun navigateToArchiveFragment(): Boolean {
        val action =
            AttendanceFragmentDirections.actionAttendanceFragmentToArchiveBottomSheet(defPercentage.toInt())
        navigate(action)
        return true
    }

    private fun listenForUndoMessage() = launchWhenStarted {
        viewModel.attendanceEvent.collect { attendanceEvent ->
            when (attendanceEvent) {
                is AttendanceViewModel.AttendanceEvent.ShowUndoDeleteMessage -> {
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

    private fun handleBackPress() {
        customBackPress {
            when {
                mainActivity.getDrawerLayout().isDrawerOpen(GravityCompat.START) ->
                    mainActivity.setDrawerState(false)

                else -> findNavController().navigateUp()
            }
        }
    }

}