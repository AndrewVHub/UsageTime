package ru.andrewvhub.usagetime.data.providers

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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

    //Тут можно обработать и выбросить кастомку. Да вообще много чего можно сделать
    //Уменьшить код, оптимизировать получение инфы в целом
    override fun getImageAppByPackageName(packageName: String): Drawable? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}