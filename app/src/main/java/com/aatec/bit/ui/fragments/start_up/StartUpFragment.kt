package com.aatec.bit.ui.fragments.start_up

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentSetUpBinding
import com.aatec.core.utils.KEY_FIRST_TIME_TOGGLE
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
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val u: Boolean = pref.getBoolean(KEY_FIRST_TIME_TOGGLE, false)

        if (u) {
            val action = StartUpFragmentDirections.actionStartUpFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        binding.apply {
            fbNext.setOnClickListener {
                val action = NavGraphDirections.actionGlobalChooseSemBottomSheet()
                findNavController().navigate(action)
            }
        }
    }

}