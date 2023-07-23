package com.atech.bit.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHomeBinding
import com.atech.bit.ui.fragments.home.adapter.HomeAdapter
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.room.library.LibraryModel
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.CalendarReminder
import com.atech.core.utils.DEFAULT_QUERY
import com.atech.core.utils.Destination
import com.atech.core.utils.TAGS
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.utils.onScrollChange
import com.atech.course.utils.tabSelectedListener
import com.atech.theme.Axis
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.ParentActivity
import com.atech.theme.customBackPress
import com.atech.theme.exitTransition
import com.atech.theme.launchWhenCreated
import com.atech.theme.loadCircular
import com.atech.theme.navigate
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.toast
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home,Axis.Y) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()

    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }


    private lateinit var homeAdapter: HomeAdapter

    private lateinit var searchAdapter: HomeAdapter

    @Inject
    lateinit var authUseCases: AuthUseCases

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeData(viewLifecycleOwner)
        binding.apply {
            setProfile()
            setDrawerOpen()
            toolbarIcon()
            setRecyclerView()
            bindTabLayout()
            setSearchRecyclerView()
            setSearchView()
        }
        handleExpand()
        handleBackPress()
        hideBottomAppBar()
        navigateToAboutUs()
    }

    private fun FragmentHomeBinding.setProfile() = this.ivUserProfileImage.apply {
        if (!authUseCases.hasLogIn()) {
            setOnClickListener {
                navigateToLogin()
            }
            return@apply
        }
        authUseCases.userData.invoke { (data, exception) ->
            if (exception != null) {
                toast("Something went wrong ${exception.message}")
                Log.e(TAGS.BIT_ERROR.name, "setProfile: $exception")
                return@invoke
            }
            data?.profilePic?.let {
                loadCircular(it)
            }
        }
        setOnClickListener {
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + Destination.Profile.value
        )
    }

    private fun navigateToLogin() {
        exitTransition(Axis.Z)
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + Destination.LogIn.value
        )
    }


    private fun hideBottomAppBar() {
        binding.recyclerView.onScrollChange(topScroll = {
            mainActivity.setBottomNavigationVisibility(true)
        }, bottomScroll = {
            mainActivity.setBottomNavigationVisibility(false)
        })
    }

    private fun FragmentHomeBinding.setDrawerOpen() = this.searchBar.setNavigationOnClickListener {
        mainActivity.setDrawerState(true)
    }


    private fun handleExpand() {
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                mainActivity.setBottomNavigationVisibility(false)
            }
            if (newState == SearchView.TransitionState.HIDING) {
                mainActivity.setBottomNavigationVisibility(true)
                viewModel.searchQuery.value = DEFAULT_QUERY
                try {
                    binding.searchExt.tabLayoutSearchType.selectTab(
                        binding.searchExt.tabLayoutSearchType.getTabAt(
                            0
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAGS.BIT_ERROR.name, "handleExpand: $e")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.setBottomNavigationVisibility(!binding.searchView.isShowing)
    }

    private fun handleBackPress() {
        customBackPress {
            when {
                mainActivity.getDrawerLayout()
                    .isDrawerOpen(GravityCompat.START) -> mainActivity.setDrawerState(false)

                else -> {
                    if (binding.searchView.isShowing) binding.searchView.hide()
                    else activity?.finish()

                }
            }
        }
    }

    private fun FragmentHomeBinding.toolbarIcon() = this.apply {
        ibNotice.setOnClickListener {
            navigateToNoticeFragment()
        }
    }

    private fun navigateToNoticeFragment() {
        exitTransition(Axis.Z)
        val action = HomeFragmentDirections.actionHomeFragmentToNoticeFragment()
        navigate(action)
    }

    private fun FragmentHomeBinding.setRecyclerView() = this.recyclerView.apply {
        adapter = HomeAdapter(
            switchClick = ::switchClick,
            switch = ::switchApply,
            onEventClick = ::navigateToEventDetails,
            onSubjectClick = ::navigateToSubjectDetails,
            onSettingClick = ::navigateToSemChoose,
            onDeleteClick = ::onDeleteClick,
            onMarkAsReturnClick = ::onLibraryEditClick,
            onEditClick = ::navigateToEditSyllabus
        ).also { homeAdapter = it }
        layoutManager = LinearLayoutManager(context)
        observeData()
    }

    private fun navigateToEditSyllabus() {
        val action = HomeFragmentDirections.actionHomeFragmentToEditBottomSheet(viewModel.courseSem)
        navigate(action)
    }

    private fun onLibraryEditClick(it: LibraryModel) {
        val markAsReturn = it.markAsReturn
        if (it.eventId != -1L) {
            CalendarReminder.deleteEvent(requireContext(), it.eventId)
        }
        viewModel.updateBook(
            it.copy(
                eventId = -1L, alertDate = 0L, markAsReturn = !markAsReturn
            )
        )
    }


    private fun onDeleteClick(libraryModel: LibraryModel) {
        if (libraryModel.eventId != -1L) {
            CalendarReminder.deleteEvent(requireContext(), libraryModel.eventId)
        }
        viewModel.deleteBook(libraryModel)
    }

    private fun navigateToSemChoose() {
        navigateWithInAppDeepLink(BASE_IN_APP_NAVIGATION_LINK + Destination.ChooseSem.value)
    }

    private fun navigateToSubjectDetails(syllabusUIModel: SyllabusUIModel) {
        exitTransition(Axis.X)
        HomeFragmentDirections.actionHomeFragmentToViewSyllabusNavGraph().also {
            navigate(it.actionId, Bundle().apply {
                putString("courseSem", viewModel.courseSem.lowercase())
                putParcelable("model", syllabusUIModel)
            })
        }
    }

    private fun navigateToEventDetails(path: String) {
        exitTransition(Axis.X)
        val action = NavGraphDirections.actionGlobalEventDetailFragment(path)
        navigate(action)
    }

    private fun switchApply(materialSwitch: MaterialSwitch) {
        materialSwitch.isChecked = viewModel.isOnline.value
        this.apply {
            if (materialSwitch.isChecked) materialSwitch.setThumbIconResource(com.atech.theme.R.drawable.round_cloud_24)
            else materialSwitch.setThumbIconResource(com.atech.theme.R.drawable.round_cloud_off_24)
        }
    }


    private fun switchClick(isChecked: Boolean) {
        viewModel.isOnline.value = isChecked
    }

    private fun observeData() = launchWhenCreated {
        viewModel.homeScreenData.collectLatest {
            homeAdapter.items = it.toMutableList()
            try {
                binding.loading.root.isVisible = it.isEmpty()
            } catch (e: Exception) {
                Log.e(TAGS.BIT_ERROR.name, "observeData: $e")
            }
            homeAdapter.defPercentage = viewModel.defPercentage
        }
    }

//    ________________________________________ Search _______________________________________________

    enum class SearchItems(
        val value: String,
    ) {
        All("All"),

        /* SyllabusOnline("Syllabus Online"),*/
        SyllabusOffline("Syllabus Offline"), Holiday("Holiday"), Notice("Notice"), Event("Event")
    }

    private fun FragmentHomeBinding.bindTabLayout() = this.searchExt.tabLayoutSearchType.apply {
        SearchItems.values().forEach {
            addTab(newTab().setText(it.value))
        }
        tabSelectedListener { tab ->
            findEnum(tab?.text.toString())?.setState()?.let { state ->
                viewModel.filterState.value = state
            }
        }
    }

    private fun findEnum(value: String) = SearchItems.values().find { it.value == value }

    private fun SearchItems.setState() = when (this) {
        SearchItems.All -> HomeViewModel.FilterState().copy(all = true)

        /*        SearchItems.SyllabusOnline ->
                    HomeViewModel.FilterState().copy(syllabusOnline = true)*/

        SearchItems.SyllabusOffline -> HomeViewModel.FilterState().copy(syllabusOffline = true)

        SearchItems.Holiday -> HomeViewModel.FilterState().copy(holiday = true)

        SearchItems.Notice -> HomeViewModel.FilterState().copy(notice = true)

        SearchItems.Event -> HomeViewModel.FilterState().copy(event = true)
    }

    private fun FragmentHomeBinding.setSearchView() = this.searchView.apply {
        editText.doOnTextChanged { text, _, _, _ ->
            viewModel.searchQuery.value = text.toString()
        }
    }

    private fun FragmentHomeBinding.setSearchRecyclerView() =
        this.searchExt.recyclerViewSearch.apply {
            adapter = HomeAdapter(
                onEventClick = ::navigateToEventDetails,
                onSubjectClick = ::navigateToSubjectDetails,
                onNoticeClick = ::navigateToNoticeDetails
            ).also { searchAdapter = it }
            layoutManager = LinearLayoutManager(context)
            observeSearchAdapter()
        }

    private fun navigateToNoticeDetails(path: String) {
        exitTransition(Axis.X)
        val action = NavGraphDirections.actionGlobalNoticeDetailFragment(path)
        navigate(action)
    }

    private fun observeSearchAdapter() = launchWhenCreated {
        viewModel.homeScreenSearchData.collectLatest {
            binding.searchExt.empty.isVisible = it.isEmpty()
            searchAdapter.items = it.toMutableList()
        }
    }

    private fun navigateToAboutUs() {
        mainActivity.navigateToAboutUs {
            exitTransition()
            val action = HomeFragmentDirections.actionHomeFragmentToAboutUsGraph()
            navigate(action)
        }
    }
}