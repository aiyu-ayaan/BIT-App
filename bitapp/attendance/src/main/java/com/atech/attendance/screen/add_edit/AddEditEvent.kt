package com.atech.attendance.screen.add_edit

sealed class AddEditEvent {
    data class OnSubjectChange(val subject: String) : AddEditEvent()
    data class OnPresentChange(val present: Int) : AddEditEvent()
    data class OnTotalChange(val total: Int) : AddEditEvent()
    data class OnTeacherNameChange(val teacherName: String) : AddEditEvent()
    data class OnSaveClick(val action : ()->Unit = {}) : AddEditEvent()

}