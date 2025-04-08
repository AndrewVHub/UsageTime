package ru.andrewvhub.utils.extension

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Process
import android.provider.Settings
import android.view.View.inflate
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.databinding.ErrorMassageViewBinding
import java.io.File

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

fun Fragment.getColor(@ColorRes colorResId: Int) =
    ContextCompat.getColor(requireContext(), colorResId)

fun Fragment.getFont(@FontRes fontResId: Int) = ResourcesCompat.getFont(requireContext(), fontResId)

fun Fragment.shareJsonFile(file: File) {
    val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, "Поделиться данными"))
}

fun Fragment.showSnackBar(message: String, title: String = getString(R.string.common_error), ) {
    val snackView = inflate(requireContext(), R.layout.error_massage_view, null)
    val binding = ErrorMassageViewBinding.bind(snackView)
    val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
    snackBar.apply {
        (view as ViewGroup).addView(binding.root)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        binding.apply {
            titleError.text = title
            descriptionError.text = message
        }
        show()
    }
}
