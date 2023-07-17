package com.atech.bit.ui.fragments.about

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.ui.fragments.about.adapter.AboutUsAdapter
import com.atech.bit.ui.fragments.about.adapter.AboutUsItem
import com.atech.bit.utils.getVersion
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.AboutUsModel
import com.atech.core.retrofit.client.Devs
import com.atech.core.utils.DataState
import com.atech.core.utils.TAGS
import com.atech.theme.Axis
import com.atech.theme.R
import com.atech.theme.ToolbarData
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.launchWhenCreated
import com.atech.theme.navigate
import com.atech.theme.openCustomChromeTab
import com.atech.theme.openPlayStore
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.layout_recycler_view) {
    private val binding: LayoutRecyclerViewBinding by viewBinding()

    @Inject
    lateinit var apiCases: ApiCases

    private lateinit var aboutUsAdapter: AboutUsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
    }

    private fun LayoutRecyclerViewBinding.setRecyclerView() = this.recyclerView.apply {
        setHasFixedSize(true)
        adapter = AboutUsAdapter(
            onDevClick = ::navigateToDevDetails,
            onPlayStoreClick = ::openPlayStore,
            onPrivacyClick = ::openPrivacyPolicy
        ).also { aboutUsAdapter = it }
        aboutUsAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        layoutManager = LinearLayoutManager(requireContext())
        observeData()
    }

    private fun observeData() = launchWhenCreated {
        val list = mutableListOf<AboutUsItem>()
        list.add(AboutUsItem.Title(getString(R.string.about)))
        list.add(AboutUsItem.AppVersion(getVersion()))
        val dev = try {
            getDevs().await()
        } catch (e: Exception) {
            Log.d(TAGS.BIT_ERROR.name, "observeData: $e")
            toast(e.message ?: getString(R.string.error))
            emptyList<AboutUsItem.Dev>()
        }
        list.addAll(dev)
        list.add(AboutUsItem.BottomView)
        aboutUsAdapter.list = list
    }

    private fun getDevs() = lifecycleScope.async(Dispatchers.IO) {
        apiCases.aboutUs.invoke().map { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    throw dataState.exception
                }

                is DataState.Success -> {
                    dataState.data.toAboutUsItem()
                }

                else -> {
                    emptyList()
                }
            }
        }.toList().flatten()
    }

    private fun AboutUsModel.toAboutUsItem(): List<AboutUsItem> {
        val (dev, manager, contributors) = this
        val list = mutableListOf<AboutUsItem>()
        list.add(AboutUsItem.Title(getString(R.string.developers)))
        list.addAll(dev.shortListOnSno().map { AboutUsItem.Dev(it) })
        contributors?.let { contributors1 ->
            if (contributors1.isNotEmpty()) {
                list.add(AboutUsItem.Title(getString(R.string.contributors)))
                contributors1.filterNotNull().shortListOnSno().map { AboutUsItem.Dev(it) }
                    .let { list.addAll(it) }
            }
        }
        manager.let { manager1 ->
            if (manager1.isNotEmpty()) {
                list.add(AboutUsItem.Title(getString(R.string.managers)))
                manager1.shortListOnSno().map { AboutUsItem.Dev(it) }.let { list.addAll(it) }
            }
        }
        return list
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(ToolbarData(title = R.string.about_app, action = findNavController()::navigateUp))
    }

    private fun navigateToDevDetails(dev: Devs) {
        exitTransition(Axis.X)
        val action = AboutFragmentDirections.actionAboutFragmentToDetailDevFragment(dev)
        navigate(action)
    }

    private fun openPlayStore() {
        requireActivity().openPlayStore(requireActivity().packageName)
    }

    private fun openPrivacyPolicy(link: String) {
        requireActivity().openCustomChromeTab(
            link
        )
    }


    private fun List<Devs>.shortListOnSno() = this.sortedBy { it.sno }
}