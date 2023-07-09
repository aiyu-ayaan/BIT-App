package com.atech.login.ui.loading

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import com.atech.login.R
import com.atech.login.databinding.FragmentLoadingBinding
import com.atech.theme.Axis
import com.atech.theme.enterTransition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadingFragment : Fragment(R.layout.fragment_loading) {
    private val binding : FragmentLoadingBinding by viewBinding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

        }
    }
}