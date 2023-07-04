package com.atech.bit.ui.fragments.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.atech.bit.R
import com.atech.bit.databinding.FragmentHomeBinding
import com.atech.core.firebase.remote.RemoteConfigHelper
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.SharePrefKeys
import com.atech.theme.Axis
import com.atech.theme.ParentActivity
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.navigate
import com.atech.theme.toast
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()

    private val mainActivity: ParentActivity by lazy {
        requireActivity() as ParentActivity
    }

    @Inject
    lateinit var remoteConfigHelper: RemoteConfigHelper

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setDrawerOpen()
            toolbarIcon()
        }
        handleExpand()
        handleBackPress()
        fetchRemoteConfigData()
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

    //    --------------------------------- Remote Config ----------------------------------
    private fun fetchRemoteConfigData() {
        remoteConfigHelper.fetchData(failure = {
            toast(it.message.toString())
        }) {
            remoteConfigHelper.getString(RemoteConfigKeys.KEY_TOGGLE_SYLLABUS_SOURCE_ARRAY.name)
                .let {
                    pref.edit().putString(SharePrefKeys.KeyToggleSyllabusSource.name, it).apply()
                }
        }
    }

}