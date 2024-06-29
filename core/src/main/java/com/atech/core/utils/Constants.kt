/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

//const val TAG = "Aiyu"


const val cacheSize = (40 * 1024 * 1024).toLong()

enum class TAGS {
    BIT_ERROR, BIT_DEBUG, BIT_COROUTINE, BIT_REMOTE,
    BIT_TIME
}


enum class RemoteConfigKeys(val value: String) {
    CourseDetails("course_details"),
    KeyToggleSyllabusSource("KEY_TOGGLE_SYLLABUS_SOURCE_ARRAY"),
    AppAlertDialog("app_alert_dialog"),
    ForceScreen("force_screen"),
    MAX_TIMES_UPLOAD("MAX_TIMES_UPLOAD"),
    MAX_CHAT_LIMIT("max_chat_limit"),
    CURRENT_YEAR("Current_Year"),
    SHOW_SOCIETY_OR_EVENT("show_society_or_event")
}

enum class SharePrefKeys {
    BITAppPref, ChooseSemLastSelectedSem, KeyToggleSyllabusSource,
    PermanentSkipLogin, SetUpDone, AppThemeState, KeyAppOpenMinimumTime,
    ShowTimes, CourseDetails, SHOW_CALENDER_PERMISSION_FOR_FIRST_TIME,
    AppAlertDialog, IsLibraryCardVisibleAttendanceScreen, ChatScreenSetting,
    UploadTime, AppNotificationSettings, IsChatScreenEnable,
}

const val SYLLABUS_SOURCE_DATA =
    "{   \"bca1\": true,   \"bca2\": false,   \"bca3\": true,   \"bca4\": false,   \"bca5\": false,   \"bca6\": false,   \"bba1\": false,   \"bba2\": false,   \"bba3\": false,   \"bba4\": false,   \"bba5\": false,   \"bba6\": false ,\"mca1\": true, \"mca2\": false, \"mca3\": false, \"mba4\": false, \"mba1\": false, \"mba2\": false, \"mba3\": false}"

const val BASE_IN_APP_NAVIGATION_LINK = "bitapp://bit.aiyu/"

const val COURSE_DETAILS = "{\n" +
        "  \"course\": [\n" +
        "    {\n" +
        "      \"name\": \"bca\",\n" +
        "      \"sem\": 6\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"bba\",\n" +
        "      \"sem\": 6\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"mca\",\n" +
        "      \"sem\": 4\n" +
        "    },\n" +
        "    {\n" +
        "      \"name\": \"mba\",\n" +
        "      \"sem\": 4\n" +
        "    }\n" +
        "  ]\n" +
        "}"

enum class Destination(val value: String) {
    ChooseSem("choosesem"), Home("home"),
    LogIn("login"), Profile("profile"),
    AboutUS("aboutUs")
}

val DEFAULT_PAIR = Pair("Book Reminder", "Reminder for book")

const val SYLLABUS_DETAILS_DEEP_LINK = "android-app://com.atech.bit/view_syllabus_fragment"

const val MAX_SPAWN = 3

const val MAX_TIMES: Int = 10

enum class CourseCapital { BBA, BCA }

const val MAX_APP_OPEN_TIME = 5

const val DEFAULT_DATE_FORMAT = "dd-MM-yyyy"
const val DEFAULT_TIME_FORMAT = "hh:mm a"
const val DD_MM = "dd MMM"
const val EDIT_TEXT_DATE_FORMAT = "MMM dd, yyyy"
const val ERROR_IN_UPDATE = 452 * 53

const val MAX_STACK_SIZE = 30

const val CHANNEL_ID_NOTICE = "Notice"
const val CHANNEL_NOTICE = "Notice Section"
const val CHANNEL_DES = "Contain all the notice from BIT Lalpur"

const val CHANNEL_ID_EVENT = "Event"
const val CHANNEL_EVENT = "Event Section"

const val CHANNEL_ID_UPDATE = "Update"
const val CHANNEL_UPDATE = "App Update"

const val CHANNEL_ID_APP = "App"
const val CHANNEL_APP = "App Notification"


const val DEFAULT_PAGE_SIZE = 20
const val INITIAL_LOAD_SIZE = 20
const val NOTICE_SITE_LINK = "https://www.bitmesra.ac.in/Display_Archive_News_List09398FGDr?cid=7"

enum class MessageTopic(val value: String) {
    Notice("NoticeByAiyu"),
    Event("EventByAiyu"),
    App("AppByAiyu"),
    Update("UpdateByAiyu")
}

enum class MessageTopicTest(val value: String) {
    Notice("NoticeByAiyuTest"),
    Event("EventByAiyuTest"),
    App("AppByAiyuTest"),
    Update("UpdateByAiyuTest")
}