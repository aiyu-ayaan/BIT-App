package com.atech.bit.utils

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.atech.core.datasource.firebase.auth.AttendanceUploadModel
import com.atech.core.datasource.room.attendance.AttendanceDao
import com.atech.core.datasource.room.attendance.AttendanceModel
import com.atech.core.usecase.AuthUseCases
import com.atech.core.utils.BitAppScope
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.UpdateDataType
import com.atech.core.utils.compareDifferenceInDays
import com.atech.core.utils.fromJSON
import com.atech.core.utils.toJSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.max


private const val TAG = "AttendanceUpload"

interface AttendanceUpload {

    fun registerLifeCycleOwner(
        owner: LifecycleOwner
    )

    fun getInstances(
        dao: AttendanceDao,
        auth: AuthUseCases,
        pref: SharedPreferences,
        maxTime: Int = 10,
        @BitAppScope scope: CoroutineScope
    )
}

class AttendanceUploadDelegate : AttendanceUpload, LifecycleEventObserver {
    private var dao: AttendanceDao? = null
    private var auth: AuthUseCases? = null
    private var scope: CoroutineScope? = null
    private var pref: SharedPreferences? = null
    private var oldList: List<AttendanceModel>? = null
    private var maxTime = 10
    override fun registerLifeCycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun getInstances(
        dao: AttendanceDao,
        auth: AuthUseCases,
        pref: SharedPreferences,
        maxTime: Int,
        scope: CoroutineScope
    ) {
        this.dao = dao
        this.auth = auth
        this.scope = scope
        this.pref = pref
        this.maxTime = maxTime
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                scope?.launch {
                    oldList = getAttendanceList().await()
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                scope?.launch {
                    val newList = getAttendanceList().await()
                    Log.d(TAG, "onStateChanged: ${newList areEqual oldList}")
                    if (!(newList areEqual oldList)) {
                        savedPrefLogic {
                            auth?.uploadData?.invoke(
                                UpdateDataType.UploadAttendance(
                                    newList.toUploadModel()
                                )
                            )
                        }
                    }
                }

            }


            else -> {
                // Who cases
            }
        }
    }

    private suspend fun savedPrefLogic(
        action: suspend () -> Unit = {}
    ) {
        pref?.getString(
            SharePrefKeys.UploadTime.name, UploadTime().json()
        )?.toUploadTime()?.let { uploadTime ->
            val (times, date) = uploadTime
            if (Date().compareDifferenceInDays(Date(date)) == 0) {
                if (times < maxTime) {
                    action()
                    saveUploadTime(uploadTime.increaseTimes().also {
                        Log.d(TAG, "onStateChanged: $it")
                    })
                } else Log.d(TAG, "onStateChanged: Reached max times")
            } else {
                action()
                saveUploadTime(UploadTime().increaseTimes())
            }
        }
    }

    private fun saveUploadTime(model: UploadTime) {
        pref?.edit()?.putString(SharePrefKeys.UploadTime.name, model.json())?.apply()
    }

    private fun List<AttendanceModel>.toUploadModel() = this.map { a ->
        AttendanceUploadModel(
            a.subject,
            a.total,
            a.present,
            a.teacher,
            a.fromSyllabus,
            a.isArchive,
            a.fromOnlineSyllabus,
            a.created
        )
    }

    private infix fun List<AttendanceModel>.areEqual(oldList: List<AttendanceModel>?): Boolean {
        if (oldList == null) return false
        if (this.size != oldList.size) return false
        for (i in this.indices) {
            if (!(this[i] areEqual oldList[i])) return false
        }
        return true
    }

    private infix fun AttendanceModel.areEqual(old: AttendanceModel?): Boolean {
        if (old == null) return false
        return this.subject == old.subject && this.total == old.total && this.present == old.present && this.teacher == old.teacher && this.fromSyllabus == old.fromSyllabus && this.isArchive == old.isArchive && this.fromOnlineSyllabus == old.fromOnlineSyllabus && this.created == old.created
    }


    private fun getAttendanceList(): Deferred<List<AttendanceModel>> {
        return scope!!.async(Dispatchers.IO) {
            if (dao == null || scope == null) {
                Log.e(TAG, "getAttendanceList: dao or scope is null")
                return@async emptyList<AttendanceModel>()
            }
            dao!!.getAllAttendance()
        }
    }

    data class UploadTime(
        val times: Int = 0, val date: Long = System.currentTimeMillis()
    )

    private fun UploadTime.increaseTimes() = this.copy(times = this.times + 1)

    private fun UploadTime.json() = toJSON(this)

    private fun String.toUploadTime() = fromJSON(this, UploadTime::class.java) ?: UploadTime()

}