package com.atech.bit.ui.fragments.about

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.theme.R
import com.atech.theme.ToolbarData
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.enterTransition
import com.atech.theme.set
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.layout_recycler_view) {
    private val binding: LayoutRecyclerViewBinding by viewBinding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
        }
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(ToolbarData(title = R.string.about_app, action = findNavController()::navigateUp))
    }
}