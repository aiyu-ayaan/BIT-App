package com.aatec.bit.ui.fragments.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
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
import com.aatec.bit.databinding.FragmentHomeBinding
import com.aatec.bit.ui.activity.main_activity.MainActivity
import com.aatec.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.aatec.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.ui.custom_views.DividerItemDecorationNoLast
import com.aatec.bit.ui.fragments.home.adapter.EventHomeAdapter
import com.aatec.bit.ui.fragments.home.adapter.HolidayHomeAdapter
import com.aatec.bit.ui.fragments.home.adapter.SyllabusHomeAdapter
import com.aatec.bit.utils.MainStateEvent
import com.aatec.bit.utils.addMenuHost
import com.aatec.core.data.preferences.Cgpa
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.aatec.core.data.ui.event.Event
import com.aatec.core.utils.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()
    private val communicatorViewModel: CommunicatorViewModel by activityViewModels()
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private var defPercentage = 75
    private lateinit var syllabusPeAdapter: SyllabusHomeAdapter
    private lateinit var syllabusTheoryAdapter: SyllabusHomeAdapter
    private lateinit var syllabusLabAdapter: SyllabusHomeAdapter
    private lateinit var holidayAdapter: HolidayHomeAdapter

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
        restoreScroll()


        holidayAdapter = HolidayHomeAdapter()
        binding.apply {
            lineChartCgpa.setNoDataText(resources.getString(R.string.loading))
            setting.setOnClickListener {
                navigateToWelcomeScreen()
            }

            attendanceClick.setOnClickListener {
                navigateToAttendance()
            }
            edit.setOnClickListener {
                navigateToEdit()
            }
            textShowAllHoliday.setOnClickListener {
                navigateToHoliday()
            }
            textViewEdit.setOnClickListener {
                navigateToCGPA()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.setStateListener(MainStateEvent.GetData)
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

//        setTimeTable()
        getData()
        setHoliday()

        createMenu()


    }

    private fun navigateToCGPA() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = HomeFragmentDirections.actionHomeFragmentToCgpaCalculatorFragment()
        findNavController().navigate(action)
    }


    private fun createMenu() {
        addMenuHost(R.menu.menu_toolbar) {
            when (it.itemId) {
                R.id.menu_search -> {
                    (requireActivity() as MainActivity).onMenuClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun setHoliday() = binding.apply {
        showHoliday.apply {
            addItemDecoration(DividerItemDecorationNoLast(
                requireContext(),
                LinearLayoutManager.VERTICAL
            ).apply {
                setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            }
            )
            adapter = holidayAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }
        holidayAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }


    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun getEvent() {
        val eventAdapter = EventHomeAdapter { event, view ->
            onEventClick(event, view)
        }
        binding.apply {
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
                    binding.parentHomeExt.isVisible =
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
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = HomeFragmentDirections.actionHomeFragmentToEventFragment()
        findNavController().navigate(action)
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun onEventClick(event: Event, view: View) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        val action = NavGraphDirections.actionGlobalEventDescriptionFragment(
            path = event.path,
            title = event.society
        )
        findNavController().navigate(action)
    }

    private fun getSyllabus() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getSyllabus()
        }
    }

    private fun navigateToEdit() {
        val directions =
            NavGraphDirections.actionGlobalEditSubjectBottomSheet()
        findNavController().navigate(directions)
    }


    private fun navigateToAttendance() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        val action = HomeFragmentDirections.actionHomeFragmentToAttendanceFragment()
        findNavController().navigate(action)
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
            binding.apply {
                textViewTotal.text = sumTotal.toString()
                textViewPresent.text = sumPresent.toString()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                textViewPercentage.text = df.format(finalPercentage)
                progressBarHome.progress = finalPercentage.toInt()
                val progressAnimator =
                    ObjectAnimator.ofInt(
                        binding.progressBarHome,
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
        binding.cardViewAttendance.isVisible = per != 0
        when {
            per >= defPercentage -> {
                val day = calculatedDays(present, total) { p, t ->
                    (((100 * p) - (defPercentage * t)) / defPercentage)
                }.toInt()
                binding.textViewStats.text = when {
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
                binding.textViewStats.text =
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
            binding.parentCgpaChart.isVisible = !it.cgpa.isAllZero
            if (!it.cgpa.isAllZero)
                setUpChart(it.cgpa)
        }
    }

    private fun setUpChart(cgpa: Cgpa) = lifecycleScope.launchWhenStarted {
        binding.lineChartCgpa.apply {

            val list = mutableListOf(
                BarEntry(1f, cgpa.sem1.toFloat()),
                BarEntry(2f, cgpa.sem2.toFloat()),
                BarEntry(3f, cgpa.sem3.toFloat()),
                BarEntry(4f, cgpa.sem4.toFloat()),
                BarEntry(5f, cgpa.sem5.toFloat()),
                BarEntry(6f, cgpa.sem6.toFloat())
            )
            val barDataSet = BarDataSet(list, "SGPA")
            barDataSet.apply {
                color = MaterialColors.getColor(
                    binding.root,
                    androidx.appcompat.R.attr.colorPrimary,
                    Color.WHITE
                )
                valueTextSize = 10f
                valueTextColor =
                    MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            }
            val barData = BarData(barDataSet)
            barData.apply {
                barWidth = 0.5f
            }

            legend.textColor = MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            xAxis.textColor = MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            axisLeft.textColor =
                MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            axisRight.textColor =
                MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            description.text = "Average CGPA :- ${DecimalFormat("#0.00").format(cgpa.cgpa)}"
            description.textColor =
                MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            setPinchZoom(false)
            setScaleEnabled(false)
            this.data = barData
        }
    }

    private fun settingUpSyllabus() {
        syllabusTheoryAdapter = SyllabusHomeAdapter(
            listener = { s, v ->
                setOnSyllabusClickListener(s, v)
            }
        )
        syllabusLabAdapter = SyllabusHomeAdapter(
            listener = { s, v ->
                setOnSyllabusClickListener(s, v)
            }
        )
        syllabusPeAdapter = SyllabusHomeAdapter(
            listener = { s, v ->
                setOnSyllabusClickListener(s, v)
            }
        )
        binding.apply {
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

    }

    private fun getData() = lifecycleScope.launchWhenStarted {
        viewModel.theory.observe(viewLifecycleOwner) {
            binding.showSubject.showTheory.isVisible = it.isNotEmpty()
            binding.showSubject.textView6.isVisible = it.isNotEmpty()
            syllabusTheoryAdapter.submitList(it)
        }

        viewModel.lab.observe(viewLifecycleOwner) {
            binding.showSubject.showLab.isVisible = it.isNotEmpty()
            binding.showSubject.textView7.isVisible = it.isNotEmpty()
            binding.showSubject.dividerTheory.isVisible = it.isNotEmpty()
            syllabusLabAdapter.submitList(it)
        }
        viewModel.pe.observe(viewLifecycleOwner) {
            binding.showSubject.showPe.isVisible = it.isNotEmpty()
            binding.showSubject.textView8.isVisible = it.isNotEmpty()
            binding.showSubject.dividerLab.isVisible =
                it.isNotEmpty()
            syllabusPeAdapter.submitList(it)
        }
        viewModel.dataStateMain.observe(viewLifecycleOwner) { dateState ->
            when (dateState) {
                is DataState.Success -> {
                    binding.apply {
                        textHoliday.isVisible = true
                        showHoliday.isVisible = true
                        textShowAllHoliday.isVisible = true
                    }
                    holidayAdapter.submitList(dateState.data)
                }
                DataState.Empty -> binding.apply {
                    textHoliday.isVisible = false
                    showHoliday.isVisible = false
                    textShowAllHoliday.isVisible = false
                }

                is DataState.Error ->
                    binding.root.showSnackBar("${dateState.exception.message}", -1)

                DataState.Loading -> {
                }
            }
        }
    }

    private fun navigateToHoliday() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        try {
            val action = HomeFragmentDirections.actionHomeFragmentToHolidayFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.click_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //        Syllabus
    private fun setOnSyllabusClickListener(syllabusModel: SyllabusModel, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_small).toLong()
        }

        try {
            val extras = FragmentNavigatorExtras(view to syllabusModel.openCode)
            val action =
                NavGraphDirections.actionGlobalSubjectHandlerFragment(syllabusModel)
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.click_warning),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        activity?.onScrollColorChange(binding.scrollViewHome, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                com.google.android.material.R.attr.colorSurface
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                R.attr.bottomBar
            )
        })

    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun restoreScroll() {
        try {
            if (communicatorViewModel.homeNestedViewPosition != null) {
                binding.scrollViewHome.scrollTo(
                    0,
                    communicatorViewModel.homeNestedViewPosition!!
                )
            }
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    override fun onStop() {
        super.onStop()
        communicatorViewModel.homeNestedViewPosition = binding.scrollViewHome.scrollY
    }


//    private fun navigateToTimeTableSetting() {
//        val action =
//            NavGraphDirections.actionGlobalBottomSheetTimeTableSetting(
//                REQUEST_OPEN_SETTING_FROM_HOME
//            )
//        findNavController().navigate(action)
//    }
}