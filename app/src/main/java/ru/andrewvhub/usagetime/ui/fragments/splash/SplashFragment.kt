package ru.andrewvhub.usagetime.ui.fragments.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.utils.extension.hasUsageStatsPermission
import ru.andrewvhub.utils.extension.nonNullObserve

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    override val viewModel by viewModels<SplashViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        return viewModel.delayLiveData.nonNullObserve(viewLifecycleOwner) {
            if (!hasUsageStatsPermission())
                viewModel.navigateToAgreement()
            else
                viewModel.navigateToMain()
        }
    }
}