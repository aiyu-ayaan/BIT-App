package com.atech.bit.ui.fragments.society

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.ui.fragments.society.adapter.SocietyAdapter
import com.atech.bit.ui.fragments.society.adapter.SocietyItem
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.Society
import com.atech.core.utils.DataState
import com.atech.theme.Axis
import com.atech.theme.R
import com.atech.theme.ToolbarData
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.isLoadingDone
import com.atech.theme.launchWhenStarted
import com.atech.theme.set
import com.atech.theme.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SocietyFragment : Fragment(R.layout.layout_recycler_view) {
    private val binding: LayoutRecyclerViewBinding by viewBinding()


    @Inject
    lateinit var cases: ApiCases

    private lateinit var societyAdapter: SocietyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
        observeData()
    }


    private fun LayoutRecyclerViewBinding.setRecyclerView() = this.recyclerView.apply {
        adapter = SocietyAdapter { item ->
            when (item) {
                is SocietyItem.SocietyData -> {
                    navigateToDetail(item.data)
                }

                else -> toast("Not implemented")
            }
        }.also { societyAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() = launchWhenStarted {
        cases.society.invoke().collectLatest { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    val list = mutableListOf<SocietyItem>()
                    list.add(SocietyItem.Title(getString(R.string.societies)))
                    list.addAll(dataState.data.societies.map { SocietyItem.SocietyData(it) })
                    list.add(SocietyItem.Title(getString(R.string.ngos)))
                    list.addAll(dataState.data.ngos.map { SocietyItem.SocietyData(it) })
                    societyAdapter.item = list
                    binding.isLoadingDone(true)
                }

                else -> {
                    if (dataState is DataState.Error)
                        toast(dataState.exception.message.toString())
                    binding.isLoadingDone(false)
                }
            }
        }
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = R.string.societies_and_ngos,
                action = findNavController()::navigateUp
            )
        )
    }

    private fun navigateToDetail(model: Society) {
        exitTransition(Axis.X)
        findNavController().navigate(
            SocietyFragmentDirections.actionSocietyFragmentToSocietyDetailFragment(
                model
            )
        )
    }
}