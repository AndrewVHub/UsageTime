package ru.andrewvhub.usagetime.ui.fragments.agreement

import android.os.Bundle
import android.util.Log
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
                Log.d("OS4:AgreementFragment", "Разрешение отсутствует при клике, навигируем на Диалог")
                viewModel.navigateToAgreementDialog()
            } else {
                Log.d("OS4:AgreementFragment", "Разрешение уже есть при клике, навигируем дальше")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission() && !navigatedAfterPermissionGrant) {
            Log.d("OS4:AgreementFragment", "Разрешение есть в onResume, навигируем дальше")
            navigatedAfterPermissionGrant = true
        } else if (!hasUsageStatsPermission()) {
            Log.d("OS4:AgreementFragment", "Разрешение отсутствует в onResume")
            navigatedAfterPermissionGrant = false // Сбрасываем флаг, если разрешение отозвано
        } else {
            Log.d("OS4:AgreementFragment", "Уже навигировали после получения разрешения или разрешение все еще есть")
        }
    }
}