package com.atech.bit.ui.fragments.notice.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.firebase.firestore.NoticeModel
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NoticeDetailViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val case: FirebaseCases
) : ViewModel() {
    val path: String = state["path"] ?: ""

    fun getNotice() = try {
        case.getDocumentDetails.invoke(
            NoticeModel::class.java,
            Db.Notice,
            path
        ).map {
            if (it == null) DataState.Empty
            else DataState.Success(it)
        }
    } catch (e: Exception) {
        MutableStateFlow(DataState.Error(e))
    }.asLiveData()

    fun getAttach() = try {
        case.getAttach.invoke(
            Db.Notice, path
        )
    } catch (e: Exception) {
        emptyFlow()
    }.asLiveData()
}