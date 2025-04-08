package ru.andrewvhub.usagetime.data.models

import androidx.annotation.StringRes
import ru.andrewvhub.usagetime.R
import java.io.IOException

sealed class UsageErrorException(@StringRes val errorResId: Int) : IOException() {
    data class UsageForPeriodException(override val message: String? = null) :
        UsageErrorException(R.string.exception_error_usage_for_period)

    data class UsageForAppPeriodException(override val message: String? = null) :
        UsageErrorException(R.string.exception_error_usage_for_app_period)

    data class ActiveUsageForAppDayException(override val message: String? = null) :
        UsageErrorException(R.string.exception_error_active_usage_for_app_day)
}