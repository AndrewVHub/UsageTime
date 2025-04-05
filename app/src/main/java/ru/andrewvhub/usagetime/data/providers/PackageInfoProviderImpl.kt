package ru.andrewvhub.usagetime.data.providers

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import ru.andrewvhub.usagetime.domain.providers.PackageInfoProvider

class PackageInfoProviderImpl(
    private val packageManager: PackageManager
) : PackageInfoProvider {

    override fun getNameAppByPackageName(packageName: String): String {
        return runCatching {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationLabel(appInfo).toString()
        }.getOrNull().orEmpty()
    }

    override fun getImageAppByPackageName(packageName: String): Drawable? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppIcon", "Приложение с packageName '$packageName' не найдено: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("AppIcon", "Ошибка при получении иконки приложения '$packageName': ${e.message}")
            null
        }
    }
}