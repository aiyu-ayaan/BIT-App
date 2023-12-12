package com.atech.core.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.atech.core.data_source.room.attendance.AttendanceDao
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.utils.DEFAULTPAGESIZE
import com.atech.core.utils.INITIALlOADSIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


data class AttendanceUseCase @Inject constructor(
    val getAllAttendance: GetAllAttendance
)

data class GetAllAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    operator fun invoke(
        sort: Sort = Sort()
    ): Flow<PagingData<AttendanceModel>> =
        Pager(
            config = PagingConfig(
                pageSize = DEFAULTPAGESIZE,
                enablePlaceholders = false,
                initialLoadSize = INITIALlOADSIZE
            )
        ) {
            dao.getAttendanceSorted(sort)
        }.flow

}