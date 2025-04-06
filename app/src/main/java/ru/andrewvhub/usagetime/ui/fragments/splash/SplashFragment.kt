package ru.andrewvhub.usagetime.ui.fragments.splash

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.usagetime.databinding.FragmentSplashBinding
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.extension.hasUsageStatsPermission
import ru.andrewvhub.utils.extension.nonNullObserve

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val viewBinding by viewBinding(FragmentSplashBinding::bind)
    override val viewModel by viewModels<SplashViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.delayLiveData.nonNullObserve(viewLifecycleOwner) {
            if (!hasUsageStatsPermission())
                viewModel.navigateToAgreement()
            else
                Log.d("OS4:SplashFragment", "Навигация на MainFragment")
        }
    }

}