package com.atech.login.ui.setup

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.login.R
import com.atech.login.databinding.FragmentSetupBinding
import com.atech.theme.Axis
import com.atech.theme.enterTransition
import com.atech.theme.navigate
import com.atech.theme.navigateWithInAppDeepLink
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    private val binding: FragmentSetupBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fbNext.setOnClickListener {
                navigateToChooseSem()
            }
        }
    }

    private fun navigateToChooseSem() {
        navigateWithInAppDeepLink(BASE_IN_APP_NAVIGATION_LINK + Destination.ChooseSem.value)
    }
}