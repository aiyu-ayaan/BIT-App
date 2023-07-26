package com.atech.bit.ui.fragments.warning

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.BuildConfig
import com.atech.bit.R
import com.atech.bit.databinding.FragmentWarningBinding
import com.atech.core.firebase.remote.RemoteConfigHelper
import com.atech.core.utils.BASE_IN_APP_NAVIGATION_LINK
import com.atech.core.utils.Destination
import com.atech.core.utils.RemoteConfigKeys
import com.atech.theme.Axis
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.customBackPress
import com.atech.theme.navigateWithInAppDeepLink
import com.atech.theme.openPlayStore
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class WarningFragment : BaseFragment(R.layout.fragment_warning, Axis.Z) {
    private val binding: FragmentWarningBinding by viewBinding()
    private val args: WarningFragmentArgs by navArgs()


    @Inject
    lateinit var remoteConfig: RemoteConfigHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textViewWarningContent.text = args.title
            buttonOpenApp.setOnClickListener {
                requireActivity().openPlayStore(args.link)
            }
            buttonQuit.setOnClickListener {
                requireActivity().finish()
            }
            buttonOpenApp.text = args.buttonText
        }
        getWarning()
        customBackPress {
            requireActivity().finish()
        }
    }

    private fun getWarning() {
        remoteConfig.fetchData({}) {
            val isEnable = remoteConfig.getBoolean(RemoteConfigKeys.isEnable.name)
            val minVersion = remoteConfig.getLong(RemoteConfigKeys.minVersion.name).toInt()
            val isMinEdition = BuildConfig.VERSION_CODE > minVersion
            if (!isEnable || isMinEdition) navigateToHome()
        }
    }

    private fun navigateToHome() {
        findNavController().popBackStack()
        navigateWithInAppDeepLink(
            BASE_IN_APP_NAVIGATION_LINK + Destination.Home.value
        )
    }


}