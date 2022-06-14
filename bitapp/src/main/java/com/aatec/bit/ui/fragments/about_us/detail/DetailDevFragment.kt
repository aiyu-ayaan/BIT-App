package com.aatec.bit.ui.fragments.about_us.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentDetailDevBinding
import com.aatec.core.utils.loadImageCircular
import com.aatec.core.utils.openCustomChromeTab
import com.aatec.core.utils.openLinks
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDevFragment : Fragment(R.layout.fragment_detail_dev) {

    private val binding: FragmentDetailDevBinding by viewBinding()
    private val viewModel: DetailDevViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = viewModel.dev?.name

        binding.apply {
            viewModel.dev?.let { dev ->
                dev.img_link?.loadImageCircular(
                    binding.root,
                    photo,
                    binding.progressBarDescription,
                    R.drawable.ic_running_error
                )
                binding.name.text = dev.name
                imageButtonGit.isVisible = dev.github!!.isNotEmpty()
                imageButtonStackOverFlow.isVisible = dev.stackoverflow!!.isNotEmpty()
                imageButtonWeb.isVisible = dev.website!!.isNotEmpty()
                imageButtonInstagram.isVisible = dev.instagram!!.isNotEmpty()
                imageButtonLinkedin.isVisible = dev.linkedin!!.isNotEmpty()

                imageButtonStackOverFlow.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.stackoverflow!!)
                }
                imageButtonWeb.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.website!!)
                }
                imageButtonInstagram.setOnClickListener {
                    dev.instagram?.openLinks(requireActivity(), R.string.no_intent_available)
                }
                imageButtonGit.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.github!!)
                }
                imageButtonLinkedin.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.linkedin!!)
                }
                photo.setOnClickListener {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
                    reenterTransition =
                        MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
                    findNavController().navigate(
                        NavGraphDirections.actionGlobalViewImageFragment(
                            dev.img_link!!,
                            dev.name!!
                        )
                    )
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

}