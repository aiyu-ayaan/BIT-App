package com.aatec.bit.fragments.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.FragmentHomeBinding
import com.aatec.bit.fragments.home.adapter.EventHomeAdapter
import com.aatec.bit.fragments.home.adapter.HolidayHomeAdapter
import com.aatec.bit.fragments.home.adapter.SyllabusHomeAdapter
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.data.ui.event.Event
import com.aatec.core.data.ui.timeTable.TimeTableModel
import com.aatec.core.utils.*
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private var defPercentage = 75
    private lateinit var divider: MaterialDividerItemDecoration

    @Inject
    lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        restoreScroll(binding)
        divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        divider.dividerInsetStart = resources.getDimensionPixelSize(R.dimen.grid_2)
        divider.dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.grid_1_5)
        divider.dividerColor = ContextCompat.getColor(requireContext(), R.color.card_corner)

        val holidayAdapter = HolidayHomeAdapter()
        binding.apply {
            showHolidayLayout.showHoliday.apply {
                addItemDecoration(divider)
                adapter = holidayAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)
            }
            holidayAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

            layoutSubjectExt.setting.setOnClickListener {
                navigateToWelcomeScreen()
            }
            showAttendanceLayout.attendanceClick.setOnClickListener {
                navigateToAttendance()
            }
            layoutSubjectExt.edit.setOnClickListener {
                navigateToEdit()
            }
            showHolidayLayout.textShowAllHoliday.setOnClickListener {
                navigateToHoliday()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.setStateListener(MainStateEvent.GetData)
        }
        viewModel.dataStateMain.observe(viewLifecycleOwner) { dateState ->
            when (dateState) {
                is DataState.Success -> {
                    binding.showHolidayLayout.apply {
                        textHoliday.isVisible = true
                        showHoliday.isVisible = true
                    }
                    holidayAdapter.submitList(dateState.data)
                }
                DataState.Empty -> {
                    binding.showHolidayLayout.apply {
                        textHoliday.isVisible = false
                        showHoliday.isVisible = false
                    }
                }
                is DataState.Error -> {
                    binding.root.showSnackBar("${dateState.exception.message}", -1)
                }
                DataState.Loading -> {
                }
            }
        }

//        SetUpSyllabus
        settingUpSyllabus()

//        Preference Manager
        preferenceManager()

//        set Progress
        setProgressBar()

//        Get syllabus from network
        getSyllabus()

//        setUp Event
        getEvent()

