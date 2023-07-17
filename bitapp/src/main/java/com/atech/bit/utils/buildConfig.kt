package com.atech.bit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import com.atech.bit.BuildConfig
import com.atech.theme.R
import com.atech.theme.convertLongToTime
import com.atech.theme.openCustomChromeTab
import java.util.Date

fun Activity.openBugLink(
    @StringRes reportType: Int = R.string.bug_repost,
    extraString: String = "",
    reportDes: String? = null
) = this.startActivity(
    Intent.createChooser(
        Intent().also {
            it.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email))
            it.putExtra(Intent.EXTRA_SUBJECT, resources.getString(reportType))
            it.putExtra(
                Intent.EXTRA_TEXT, if (extraString.isNotBlank()) """Found a bug on $extraString  
                                |happened on ${Date().time.convertLongToTime("dd/mm/yyyy hh:mm:aa")}
                                |due to $reportDes .
                                |
                                |Device Info: ${Build.MANUFACTURER} ${Build.MODEL}
                                |Android Version: ${Build.VERSION.RELEASE}
                                |App Version: ${BuildConfig.VERSION_NAME}
                                |""".trimMargin()
                else ""
            )
            it.type = "text/html"
            it.setPackage(resources.getString(R.string.gmail_package))
        }, resources.getString(R.string.bug_title)
    )
)

fun Context.isBeta(): Boolean = BuildConfig.VERSION_NAME.contains("-beta")
fun Activity.openReleaseNotes() {
    val link = isBeta().let {
        val version =
            if (BuildConfig.VERSION_NAME.contains("\\sPatch\\s".toRegex())) BuildConfig.VERSION_NAME.replace(
                "\\sPatch\\s".toRegex(), "."
            )
            else BuildConfig.VERSION_NAME
        if (it) resources.getString(
            R.string.release_notes, "pre-release-v$version"
        )
        else resources.getString(R.string.release_notes, "v$version")
    }
    this.openCustomChromeTab(link.replace("-[b,g]\\w+".toRegex(), ""))

}

fun getVersion() = BuildConfig.VERSION_NAME
    .replace("-beta", "")
    .replace("-playStore", "")