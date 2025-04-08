package ru.andrewvhub.utils.extension

import android.content.res.Resources
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.data.models.UsageErrorException

fun Resources.getMessageFromThrowable(throwable: Throwable): String =
    when (throwable) {
        is UsageErrorException -> {
            when (throwable) {
                is UsageErrorException.UsageForPeriodException -> getString(throwable.errorResId)
                is UsageErrorException.UsageForAppPeriodException -> getString(throwable.errorResId)
                is UsageErrorException.ActiveUsageForAppDayException -> getString(throwable.errorResId)
            }
        }
        else -> getString(R.string.exception_unknown_error, throwable.message)
    }