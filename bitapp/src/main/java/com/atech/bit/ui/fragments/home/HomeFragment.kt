package com.atech.bit.ui.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.withTransaction
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHomeBinding
import com.atech.bit.ui.activity.main_activity.MainActivity
import com.atech.bit.ui.activity.main_activity.viewmodels.CommunicatorViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.ui.activity.main_activity.viewmodels.UserDataViewModel
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.ui.fragments.course.CourseFragment
import com.atech.bit.ui.fragments.event.EventsAdapter
import com.atech.bit.ui.fragments.home.adapter.AttendanceHomeAdapter
import com.atech.bit.ui.fragments.home.adapter.HolidayHomeAdapter
import com.atech.bit.ui.fragments.home.adapter.SyllabusHomeAdapter
import com.atech.bit.utils.Encryption.decryptText
import com.atech.bit.utils.Encryption.getCryptore
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.getUid
import com.atech.bit.utils.loadAdds
import com.atech.bit.utils.sortBySno
import com.atech.core.data.network.user.UserModel
import com.atech.core.data.preferences.Cgpa
import com.atech.core.data.room.BitDatabase
import com.atech.core.data.room.syllabus.SyllabusList
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.data.ui.events.Events
import com.atech.core.utils.DataState
import com.atech.core.utils.GITHUB_LINK
import com.atech.core.utils.KEY_COURSE_OPEN_FIRST_TIME
import com.atech.core.utils.KEY_DO_NOT_SHOW_AGAIN
import com.atech.core.utils.KEY_IS_USER_LOG_IN
import com.atech.core.utils.KEY_REACH_TO_HOME
import com.atech.core.utils.KEY_USER_HAS_DATA_IN_DB
import com.atech.core.utils.REQUEST_EVENT_FROM_HOME
import com.atech.core.utils.REQUEST_LOGIN_FROM_HOME
import com.atech.core.utils.REQUEST_UPDATE_SEM
import com.atech.core.utils.RemoteConfigUtil
import com.atech.core.utils.TAG
import com.atech.core.utils.calculatedDays
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.checkForAPI33
import com.atech.core.utils.findPercentage
import com.atech.core.utils.loadImageCircular
import com.atech.core.utils.onScrollColorChange
import com.atech.core.utils.showSnackBar
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()
    private val communicatorViewModel: CommunicatorViewModel by activityViewModels()
    private val preferencesManagerViewModel: PreferenceManagerViewModel by activityViewModels()
    private val userDataViewModel by activityViewModels<UserDataViewModel>()
    private var defPercentage = 75
    private lateinit var syllabusPeAdapter: SyllabusHomeAdapter
    private lateinit var syllabusTheoryAdapter: SyllabusHomeAdapter
    private lateinit var syllabusLabAdapter: SyllabusHomeAdapter
    private lateinit var holidayAdapter: HolidayHomeAdapter
    private var userModel: UserModel? = null

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var remoteConfig: RemoteConfigUtil

    @Inject
    lateinit var bitDatabase: BitDatabase

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        if (myScrollViewerInstanceState != null) {
            binding.scrollViewHome.onRestoreInstanceState(CourseFragment.myScrollViewerInstanceState)
        }

        holidayAdapter = HolidayHomeAdapter()
        binding.apply {
            lineChartCgpa.setNoDataText(resources.getString(R.string.loading))
            setting.setOnClickListener {
                navigateToWelcomeScreen()
            }

            attendanceClick.root.setOnClickListener {
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


//        SetUpSyllabus
        settingUpSyllabus()

//        Preference Manager
        preferenceManager()

//        set Progress
        setProgressBar()


//        setUp Event
        getEvent()

//        Handle on Destroy
        detectScroll()

//        setTimeTable()
        getData()
        setHoliday()

        createMenu()



        setUpLinkClick()

        checkHasData()
        setPref()
        restoreScroll()
        setAds()
        getOldAppWarningDialog()
        clearAndAddSyllabusDatabase()

        checkPermission()
    }

    private fun checkPermission() {
        if (checkForAPI33()) {
            checkNotificationPermission()
            requestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    Log.d(TAG, "checkNotificationPermission: Granted")
                } else {
                    Snackbar.make(
                        binding.root,
                        "Please grant Notification permission from App Settings",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "requestPermission: Granted")
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun clearAndAddSyllabusDatabase() {
        val isSyllabusDataRefresh = pref.getBoolean(KEY_COURSE_OPEN_FIRST_TIME, true)
        if (isSyllabusDataRefresh) lifecycleScope.launchWhenStarted {
            bitDatabase.withTransaction {
                bitDatabase.syllabusDao().deleteAll()
                SyllabusList.syllabus.also {
                    bitDatabase.syllabusDao().insertAll(it)
                }
                pref.edit().putBoolean(KEY_COURSE_OPEN_FIRST_TIME, false).apply()
            }
        }
    }

    private fun setAds() {
        requireContext().loadAdds(binding.adView)
    }

    private fun setPref() {
        pref.edit().putBoolean(
            KEY_REACH_TO_HOME, true
        ).apply()
    }

    private fun checkHasData() {
        val isLogin = pref.getBoolean(KEY_IS_USER_LOG_IN, false)
        if (auth.currentUser != null && isLogin) userDataViewModel.checkUserData(getUid(auth)!!, {
            if (!it) {
                preferencesManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) { dataStore ->
                    userDataViewModel.addCourseSem(getUid(auth)!!,
                        dataStore.course,
                        dataStore.sem,
                        {
                            pref.edit().putBoolean(KEY_USER_HAS_DATA_IN_DB, true).apply()
                        }) {
                        Toast.makeText(
                            requireContext(), "Data upload failed", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }) {}
    }

    private fun setUpLinkClick() {
        binding.layoutNoteDev.tagInfo.setOnClickListener {
            remoteConfig.fetchData({
                Log.e(TAG, "setUpLinkClick: ${it.message}")
            }) {
                try {
                    val link = remoteConfig.getString(GITHUB_LINK).trim()
                    requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                } catch (e: Exception) {
                    Log.e(TAG, "setUpLinkClick: ${e.message}")
                }
            }
        }
    }

    private fun getOldAppWarningDialog() {
        val u = pref.getBoolean(KEY_DO_NOT_SHOW_AGAIN, false)
        if (isOldAppInstalled() && !communicatorViewModel.uninstallDialogSeen && !u) navigateToUninstallOldAppDialog()
    }

    private fun navigateToUninstallOldAppDialog() {
        val action = NavGraphDirections.actionGlobalUninstallOldAppDialog()
        findNavController().navigate(action)
    }

    @Suppress("deprecation")
    private fun isOldAppInstalled(): Boolean {
        var available = true
        try {
            // check if available
            requireContext().packageManager.getPackageInfo("com.aatec.bit", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            // if not available set
            // available as false
            available = false
        }
        return available
    }

    private fun navigateToCGPA() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = HomeFragmentDirections.actionHomeFragmentToCgpaCalculatorFragment()
        findNavController().navigate(action)
    }


    private fun createMenu() {
        addMenuHost(R.menu.menu_toolbar, {
            Handler(Looper.getMainLooper()).post {
                val menuItem = it.findItem(R.id.menu_profile)
                val view = menuItem.actionView as FrameLayout
                val imageView = view.findViewById<ImageView>(R.id.toolbar_profile_image)
                imageView?.let {
                    setProfileImageView(it)
                }
            }
        }) {
            when (it.itemId) {
                R.id.menu_search -> {
                    (requireActivity() as MainActivity).onMenuClick()
                    true
                }

                else -> false
            }
        }
    }

    private fun setProfileImageView(imageView: ImageView) {
        imageView.apply {
            if (auth.currentUser != null) {
                auth.currentUser?.photoUrl.toString().loadImageCircular(this)
                getDataOFUser()
                setOnClickListener {
                    userModel?.let {
                        navigateToProfile(getUid(auth)!!, it)
                    }
                }
            } else {
                imageView.setImageResource(
                    R.drawable.ic_account
                )
                setOnClickListener {
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action =
            HomeFragmentDirections.actionHomeFragmentToLogInFragment(request = REQUEST_LOGIN_FROM_HOME)
        findNavController().navigate(action)
    }

    private fun getDataOFUser() = lifecycleScope.launchWhenStarted {
        val uid = getUid(auth)!!
        userDataViewModel.getUser(uid, {
            userModel = convertEncryptedData(uid, it)
        }, {
            Toast.makeText(requireContext(), "Something went wrong !!", Toast.LENGTH_SHORT).show()
        })
    }

    private fun convertEncryptedData(uid: String, user: UserModel): UserModel? {
        try {
            val cryptore = context?.getCryptore(uid)
            val email = cryptore?.decryptText(user.email)
            val name = cryptore?.decryptText(user.name)
            val profilePic = cryptore?.decryptText(user.profilePic)
            return UserModel(
                email = email,
                name = name,
                profilePic = profilePic,
                uid = user.uid,
                syncTime = user.syncTime
            )

        } catch (e: Exception) {
            Log.e(TAG, "convertEncryptedData: $e")
            return null
        }
    }

    private fun navigateToProfile(uid: String, userDecrypt: UserModel) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment(uid, userDecrypt)
        findNavController().navigate(action)
    }

    private fun setHoliday() = binding.apply {
        showHoliday.apply {
            adapter = holidayAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
            addItemDecoration(
                DividerItemDecorationNoLast(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                ).apply {
                    setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                })
        }
        holidayAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }


    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun getEvent() {
        val eventAdapter = EventsAdapter(
            db,
            {
                navigateToImageView(it)
            },
        ) { event, rootView ->
            navigateToEventDetail(event, rootView)
        }
        binding.apply {
            showEvent.apply {
                adapter = eventAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(this)
                eventIndicator.attachToRecyclerView(this, snapHelper)
                eventAdapter.registerAdapterDataObserver(eventIndicator.adapterDataObserver);
            }
            eventAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            textShowAllEvent.setOnClickListener {
                navigateToEvent()
            }
        }
        viewModel.getEvent(
            communicatorViewModel.instanceBefore14Days!!,
            communicatorViewModel.instanceAfter15Days!!
        ).observe(viewLifecycleOwner) {
            it?.let {
                eventAdapter.submitList(it)
                binding.materialCardViewEventRecyclerView.isVisible = it.isNotEmpty()
                binding.textEvent.isVisible = it.isNotEmpty()
                binding.textShowAllEvent.isVisible = it.isNotEmpty()
                binding.eventIndicator.isVisible = it.isNotEmpty()
            }
        }

    }

    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun navigateToEvent() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = HomeFragmentDirections.actionHomeFragmentToEventFragment()
        findNavController().navigate(action)
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun navigateToEventDetail(event: Events, view: View) {
        val extras = FragmentNavigatorExtras(view to event.path)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val action = NavGraphDirections.actionGlobalEventDetailFragment(
            path = event.path, request = REQUEST_EVENT_FROM_HOME
        )
        findNavController().navigate(action, extras)
    }


    private fun navigateToEdit() {
        val directions = NavGraphDirections.actionGlobalEditSubjectBottomSheet()
        findNavController().navigate(directions)
    }


    private fun navigateToAttendance() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        val action = HomeFragmentDirections.actionHomeFragmentToAttendanceFragment()
        findNavController().navigate(action)
    }

    private fun setProgressBar() {
        val attendanceHomeAdapter = AttendanceHomeAdapter(defPercentage)
        binding.attendanceClick.recyclerViewShowAttendance.apply {
            adapter = attendanceHomeAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(false)
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
            binding.attendanceClick.attendanceIndicator.attachToRecyclerView(this, snapHelper)
            attendanceHomeAdapter.registerAdapterDataObserver(binding.attendanceClick.attendanceIndicator.adapterDataObserver);
        }
        viewModel.attAttendance.observe(viewLifecycleOwner) {
            attendanceHomeAdapter.submitList(it)
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
            binding.attendanceClick.apply {
                textViewTotal.text = sumTotal.toString()
                textViewPresent.text = sumPresent.toString()
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.FLOOR
                textViewOverAllAttendance.text =
                    resources.getString(R.string.overallAttendance, df.format(finalPercentage))
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
                binding.attendanceClick.textViewStats.text = when {
                    per == defPercentage || day == 0 -> "On track don't miss next class"

                    day > 0 -> "You can leave $day class"
                    else -> "Error !!"
                }
            }

            else -> {
                val day = calculatedDays(present, total) { p, t ->
                    (((defPercentage * t) - (100 * p)) / (100 - defPercentage))
                }
                binding.attendanceClick.textViewStats.text =
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
            binding.materialCardViewCgpaGraph.isVisible = !it.cgpa.isAllZero
            binding.textViewCgpa.isVisible = !it.cgpa.isAllZero
            binding.textViewEdit.isVisible = !it.cgpa.isAllZero
            if (!it.cgpa.isAllZero) setUpChart(it.cgpa)
        }
    }

    private fun setUpChart(cgpa: Cgpa) = lifecycleScope.launchWhenStarted {
        binding.lineChartCgpa.apply {
            val list = mutableListOf(
                Entry(0f, 0f)
            )
            addData(list, cgpa)
            val barDataSet = LineDataSet(list, "SGPA")
            barDataSet.apply {
                color = MaterialColors.getColor(
                    binding.root, androidx.appcompat.R.attr.colorPrimary, Color.WHITE
                )
                valueTextSize = 10f
                valueTextColor =
                    MaterialColors.getColor(binding.root, R.attr.textColor, Color.WHITE)
            }
            val barData = LineData(barDataSet)
            xAxis.setLabelCount(list.size, /*force: */true)
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

    private fun addData(list: MutableList<Entry>, cgpa: Cgpa) {
        if (cgpa.sem1 != 0.0) list.add(Entry(1f, cgpa.sem1.toFloat()))
        if (cgpa.sem2 != 0.0) list.add(Entry(2f, cgpa.sem2.toFloat()))
        if (cgpa.sem3 != 0.0) list.add(Entry(3f, cgpa.sem3.toFloat()))
        if (cgpa.sem4 != 0.0) list.add(Entry(4f, cgpa.sem4.toFloat()))
        if (cgpa.sem5 != 0.0) list.add(Entry(5f, cgpa.sem5.toFloat()))
        if (cgpa.sem6 != 0.0) list.add(Entry(6f, cgpa.sem6.toFloat()))
    }

    private fun settingUpSyllabus() {
        syllabusTheoryAdapter = SyllabusHomeAdapter(listener = { s, v ->
            setOnSyllabusClickListener(s, v)
        })
        syllabusLabAdapter = SyllabusHomeAdapter(listener = { s, v ->
            setOnSyllabusClickListener(s, v)
        })
        syllabusPeAdapter = SyllabusHomeAdapter(listener = { s, v ->
            setOnSyllabusClickListener(s, v)
        })
        binding.apply {

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
        syllabusLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        syllabusLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        syllabusPeAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

    }

    private fun getData() = lifecycleScope.launchWhenStarted {
        viewModel.theory.observe(viewLifecycleOwner) {
            binding.showTheory.isVisible = it.isNotEmpty()
            binding.textView6.isVisible = it.isNotEmpty()
            syllabusTheoryAdapter.submitList(it)
        }

        viewModel.lab.observe(viewLifecycleOwner) {
            binding.showLab.isVisible = it.isNotEmpty()
            binding.textView7.isVisible = it.isNotEmpty()
            binding.dividerTheory.isVisible = it.isNotEmpty()
            syllabusLabAdapter.submitList(it)
        }
        viewModel.pe.observe(viewLifecycleOwner) {
            binding.showPe.isVisible = it.isNotEmpty()
            binding.textView8.isVisible = it.isNotEmpty()
            binding.dividerLab.isVisible = it.isNotEmpty()
            syllabusPeAdapter.submitList(it)
        }
        viewModel.getHoliday().observe(viewLifecycleOwner) { dateState ->
            when (dateState) {
                is DataState.Success -> {
                    binding.apply {
                        textHoliday.isVisible = true
                        materialCardViewHolidayRecyclerView.isVisible = true
                        textShowAllHoliday.isVisible = true
                    }
                    holidayAdapter.submitList(dateState.data.holidays.sortBySno())
                }

                DataState.Empty -> binding.apply {
                    textHoliday.isVisible = false
                    materialCardViewHolidayRecyclerView.isVisible = false
                    textShowAllHoliday.isVisible = false
                }

                is DataState.Error -> binding.root.showSnackBar(
                    "${dateState.exception.message}", -1
                )

                DataState.Loading -> {
                }
            }
        }
    }

    private fun navigateToHoliday() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        try {
            val action = HomeFragmentDirections.actionHomeFragmentToHolidayFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), resources.getString(R.string.click_warning), Toast.LENGTH_SHORT
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
            val action = NavGraphDirections.actionGlobalSubjectHandlerFragment(syllabusModel)
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(), resources.getString(R.string.click_warning), Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        activity?.onScrollColorChange(binding.scrollViewHome, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar, com.google.android.material.R.attr.colorSurface
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar, R.attr.bottomBar
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
                    0, communicatorViewModel.homeNestedViewPosition!!
                )
            }
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }


    override fun onPause() {
        super.onPause()
        myScrollViewerInstanceState = binding.scrollViewHome.onSaveInstanceState()
    }

    companion object {
        var myScrollViewerInstanceState: Parcelable? = null
    }


}