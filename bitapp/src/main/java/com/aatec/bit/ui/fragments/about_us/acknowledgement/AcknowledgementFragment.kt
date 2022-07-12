package com.aatec.bit.ui.fragments.about_us.acknowledgement

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentAcknowledgementBinding
import com.aatec.bit.ui.custom_views.DividerItemDecorationNoLast
import com.aatec.bit.utils.addMenuHost
import com.aatec.core.data.ui.componentUse.ComponentList
import com.aatec.core.utils.changeStatusBarToolbarColor
import com.aatec.core.utils.onScrollColorChange
import com.aatec.core.utils.openCustomChromeTab
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AcknowledgementFragment : Fragment(R.layout.fragment_acknowledgement) {

    private val binding: FragmentAcknowledgementBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val componentUseAdapter = AcknowledgementAdapter {
            handleClick(it)
        }
        binding.apply {
            showContent.apply {
                addItemDecoration(
                    DividerItemDecorationNoLast(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    ).apply {
                        setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
                    }
                )
                adapter = componentUseAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        componentUseAdapter.submitList(ComponentList.list)
        detectScroll()
        addMenuHost()
    }

     

    private fun handleClick(it: String) {
        requireContext().openCustomChromeTab(it)
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        activity?.onScrollColorChange(binding.nestedViewAcknowledgement, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                com.google.android.material.R.attr.colorSurface
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                R.attr.bottomBar
            )
        })

    }

    override fun onPause() {
        super.onPause()
        activity?.changeStatusBarToolbarColor(
            R.id.toolbar,
            android.viewbinding.library.R.attr.colorSurface
        )
    }
}