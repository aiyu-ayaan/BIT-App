package com.atech.bit.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHomeBinding
import com.atech.bit.ui.fragments.home.adapter.HomeAdapter
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.TAGS
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.utils.onScrollChange
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
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

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel: HomeViewModel by viewModels()

    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }


    private lateinit var homeAdapter: HomeAdapter

    @Inject
    lateinit var authUseCases: AuthUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setProfile()
            setDrawerOpen()
            toolbarIcon()
            setRecyclerView()
        }
        handleExpand()
        handleBackPress()
        hideBottomAppBar()
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
            }
        }
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
            onSettingClick = ::navigateToSemChoose
        ).also { homeAdapter = it }
        layoutManager = LinearLayoutManager(context)
        observeData()
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
    }


    private fun switchClick(isChecked: Boolean) {
        viewModel.isOnline.value = isChecked
    }

    private fun observeData() = launchWhenCreated {
        viewModel.homeScreenData.collectLatest {
            homeAdapter.items = it.toMutableList()
            homeAdapter.defPercentage = viewModel.defPercentage
        }
    }


}