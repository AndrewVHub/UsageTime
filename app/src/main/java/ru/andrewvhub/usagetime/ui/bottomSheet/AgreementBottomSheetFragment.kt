package ru.andrewvhub.usagetime.ui.bottomSheet

import android.os.Bundle
import android.view.View
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseBottomSheetDialogFragment
import ru.andrewvhub.usagetime.databinding.FragmentAgreementBottomSheetBinding
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.extension.requestUsageStatsPermission
import ru.andrewvhub.utils.extension.setOnThrottleClickListener

class AgreementBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.fragment_agreement_bottom_sheet) {

    private val viewBinding by viewBinding(FragmentAgreementBottomSheetBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)

        navigateToSettings.setOnThrottleClickListener {
            dismiss()
            requestUsageStatsPermission()
        }
    }
}