package com.aatec.bit.ui.fragments.about_us

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.R
import com.aatec.bit.ui.custom_views.DividerItemDecorationNoLast
import com.aatec.bit.databinding.FragmentAboutUsBinding
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.utils.changeStatusBarToolbarColor
import com.aatec.core.utils.openPlayStore
import com.aatec.core.data.network.aboutus.Devs
import com.aatec.core.utils.DataState
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    private val binding: FragmentAboutUsBinding by viewBinding()
    private val viewModel: AboutUsViewModel by viewModels()

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
        val devAdapter = DevsAdapter { devs ->
            setOnAboutUsClickListener(devs)
        }
        val contributorAdapter = DevsAdapter { devs ->
            setOnAboutUsClickListener(devs)
        }
        binding.apply {
            showDevs.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = devAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            textViewComponent.setOnClickListener {
                navigateToFragmentComponentUse()
            }
            textViewPlayStore.setOnClickListener {
                requireActivity().openPlayStore(requireActivity().packageName)
            }
            showContributors.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = contributorAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        viewModel.setStateListener(MainStateEvent.GetData)
        lifecycleScope.launchWhenStarted {
            viewModel.dataState.combine(viewModel.dataStateContributors) { dev, con ->
                CombineFlow(dev, con)
            }.collect { combineFlow ->
                when (combineFlow.dev) {
                    is DataState.Success -> {
                        devAdapter.submitList(combineFlow.dev.data)
                    }
                    DataState.Empty -> {
                        Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
                    }
                    is DataState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "${combineFlow.dev.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    DataState.Loading -> {

                    }
                }
                when (combineFlow.con) {
                    is DataState.Success -> {
                        binding.showContributors.isVisible = true
                        binding.textViewContributors.isVisible = true
                        contributorAdapter.submitList(combineFlow.con.data)
                    }
                    DataState.Empty -> {
                        binding.showContributors.isVisible = false
                        binding.textViewContributors.isVisible = false
                        Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
                    }
                    is DataState.Error -> {
                        binding.showContributors.isVisible = false
                        binding.textViewContributors.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "${combineFlow.con.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    DataState.Loading -> {

                    }
                }
            }
        }
        binding.showDevs.isNestedScrollingEnabled = false
        detectScroll()
        setHasOptionsMenu(true)
    }

    private fun detectScroll() {
        binding.nestedViewAboutUs.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            when (scrollY) {
                0 -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        com.google.android.material.R.attr.colorSurface
                    )
                }
                else -> {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar,
                        R.attr.bottomBar
                    )
                }
            }
        }
    }

    private fun navigateToFragmentComponentUse() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        val action = AboutUsFragmentDirections.actionAboutUsFragmentToAcknowledgementFragment()
        findNavController().navigate(action)
    }


    private fun restoreScroll() {
        try {
            lifecycleScope.launchWhenStarted {
                viewModel.aboutNestedViewPosition.collect { position ->
                    binding.nestedViewAboutUs.post {
                        binding.nestedViewAboutUs.scrollTo(0, position)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    private fun setOnAboutUsClickListener(devs: Devs) {
        try {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            val action = AboutUsFragmentDirections.actionAboutUsFragmentToDetailDevFragment(devs)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onStop() {
        try {
            super.onStop()
            viewModel.aboutNestedViewPosition.value = binding.nestedViewAboutUs.scrollY
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }
}

data class CombineFlow(
    val dev: DataState<List<Devs>>,
    val con: DataState<List<Devs>>
)