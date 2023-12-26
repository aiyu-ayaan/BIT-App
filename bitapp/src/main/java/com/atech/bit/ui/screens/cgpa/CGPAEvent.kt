package com.atech.bit.ui.screens.cgpa

sealed interface CGPAEvent {
    data class OnSem1Change(val value: CgpaEditModel) : CGPAEvent
    data class OnSem2Change(val value: CgpaEditModel) : CGPAEvent
    data class OnSem3Change(val value: CgpaEditModel) : CGPAEvent
    data class OnSem4Change(val value: CgpaEditModel) : CGPAEvent
    data class OnSem5Change(val value: CgpaEditModel) : CGPAEvent
    data class OnSem6Change(val value: CgpaEditModel) : CGPAEvent
    data object CalculateAndSave : CGPAEvent
}