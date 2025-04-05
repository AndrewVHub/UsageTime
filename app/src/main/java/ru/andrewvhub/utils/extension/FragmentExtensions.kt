package ru.andrewvhub.utils.extension

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.fragment.app.Fragment

fun Fragment.hasUsageStatsPermission(): Boolean {
    val appOpsManager = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        requireContext().packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun Fragment.requestUsageStatsPermission() {
    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
}