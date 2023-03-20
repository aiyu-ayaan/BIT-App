package com.atech.bit.ui.fragments.global_search.model

import com.atech.core.api.holiday.Holiday
import com.atech.core.api.holiday.HolidayModel
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.data.ui.events.Events
import com.atech.core.data.ui.notice.Notice3

sealed class SearchItem {

    class Title(val title: String) : SearchItem()

    class Syllabus(val data: SyllabusModel) : SearchItem()

    class Holiday(val data: com.atech.core.api.holiday.Holiday) : SearchItem()


    class Notice(val data : Notice3) : SearchItem()


    class Event(val data : Events) : SearchItem()

}