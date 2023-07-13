package com.atech.bit.ui.fragments.home.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.RowSubjectAdapterRequest
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.course.utils.RowSubjectAdapter
import com.atech.theme.BaseBottomSheet
import com.atech.theme.BottomSheetItem
import com.atech.theme.R
import com.atech.theme.databinding.LayoutBottomSheetBinding
import com.atech.theme.launchWhenCreated
import com.atech.theme.setTopView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class EditBottomSheet : BaseBottomSheet() {

    private val arg: EditBottomSheetArgs by navArgs()

    private lateinit var binding: LayoutBottomSheetBinding
    private lateinit var rowAdapter: RowSubjectAdapter

    @Inject
    lateinit var dao: SyllabusDao

    @Inject
    lateinit var mapper: OfflineSyllabusUIMapper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetBinding.inflate(inflater)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
        observeData()
        return binding.root
    }

    private fun LayoutBottomSheetBinding.setRecyclerView() = this.listAll.apply {
        adapter = RowSubjectAdapter(
            request = RowSubjectAdapterRequest.FROM_HOME,
            onItemClick = ::onItemClick
        ).also { rowAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onItemClick(model: SyllabusUIModel, isChecked: Boolean) = launchWhenCreated {
        dao.updateSyllabus(mapper.mapToEntity(model).copy(isChecked = isChecked))
    }


    private fun observeData() = launchWhenCreated {
        dao.getSyllabusEdit(arg.courseSem).collectLatest {
            rowAdapter.submitList(mapper.mapFromEntityList(it))
        }
    }


    private fun LayoutBottomSheetBinding.setToolbar() = this.apply {
        setTopView(
            BottomSheetItem(
                getString(R.string.edit),
                R.drawable.ic_arrow_downward,
                onIconClick = {
                    dismiss()
                })
        )
    }
}