package ru.andrewvhub.usagetime.ui.fragments.agreement

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.usagetime.databinding.FragmentAgreementBinding
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.extension.addSystemBottomSpace
import ru.andrewvhub.utils.extension.addSystemTopSpace
import ru.andrewvhub.utils.extension.hasUsageStatsPermission
import ru.andrewvhub.utils.extension.setOnThrottleClickListener

class AgreementFragment : BaseFragment(R.layout.fragment_agreement) {

    private val viewBinding by viewBinding(FragmentAgreementBinding::bind)
    override val viewModel by viewModel<AgreementViewModel>()

    private var navigatedAfterPermissionGrant = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.addSystemTopSpace(false)
        startButton.addSystemBottomSpace(false)

        startButton.setOnThrottleClickListener {
            if (!hasUsageStatsPermission()) {
                viewModel.navigateToAgreementDialog()
            } else {
                viewModel.navigateToMain()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission() && !navigatedAfterPermissionGrant) {
            navigatedAfterPermissionGrant = true
            viewModel.navigateToMain()
        } else if (!hasUsageStatsPermission()) {
            navigatedAfterPermissionGrant = false // Сбрасываем флаг, если разрешение отозвано
        }
    }
}