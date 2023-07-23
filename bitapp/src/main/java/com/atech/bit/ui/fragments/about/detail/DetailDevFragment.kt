package com.atech.bit.ui.fragments.about.detail

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentDetailDevBinding
import com.atech.core.retrofit.client.Devs
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.loadCircular
import com.atech.theme.openCustomChromeTab
import com.atech.theme.openLinks
import com.atech.theme.set
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDevFragment : BaseFragment(R.layout.fragment_detail_dev,Axis.X) {

    private val binding: FragmentDetailDevBinding by viewBinding()
    private val args: DetailDevFragmentArgs by navArgs()

    private val dev by lazy {
        args.dev
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setProfileImage(dev)
            setNamesAndRoles(dev)
            setButtons(dev)
        }
    }

    private fun FragmentDetailDevBinding.setNamesAndRoles(dev: Devs) {
        textViewName.text = dev.name
        binding.textViewRole.text = dev.des
    }

    private fun FragmentDetailDevBinding.setButtons(dev: Devs) {
        buttonWeb.isVisible = dev.website.isNotBlank()
        divWebsite.isVisible = dev.website.isNotBlank()

        buttonStackOverFlow.isVisible = dev.stackoverflow.isNotBlank()
        divStack.isVisible = dev.stackoverflow.isNotBlank()

        buttonGit.isVisible = dev.github.isNotBlank()
        divGit.isVisible = dev.github.isNotBlank()

        buttonLinkedin.isVisible = dev.linkedin.isNotBlank()
        divLink.isVisible = dev.linkedin.isNotBlank()


        buttonInstagram.isVisible = dev.instagram.isNotBlank()



        buttonStackOverFlow.setOnClickListener {
            requireActivity().openCustomChromeTab(dev.stackoverflow)
        }
        buttonWeb.setOnClickListener {
            requireActivity().openCustomChromeTab(dev.website)
        }
        buttonInstagram.setOnClickListener {
            dev.instagram.openLinks(
                requireActivity(),
                com.atech.theme.R.string.no_intent_available
            )
        }
        buttonGit.setOnClickListener {
            requireActivity().openCustomChromeTab(dev.github)
        }
        buttonLinkedin.setOnClickListener {
            requireActivity().openCustomChromeTab(dev.linkedin)
        }
    }

    private fun FragmentDetailDevBinding.setProfileImage(dev: Devs) {
        imageViewProfile.loadCircular(
            dev.imageLink
        )
        imageViewProfile.setOnClickListener {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            reenterTransition =
                MaterialSharedAxis(MaterialSharedAxis.Z, false)
            findNavController().navigate(
                NavGraphDirections.actionGlobalViewImageFragment(
                    dev.imageLink,
                    dev.name
                )
            )
        }
    }

    private fun FragmentDetailDevBinding.setToolbar() = this.includeToolbar.apply {
        set(ToolbarData(titleString = dev.name, action = findNavController()::navigateUp))
    }


}