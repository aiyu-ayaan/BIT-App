package com.atech.core.utils

//const val TAG = "Aiyu"

enum class AppTheme { Light, Dark, Sys }
enum class TAGS {
    BIT_ERROR, BIT_DEBUG, BIT_COROUTINE,
}

enum class RowSubjectAdapterRequest {
    FROM_ATTENDANCE, FROM_HOME
}

enum class SharePrefKeys {
    SharedPreferenceName, ChooseSemLastSelectedSem, KeyToggleSyllabusSource, SyllabusVisibility,
    UserHasDataInCloud, RestoreDone, PermanentSkipLogin, SetUpDone, AppTheme
}

const val SYLLABUS_SOURCE_DATA =
    "{   \"bca1\": true,   \"bca2\": false,   \"bca3\": true,   \"bca4\": false,   \"bca5\": false,   \"bca6\": false,   \"bba1\": false,   \"bba2\": false,   \"bba3\": false,   \"bba4\": false,   \"bba5\": false,   \"bba6\": false ,\"mca1\": true, \"mca2\": false, \"mca3\": false, \"mba4\": false, \"mba1\": false, \"mba2\": false, \"mba3\": false}"

const val BASE_IN_APP_NAVIGATION_LINK = "bitapp://bit.aiyu/"

enum class Destination(val value: String) {
    ChooseSem("choosesem"),
    Home("home"),
    LogIn("login"),
    Profile("profile")
}

val DEFAULT_PAIR = Pair("Book Reminder", "Reminder for book")

const val SYLLABUS_DETAILS_DEEP_LINK = "android-app://com.atech.bit/view_syllabus_fragment"

const val MAX_SPAWN = 3

const val MAX_TIMES: Int = 10

enum class CourseCapital { BBA, BCA }

const val DEFAULT_QUERY = "no_query_only_aiyu"
const val DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG"
const val UPDATE_REQUEST = "Update"
const val REQUEST_EDIT_SUBJECT_FROM_LIST_ALL = 674 * 453
const val REQUEST_ADD_SUBJECT_FROM_SYLLABUS = 345 * 453
const val REQUEST_MENU_FROM_ARCHIVE = 345 * 453 + 1
const val REQUEST_EDIT_SUBJECT_FROM_SYLLABUS = 347 * 453

const val DEFAULT_DATE_FORMAT = "dd-MM-yyyy"
const val DEFAULT_TIME_FORMAT = "hh:mm a"
const val DD_MM = "dd MMM"
const val EDIT_TEXT_DATE_FORMAT = "MMM dd, yyyy"
const val ERROR_IN_UPDATE = 452 * 53

const val MAX_STACK_SIZE = 30
const val UPDATE_REQUEST_CODE = 444

enum class RemoteConfigKeys {
    SYLLABUS_VISIBILITY, KEY_TOGGLE_SYLLABUS_SOURCE_ARRAY,
    SYLLABUS_BCA, SYLLABUS_BBA, KEY_TOGGLE_SYLLABUS_SOURCE,
    title, minVersion, link, isEnable, button_text, show_times,
    Github_Link, Current_Year, ann_version, ann_pos_button, ann_neg_button,
    ann_message, ann_link, ann_title, MAX_TIMES_UPLOAD
}