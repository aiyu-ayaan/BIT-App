package com.atech.bit.ui.fragments.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.firebase.firestore.NoticeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val case: FirebaseCases
) : ViewModel() {
    val allNotices = case.getData
        .invoke(
            NoticeModel::class.java,
            Db.Notice
        ).asLiveData()

    fun getAttach(id: String) = case.getAttach
        .invoke(Db.Notice, id)
        .asLiveData()
}