package com.atech.bit.ui.fragments.startup

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSetUpBinding
import com.atech.bit.utils.getVersion
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartUpFragment : Fragment(R.layout.fragment_set_up) {

    private val binding: FragmentSetUpBinding by viewBinding()

    @Inject
    lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textView3.text = resources.getString(
                R.string.full_version,
                getVersion()
            )
            fbNext.setOnClickListener {
                val action = NavGraphDirections.actionGlobalChooseSemBottomSheet()
                findNavController().navigate(action)
            }
        }
    }

}