//        Handle on Destroy
        detectScroll()

        setTimeTable()
    }


    private fun setTimeTable() {
        binding.layoutTimeTableExt.apply {
            settingTimeTable.setOnClickListener {
//                navigateToTimeTableSetting()
            }
        }
        lifecycleScope.launchWhenStarted {
            preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) { p ->
                if (p.firstOpenTimeTable) {
                    binding.layoutTimeTableExt.parentTimeTable.isVisible = false
                } else {
                    lifecycleScope.launchWhenStarted {
                        viewModel.defTimeTable.collectLatest { defTT ->
                            when (defTT) {
                                DataState.Empty -> {
                                    Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                is DataState.Error -> {
                                    binding.layoutTimeTableExt.parentTimeTable.isVisible = false
                                }
                                DataState.Loading -> {}
                                is DataState.Success -> {
                                    setViewTimeTable(defTT)
                                }
                            }
                        }
                    }
                }

                viewModel.timeTableQuery.value = QueryTimeTable(
                    p.timeTablePreferences.course,
                    p.timeTablePreferences.gender.name,
                    p.timeTablePreferences.sem,
                    p.timeTablePreferences.section.name
                )
            }
        }
    }

    private fun setViewTimeTable(defTT: DataState.Success<TimeTableModel>) {
        binding.layoutTimeTableExt.parentTimeTable.isVisible = true
        binding.layoutTimeTableExt.defaultTitleTable.apply {
            binding.layoutTimeTableExt.defaultTitleTable.cardViewTt.setOnClickListener {
                navigateToViewImage(
                    defTT.data.imageLink,
                    resources.getString(
                        R.string.time_table_title,
                        defTT.data.course,
                        defTT.data.sem,
                        defTT.data.gender,
                        defTT.data.section
                    )
                )
            }
            defTT.data.imageLink.loadImageDefault(
                binding.root,
                binding.layoutTimeTableExt.defaultTitleTable.imageViewTimeTable,
                binding.layoutTimeTableExt.defaultTitleTable.progressBarDev,
                R.drawable.ic_running_error
            )
            binding.layoutTimeTableExt.defaultTitleTable.textViewTimeTableName.text =
                binding.root.context.resources.getString(
                    R.string.time_table_title,
                    defTT.data.course,
                    defTT.data.sem,
                    defTT.data.gender,
                    defTT.data.section
                )
            binding.layoutTimeTableExt.defaultTitleTable.textViewTimeTableUpdated.text =
                binding.root.context.resources.getString(
                    R.string.time_table_update,
                    defTT.data.created.convertLongToTime("dd/MM/yyyy")
                )
        }

    }


    private fun navigateToViewImage(link: String, title: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
//        val action = NavGraphDirections.actionGlobalFragmentViewImage(link, title)
//        findNavController().navigate(action)
    }


    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun restoreScroll(binding: FragmentHomeBinding) {
        try {
            lifecycleScope.launchWhenStarted {
                viewModel.homeNestedViewPosition.collect { pos ->
                    binding.scrollViewHome.post {
                        binding.scrollViewHome.scrollTo(0, pos)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun getEvent() {
        val eventAdapter = EventHomeAdapter { event, view ->
            onEventClick(event, view)
        }
        binding.showEventLayout.apply {
            showEvent.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            eventAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            textShowAll.setOnClickListener {
                navigateToEvent()
            }
        }
        lifecycleScope.launchWhenStarted {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -14)
            val start = cal.time.convertDateToTime().convertStringToLongMillis() //before 14 days
            cal.add(Calendar.DATE, +15)
            val end = cal.time.convertDateToTime().convertStringToLongMillis() //Day after today
            viewModel.getEvent(start!!, end!!).observe(viewLifecycleOwner) {
                it?.let {
                    binding.showEventLayout.parentHomeExt.isVisible =
                        it.isNotEmpty()
                    eventAdapter.submitList(it)
                }
            }
        }
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun navigateToEvent() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
//        val action = FragmentHomeDirections.actionFragmentHomeToFragmentEvent()
//        findNavController().navigate(action)
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun onEventClick(event: Event, view: View) {
        val extras = FragmentNavigatorExtras(view to event.event_title)
        exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = Hold().apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
//        val action = NavGraphDirections.actionGlobalFragmentSocietyEventDescription(
//            event = event,
//            title = event.society
//        )
//        findNavController().navigate(action, extras)
    }

    private fun getSyllabus() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getSyllabus()
        }
    }

    private fun navigateToEdit() {
//        val directions =
//            NavGraphDirections.actionGlobalBottomSheetEditSubject()
//        findNavController().navigate(directions)
    }


    private fun navigateToAttendance() {
        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
//        val action = FragmentHomeDirections.actionFragmentHomeToFragmentAttendance()
//        findNavController().navigate(action)
    }

    private fun setProgressBar() {
        viewModel.attAttendance.observe(viewLifecycleOwner) {
            var sumPresent = 0
            var sumTotal = 0
            it.forEach { at ->
                sumPresent += at.present
                sumTotal += at.total
            }
            val finalPercentage =
                findPercentage(sumPresent.toFloat(), sumTotal.toFloat()) { present, total ->
                    when (total) {
                        0.0F -> 0.0F
                        else -> ((present / total) * 100)
                    }
                }
            setCondition(finalPercentage.toInt(), sumPresent, sumTotal)
            binding.showAttendanceLayout.apply {
                textViewTotal.text = sumTotal.toString()
                textViewPresent.text = sumPresent.toString()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                textViewPercentage.text = df.format(finalPercentage)
                progressBarHome.progress = finalPercentage.toInt()
                val progressAnimator =
                    ObjectAnimator.ofInt(
                        binding.showAttendanceLayout.progressBarHome,
                        "progress",
                        0,
                        finalPercentage.toInt()
                    )
                progressAnimator.duration = 2000
                progressAnimator.interpolator = LinearInterpolator()
                progressAnimator.start()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCondition(per: Int, present: Int, total: Int) {
        binding.showAttendanceLayout.cardViewAttendance.isVisible = per != 0
        when {
            per >= defPercentage -> {
                val day = calculatedDays(present, total) { p, t ->
                    (((100 * p) - (defPercentage * t)) / defPercentage)
                }.toInt()
                binding.showAttendanceLayout.textViewStats.text = when {
                    per == defPercentage || day <= 0 ->
                        "On track don't miss next class"
                    day != 0 -> "You can leave $day class"
                    else -> "Error !!"
                }
            }
            per < defPercentage -> {
                val day = calculatedDays(present, total) { p, t ->
                    (((defPercentage * t) - (100 * p)) / (100 - defPercentage))
                }
                binding.showAttendanceLayout.textViewStats.text =
                    "Attend Next ${(ceil(day)).toInt()} Class"
            }
        }
    }


    private fun navigateToWelcomeScreen() {
        val action = NavGraphDirections.actionGlobalChooseSemBottomSheet(REQUEST_UPDATE_SEM)
        findNavController().navigate(action)
    }

    private fun preferenceManager() {
        preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            viewModel.syllabusQuery.value = "${it.course}${it.sem}"
        }
    }


    private fun settingUpSyllabus() {
        val syllabusTheoryAdapter = SyllabusHomeAdapter(
            listener = { _, _ ->

            }
        )
        val syllabusLabAdapter = SyllabusHomeAdapter(
            listener = { _, _ ->

            }
        )
        val syllabusPeAdapter = SyllabusHomeAdapter(
            listener = { _, _ ->

            }
        )
        binding.layoutSubjectExt.apply {
            showSubject.apply {
                showTheory.apply {
                    adapter = syllabusTheoryAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                showLab.apply {
                    adapter = syllabusLabAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                showPe.apply {
                    adapter = syllabusPeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
        syllabusLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        syllabusLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        syllabusPeAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY


        viewModel.theory.observe(viewLifecycleOwner) {
            binding.layoutSubjectExt.showSubject.showTheory.isVisible = it.isNotEmpty()
            binding.layoutSubjectExt.showSubject.textView6.isVisible = it.isNotEmpty()
            syllabusTheoryAdapter.submitList(it)
        }

        viewModel.lab.observe(viewLifecycleOwner) {
            binding.layoutSubjectExt.showSubject.showLab.isVisible = it.isNotEmpty()
            binding.layoutSubjectExt.showSubject.textView7.isVisible = it.isNotEmpty()
            binding.layoutSubjectExt.showSubject.dividerTheory.isVisible = it.isNotEmpty()
            syllabusLabAdapter.submitList(it)
        }
        viewModel.pe.observe(viewLifecycleOwner) {
            binding.layoutSubjectExt.showSubject.showPe.isVisible = it.isNotEmpty()
            binding.layoutSubjectExt.showSubject.textView8.isVisible = it.isNotEmpty()
            binding.layoutSubjectExt.showSubject.dividerLab.isVisible =
                it.isNotEmpty()
            syllabusPeAdapter.submitList(it)
        }
    }

    private fun navigateToHoliday() {
//        try {
//            val action = FragmentHomeDirections.actionFragmentHomeToFragmentHoliday()
//            findNavController().navigate(action)
//        } catch (e: Exception) {
//            Toast.makeText(
//                requireContext(),
//                resources.getString(R.string.click_warning),
//                Toast.LENGTH_SHORT
//            ).show()
//        }
    }

    //    Syllabus
//    override fun setOnSyllabusClickListener(syllabusModel: SyllabusModel, view: View) {
//        exitTransition = MaterialElevationScale(false).apply {
//            duration = resources.getInteger(R.integer.duration_medium).toLong()
//        }
//        reenterTransition = MaterialElevationScale(true).apply {
//            duration = resources.getInteger(R.integer.duration_small).toLong()
//        }
//
//        try {
//            val extras = FragmentNavigatorExtras(view to syllabusModel.openCode)
//            val action =
//                FragmentHomeDirections.actionFragmentHomeToFragmentSubjectHandler(syllabusModel)
//            findNavController().navigate(action, extras)
//        } catch (e: Exception) {
//            Toast.makeText(
//                requireContext(),
//                resources.getString(R.string.click_warning),
//                Toast.LENGTH_SHORT
//            )
//                .show()
//        }
//}

/**
 * @since 4.0.4
 * @author Ayaan
 */
private fun detectScroll() {
//        binding.scrollViewHome.setOnScrollChangeListener { _, _, scrollY, _, _ ->
//            when (scrollY) {
//                0 -> {
//                    activity?.changeStatusBarToolbarColor(
//                        R.color.MainColor
//                    )
//                }
//                else -> {
//                    activity?.changeStatusBarToolbarColor(
//                        R.color.bottom_nav
//                    )
//                }
//            }
//        }
}

//    override fun onPause() {
//        super.onPause()
//        activity?.changeStatusBarToolbarColor(
//            R.color.MainColor
//        )
//    }

//    override fun onStop() {
//        try {
//            super.onStop()
//            viewModel.homeNestedViewPosition.value = binding.scrollViewHome.scrollY
//        } catch (e: Exception) {
//            Log.e("Error", e.message!!)
//        }
//    }

//    private fun navigateToTimeTableSetting() {
//        val action =
//            NavGraphDirections.actionGlobalBottomSheetTimeTableSetting(
//                REQUEST_OPEN_SETTING_FROM_HOME
//            )
//        findNavController().navigate(action)
//    }
}