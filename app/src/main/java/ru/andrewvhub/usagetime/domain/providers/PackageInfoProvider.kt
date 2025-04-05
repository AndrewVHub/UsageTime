package ru.andrewvhub.usagetime.domain.providers

import android.graphics.drawable.Drawable

interface PackageInfoProvider {
    fun getNameAppByPackageName(packageName: String): String
    fun getImageAppByPackageName(packageName: String): Drawable?
}