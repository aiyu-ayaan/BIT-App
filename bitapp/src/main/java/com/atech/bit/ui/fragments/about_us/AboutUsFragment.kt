package com.atech.bit.ui.fragments.about_us

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentAboutUsBinding
import com.atech.bit.ui.custom_views.DividerItemDecorationNoLast
import com.atech.bit.utils.getVersion
import com.atech.bit.utils.launchWhenStarted
import com.atech.core.api.aboutus.Devs
import com.atech.core.utils.DataState
import com.atech.core.utils.TAG
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.openPlayStore
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    private val binding: FragmentAboutUsBinding by viewBinding()
    private val viewModel: AboutUsViewModel by viewModels()
    private lateinit var devAdapter: DevsAdapter
    private lateinit var contributorAdapter: DevsAdapter
    private lateinit var managersAdapter: DevsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        restoreScroll()

        devAdapter = DevsAdapter { devs ->
            setOnAboutUsClickListener(devs)
        }
        contributorAdapter = DevsAdapter { devs ->
            setOnAboutUsClickListener(devs)
        }
        managersAdapter = DevsAdapter { devs ->
            setOnAboutUsClickListener(devs)
        }

        binding.apply {
            textView4.text =  getVersion()
            showDevs.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
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
            showManagers.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = managersAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }


        binding.showDevs.isNestedScrollingEnabled = false
        detectScroll()
        getData()
    }

    private fun getData() {
        viewModel.aboutUsData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    devAdapter.submitList(it.data.devs.shortListOnSno())
                    managersAdapter.submitList(it.data.managers.shortListOnSno())
                    contributorAdapter.submitList(it.data.contributors)
                    binding.apply {
                        textViewManagers.isVisible = it.data.managers.isNotEmpty()
                        materialCardViewManagers.isVisible = it.data.managers.isNotEmpty()
                        textViewContributors.isVisible = it.data.contributors?.isNotEmpty() == true
                        materialCardViewContributors.isVisible =
                            it.data.contributors?.isNotEmpty() == true
                    }
                }

                is DataState.Error -> {
                    Log.d("XXX", "getData: ${it.exception.message}")
                }

                DataState.Empty -> {}
                DataState.Loading -> {

                }
            }
        }
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
            launchWhenStarted {
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


    override fun onStop() {
        try {
            super.onStop()
            viewModel.aboutNestedViewPosition.value = binding.nestedViewAboutUs.scrollY
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    private fun List<Devs>?.shortListOnSno() = this?.sortedBy { it.sno }

}