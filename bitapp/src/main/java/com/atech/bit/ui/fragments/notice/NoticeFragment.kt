package com.atech.bit.ui.fragments.notice

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentNoticeBinding
import com.atech.bit.utils.MainStateEvent
import com.atech.bit.utils.loadAdds
import com.atech.core.data.ui.notice.Notice3
import com.atech.core.utils.DataState
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.onScrollChange
import com.atech.core.utils.showSnackBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoticeFragment : Fragment(R.layout.fragment_notice) {

    private val viewModel: NoticeViewModel by viewModels()
    private val binding: FragmentNoticeBinding by viewBinding()

    @Inject
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        restoreColor()
        val noticeAdapter = NoticeAdapter(db, { notice, view ->
            navigationToNotice3Description(notice, view)
        }, { it ->
            navigateToImageView(it)
        })
        binding.apply {
            showNotice.apply {
                adapter = noticeAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        noticeAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        viewModel.setStateListenerMain(MainStateEvent.GetData)

        viewModel.dataStateNotice3Main.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.empty.visibility = View.GONE
                    noticeAdapter.submitList(dataState.data)
                }
                is DataState.Error -> {
                    binding.empty.visibility = View.VISIBLE
                    binding.root.showSnackBar(
                        dataState.exception.message!!, Snackbar.LENGTH_SHORT
                    )
                }
                DataState.Empty -> binding.empty.visibility = View.VISIBLE
                DataState.Loading -> {
                }
            }
        }
        detectScroll()
        requireContext().loadAdds(binding.adView)
    }

    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
    }

    private fun navigationToNotice3Description(notice: Notice3, view: View) {
        val extras = FragmentNavigatorExtras(view to notice.path)
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        val action = NavGraphDirections.actionGlobalNoticeDetailFragment(notice.path)
        findNavController().navigate(action, extras)
    }


    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        binding.showNotice.onScrollChange({
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar, com.google.android.material.R.attr.colorSurface
            )
            viewModel.isColored.value = false
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar, R.attr.bottomBar
            )
            viewModel.isColored.value = true
        })
    }

    private fun restoreColor() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isColored.collect {
                if (it) {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar, R.attr.bottomBar
                    )
                } else {
                    activity?.changeStatusBarToolbarColor(
                        R.id.toolbar, com.google.android.material.R.attr.colorSurface
                    )
                }
            }
        }
    }
}