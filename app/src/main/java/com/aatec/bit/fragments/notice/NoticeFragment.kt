package com.aatec.bit.fragments.notice

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentNoticeBinding
import com.aatec.bit.ui.Fragments.Notice.NoticeMain.Notice3Adapter
import com.aatec.bit.ui.Fragments.Notice.NoticeMain.NoticeViewModel
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.data.ui.notice.Notice3
import com.aatec.core.utils.DataState
import com.aatec.core.utils.showSnackBar
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

        val noticeAdapter = Notice3Adapter(db, { notice, view ->
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
                        dataState.exception.message!!,
                        Snackbar.LENGTH_SHORT
                    )
                }
                DataState.Empty -> binding.empty.visibility = View.VISIBLE
                DataState.Loading -> {
                }
            }
        }
//        detectScroll()
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
//    private fun detectScroll() {
//        binding.showNotice.onScrollChange({
//            viewModel.isColored.value = true
//
//        },
//            {
////                        Status bar
//                viewModel.isColored.value = false
//
//            })
//    }
}