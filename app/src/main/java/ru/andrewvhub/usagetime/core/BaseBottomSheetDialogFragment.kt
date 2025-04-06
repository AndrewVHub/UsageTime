package ru.andrewvhub.usagetime.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.andrewvhub.usagetime.ui.MainNavigator
import ru.andrewvhub.utils.extension.navigateSafe
import ru.andrewvhub.utils.extension.nonNullObserve

abstract class BaseBottomSheetDialogFragment(
    private val layoutResId: Int,
    private val isAppearanceLightStatusBars: Boolean = false
) : BottomSheetDialogFragment() {

    protected open val viewModel: BaseViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false).also {

        //сделать иконки на статус-баре белыми
        dialog?.window?.let {
            val windowInsetController = WindowCompat.getInsetsController(it, it.decorView)
            windowInsetController.isAppearanceLightStatusBars = isAppearanceLightStatusBars
        }

        viewModel?.navigate?.nonNullObserve(viewLifecycleOwner) {
            findNavController().navigateSafe(it)
        }

        viewModel?.mainNavigate?.nonNullObserve(viewLifecycleOwner) {
            (activity as? MainNavigator)?.getNavController()?.navigateSafe(it)
        }
    }
}