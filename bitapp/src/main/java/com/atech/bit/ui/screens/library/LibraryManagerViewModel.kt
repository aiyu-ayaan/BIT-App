package com.atech.bit.ui.screens.library

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.room.library.LibraryModel
import com.atech.core.usecase.LibraryUseCase
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.compareDifferenceInDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LibraryManagerViewModel @Inject constructor(
    private val useCase: LibraryUseCase, private val pref: SharedPreferences
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

    private val _issueDate = mutableLongStateOf(_currentClickLibraryModel.value?.issueDate ?: -1L)
    val issueDate: State<Long> get() = _issueDate

    private val _returnDate = mutableLongStateOf(_currentClickLibraryModel.value?.returnDate ?: -1L)
    val returnDate: State<Long> get() = _returnDate

    private val _harError = mutableStateOf(false to "")
    val harError: State<Pair<Boolean, String>> get() = _harError

    private val _hasSubjectError = mutableStateOf(false)
    val hasSubjectError: State<Boolean> get() = _hasSubjectError

    private val _hasIssueDateError = mutableStateOf(false)
    val hasIssueDateError: State<Boolean> get() = _hasIssueDateError

    private val _hasErrorInRemainder = mutableStateOf(false to "")
    val hasErrorInRemainder: State<Pair<Boolean, String>> get() = _hasErrorInRemainder

    var showPermissionForFirstTime: Boolean
        get() = pref.getBoolean(SharePrefKeys.SHOW_CALENDER_PERMISSION_FOR_FIRST_TIME.name, true)
        set(value) = pref.edit()
            .putBoolean(SharePrefKeys.SHOW_CALENDER_PERMISSION_FOR_FIRST_TIME.name, value).apply()


    init {
        getAllData()
    }

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.NavigateToAddEditScreen -> {
                _currentClickLibraryModel.value = event.model
                if (event.model != null) {
                    _bookId.value = event.model.bookId
                    _bookName.value = event.model.bookName
                    _issueDate.longValue = event.model.issueDate
                    _returnDate.longValue = event.model.returnDate
                }
            }
//            Add Edit Screen
            is LibraryEvent.OnBookIdChange -> _bookId.value = event.value
            is LibraryEvent.OnBookNameChange -> _bookName.value = event.value
            is LibraryEvent.OnIssueDateChange -> {
                _issueDate.longValue = event.value
                checkError()
            }

            is LibraryEvent.OnReturnDateChange -> {
                _returnDate.longValue = event.value
                checkError()
            }

            is LibraryEvent.PickDateClick -> {
                when (event.pickFor) {
                    PickFor.ISSUE_DATE -> onEvent(
                        LibraryEvent.OnIssueDateChange(
                            event.date
                        )
                    )


                    PickFor.RETURN_DATE -> onEvent(
                        LibraryEvent.OnReturnDateChange(
                            event.date
                        )
                    )

                    PickFor.REMINDER_DATE -> TODO()
                }
            }

            LibraryEvent.ResetValue -> {
                _bookId.value = ""
                _bookName.value = ""
                _issueDate.longValue = -1L
                _returnDate.longValue = -1L
                _harError.value = false to ""
            }

            is LibraryEvent.HasError -> {
                _harError.value = event.hasError to event.message
            }

            is LibraryEvent.SaveBook -> {
                if (_bookName.value.isBlank()) {
                    _hasSubjectError.value = true
                    return
                }
                _hasSubjectError.value = false
                if (_issueDate.longValue == -1L) {
                    _hasIssueDateError.value = true
                    return
                }
                _hasIssueDateError.value = false

                if (_returnDate.longValue == -1L) {
                    onEvent(
                        LibraryEvent.HasError(
                            true, "Return date can't be empty"
                        )
                    )
                    return
                }
                onEvent(LibraryEvent.HasError())

                val model = LibraryModel(
                    bookName = _bookName.value,
                    issueDate = _issueDate.longValue,
                    bookId = _bookId.value,
                    returnDate = _returnDate.longValue,
                    markAsReturn = _currentClickLibraryModel.value?.markAsReturn ?: false,
                    alertDate = _currentClickLibraryModel.value?.alertDate ?: -1L,
                    id = _currentClickLibraryModel.value?.id ?: 0
                )
                viewModelScope.launch {
                    useCase.insertBook(model)
                }
                event.action.invoke()
            }

            is LibraryEvent.HasErrorInReminder -> _hasErrorInRemainder.value =
                event.hasError to event.message
        }
    }

    private fun checkError() {
        if (_returnDate.longValue != -1L && _issueDate.longValue != -1L) {
            if (Date(_returnDate.longValue).compareDifferenceInDays(Date(_issueDate.longValue)) < 0) {
                onEvent(LibraryEvent.HasError(true, "Return date can't be before issue date"))
            } else onEvent(LibraryEvent.HasError())
        }
    }

    private fun getAllData() {
        job?.cancel()
        job = useCase.getAll.invoke().onEach { items ->
            _libraryList.value = items
        }.launchIn(viewModelScope)
    }
}