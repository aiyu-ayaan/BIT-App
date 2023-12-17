package com.atech.bit.ui.screens.library

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.room.library.LibraryModel
import com.atech.core.usecase.LibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LibraryManagerViewModel @Inject constructor(
    private val useCase: LibraryUseCase
) : ViewModel() {
    private val _libraryList = mutableStateOf<List<LibraryModel>>(emptyList())
    val libraryList: State<List<LibraryModel>> get() = _libraryList

    private var job: Job? = null

    //     Add Edit Screen
    private val _currentClickLibraryModel = mutableStateOf<LibraryModel?>(null)

    private val _bookName = mutableStateOf(_currentClickLibraryModel.value?.bookName ?: "")
    val bookName: State<String> get() = _bookName

    private val _bookId = mutableStateOf(_currentClickLibraryModel.value?.bookId ?: "")
    val bookId: State<String> get() = _bookId

    private val _issueDate = mutableStateOf(_currentClickLibraryModel.value?.issueDate ?: -1L)
    val issueDate: State<Long> get() = _issueDate

    private val _returnDate = mutableStateOf(_currentClickLibraryModel.value?.returnDate ?: -1L)
    val returnDate: State<Long> get() = _returnDate


    init {
        getAllData()
    }

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.NavigateToAddEditScreen -> _currentClickLibraryModel.value = event.model
//            Add Edit Screen
            is LibraryEvent.OnBookIdChange -> _bookId.value = event.value
            is LibraryEvent.OnBookNameChange -> _bookName.value = event.value
            is LibraryEvent.OnIssueDateChange -> _issueDate.value = event.value
            is LibraryEvent.OnReturnDateChange -> _returnDate.value = event.value
            is LibraryEvent.PickDateClick -> {
                when (event.pickFor) {
                    PickFor.ISSUE_DATE -> {
                        _issueDate.value = event.date
                    }

                    PickFor.RETURN_DATE -> {
                        _returnDate.value = event.date
                    }
                }
            }

            LibraryEvent.ResetValue -> {
                _bookId.value = ""
                _bookName.value = ""
                _issueDate.value = -1L
                _returnDate.value = -1L
            }
        }
    }

    private fun getAllData() {
        job?.cancel()
        job = useCase.getAll.invoke()
            .onEach { items ->
                _libraryList.value = items
            }.launchIn(viewModelScope)
    }
}