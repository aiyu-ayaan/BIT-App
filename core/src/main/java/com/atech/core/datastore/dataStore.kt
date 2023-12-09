package com.atech.core.datastore

//import android.content.Context
//import android.os.Parcelable
//import android.util.Log
//import androidx.annotation.Keep
//import androidx.datastore.preferences.core.doublePreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.emptyPreferences
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import com.atech.core.room.attendance.Sort
//import com.atech.core.room.attendance.SortBy
//import com.atech.core.room.attendance.SortOrder
//import dagger.hilt.android.qualifiers.ApplicationContext
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.map
//import kotlinx.parcelize.IgnoredOnParcel
//import kotlinx.parcelize.Parcelize
//import java.io.IOException
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//private const val TAG = "PreferencesManager"
//
//@Keep
//@Parcelize
//data class Cgpa(
//    var sem1: Double = 0.0,
//    var sem2: Double = 0.0,
//    var sem3: Double = 0.0,
//    var sem4: Double = 0.0,
//    var sem5: Double = 0.0,
//    var sem6: Double = 0.0,
//    val cgpa: Double = 1.0,
//    val earnCrSem1: Double? = 0.0,
//    val earnCrSem2: Double? = 0.0,
//    val earnCrSem3: Double? = 0.0,
//    val earnCrSem4: Double? = 0.0,
//    val earnCrSem5: Double? = 0.0,
//    val earnCrSem6: Double? = 0.0,
//) : Parcelable {
//    @IgnoredOnParcel
//    val isAllZero =
//        sem1 == 0.0 && sem2 == 0.0 && sem3 == 0.0 && sem4 == 0.0 && sem5 == 0.0 && sem6 == 0.0
//
//    @IgnoredOnParcel
//    val totalCr = earnCrSem1 + earnCrSem2 + earnCrSem3 + earnCrSem4 + earnCrSem5 + earnCrSem6
//
//    @IgnoredOnParcel
//    val isNewCgpa =
//        earnCrSem1 != 0.0 && earnCrSem2 != 0.0 && earnCrSem3 != 0.0 && earnCrSem4 != 0.0 && earnCrSem5 != 0.0 && earnCrSem6 != 0.0
//}
//
//private operator fun Double?.plus(earnCrSem1: Double?): Double {
//    return this?.plus(earnCrSem1 ?: 0.0) ?: 0.0
//}
//
//@Keep
//@Parcelize
//data class FilterPreferences(
//    val defPercentage: Int,
//    val course: String,
//    val sem: String,
//    val cgpa: Cgpa,
//    val sort: Sort
//) : Parcelable {
//    @IgnoredOnParcel
//    val courseWithSem = "$course$sem"
//}
//
//
//@Singleton
//class PreferencesManager @Inject constructor(@ApplicationContext val context: Context) {
//    private val Context.dataStore by preferencesDataStore(name = "user_preferences")
//
//    val attendanceSortFLow = context.dataStore.data
//        .catch { exception ->
//            when (exception) {
//                is IOException -> {
//                    emit(emptyPreferences())
//                    Log.e(TAG, "$exception")
//                }
//
//                else -> throw exception
//            }
//        }
//        .map { preferences ->
//            val sortBy = preferences[PreferencesKeys.DEF_SORT_BY] ?: SortBy.SUBJECT.name
//            val sortOrder = preferences[PreferencesKeys.DEF_SORT_ORDER] ?: SortOrder.ASC.name
//            Sort(
//                SortBy.valueOf(sortBy),
//                SortOrder.valueOf(sortOrder)
//            )
//        }
//
//    val preferencesFlow = context.dataStore.data
//        .catch { exception ->
//            when (exception) {
//                is IOException -> {
//                    emit(emptyPreferences())
//                    Log.e(TAG, "$exception")
//                }
//
//                else -> throw exception
//            }
//        }
//        .map { preferences ->
//            val defPercentage = preferences[PreferencesKeys.DEFAULT_PERCENTAGE] ?: 75
//            val course = preferences[PreferencesKeys.DEF_COURSE] ?: "BCA"
//            val sem = preferences[PreferencesKeys.DEF_SEM] ?: "1"
//            val sem1Cgpa = preferences[PreferencesKeys.DEF_SEM_1_CGPA] ?: 0.0
//            val sem2Cgpa = preferences[PreferencesKeys.DEF_SEM_2_CGPA] ?: 0.0
//            val sem3Cgpa = preferences[PreferencesKeys.DEF_SEM_3_CGPA] ?: 0.0
//            val sem4Cgpa = preferences[PreferencesKeys.DEF_SEM_4_CGPA] ?: 0.0
//            val sem5Cgpa = preferences[PreferencesKeys.DEF_SEM_5_CGPA] ?: 0.0
//            val sem6Cgpa = preferences[PreferencesKeys.DEF_SEM_6_CGPA] ?: 0.0
//            val earnCrSem1 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_1] ?: 0.0
//            val earnCrSem2 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_2] ?: 0.0
//            val earnCrSem3 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_3] ?: 0.0
//            val earnCrSem4 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_4] ?: 0.0
//            val earnCrSem5 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_5] ?: 0.0
//            val earnCrSem6 = preferences[PreferencesKeys.DEF_EARN_CR_SEM_6] ?: 0.0
//            val cgpa = preferences[PreferencesKeys.DEF_CGPA] ?: 0.0
//            val sortBy = preferences[PreferencesKeys.DEF_SORT_BY] ?: SortBy.SUBJECT.name
//            val sortOrder = preferences[PreferencesKeys.DEF_SORT_ORDER] ?: SortOrder.ASC.name
//
//
//
//            FilterPreferences(
//                defPercentage,
//                course,
//                sem,
//                Cgpa(
//                    sem1Cgpa,
//                    sem2Cgpa,
//                    sem3Cgpa,
//                    sem4Cgpa,
//                    sem5Cgpa,
//                    sem6Cgpa,
//                    cgpa,
//                    earnCrSem1,
//                    earnCrSem2,
//                    earnCrSem3,
//                    earnCrSem4,
//                    earnCrSem5,
//                    earnCrSem6
//                ),
//                Sort(
//                    SortBy.valueOf(sortBy),
//                    SortOrder.valueOf(sortOrder)
//                )
//            )
//        }
//
//
//    suspend fun updateCourse(value: String) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.DEF_COURSE] = value
//        }
//    }
//
//    suspend fun updateCgpa(cgpa: Cgpa) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.DEF_SEM_1_CGPA] = cgpa.sem1
//            preferences[PreferencesKeys.DEF_SEM_2_CGPA] = cgpa.sem2
//            preferences[PreferencesKeys.DEF_SEM_3_CGPA] = cgpa.sem3
//            preferences[PreferencesKeys.DEF_SEM_4_CGPA] = cgpa.sem4
//            preferences[PreferencesKeys.DEF_SEM_5_CGPA] = cgpa.sem5
//            preferences[PreferencesKeys.DEF_SEM_6_CGPA] = cgpa.sem6
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_1] = cgpa.earnCrSem1 ?: 0.0
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_2] = cgpa.earnCrSem2 ?: 0.0
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_3] = cgpa.earnCrSem3 ?: 0.0
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_4] = cgpa.earnCrSem4 ?: 0.0
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_5] = cgpa.earnCrSem5 ?: 0.0
//            preferences[PreferencesKeys.DEF_EARN_CR_SEM_6] = cgpa.earnCrSem6 ?: 0.0
//            preferences[PreferencesKeys.DEF_CGPA] = cgpa.cgpa
//        }
//    }
//
//    suspend fun updateSem(value: String) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.DEF_SEM] = value
//        }
//    }
//
//
//    suspend fun updatePercentage(value: Int) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.DEFAULT_PERCENTAGE] = value
//        }
//    }
//
//    suspend fun updateSort(sort: Sort) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesKeys.DEF_SORT_BY] = sort.sortBy.name
//            preferences[PreferencesKeys.DEF_SORT_ORDER] = sort.sortOrder.name
//        }
//    }
//
//    suspend fun clearAll() {
//        context.dataStore.edit { preferences ->
//            preferences.clear()
//        }
//    }
//
//
//    private object PreferencesKeys {
//        val DEF_COURSE = stringPreferencesKey("def_course")
//        val DEF_SEM = stringPreferencesKey("def_sem")
//        val DEFAULT_PERCENTAGE = intPreferencesKey("default_percentage")
//        val DEF_SEM_1_CGPA = doublePreferencesKey("default_sem_1_cgpa")
//        val DEF_SEM_2_CGPA = doublePreferencesKey("default_sem_2_cgpa")
//        val DEF_SEM_3_CGPA = doublePreferencesKey("default_sem_3_cgpa")
//        val DEF_SEM_4_CGPA = doublePreferencesKey("default_sem_4_cgpa")
//        val DEF_SEM_5_CGPA = doublePreferencesKey("default_sem_5_cgpa")
//        val DEF_SEM_6_CGPA = doublePreferencesKey("default_sem_6_cgpa")
//        val DEF_EARN_CR_SEM_1 = doublePreferencesKey("default_earn_cr_sem_1")
//        val DEF_EARN_CR_SEM_2 = doublePreferencesKey("default_earn_cr_sem_2")
//        val DEF_EARN_CR_SEM_3 = doublePreferencesKey("default_earn_cr_sem_3")
//        val DEF_EARN_CR_SEM_4 = doublePreferencesKey("default_earn_cr_sem_4")
//        val DEF_EARN_CR_SEM_5 = doublePreferencesKey("default_earn_cr_sem_5")
//        val DEF_EARN_CR_SEM_6 = doublePreferencesKey("default_earn_cr_sem_6")
//        val DEF_CGPA = doublePreferencesKey("default_cgpa")
//        val DEF_SORT_BY = stringPreferencesKey("default_sort_by")
//        val DEF_SORT_ORDER = stringPreferencesKey("default_sort_order")
//    }
//}
