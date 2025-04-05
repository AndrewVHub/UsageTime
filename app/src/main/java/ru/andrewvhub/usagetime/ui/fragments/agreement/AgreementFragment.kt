package ru.andrewvhub.usagetime.ui.fragments.agreement

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.core.BaseFragment
import ru.andrewvhub.usagetime.databinding.FragmentAgreementBinding
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding
import ru.andrewvhub.utils.extension.addSystemTopSpace
import ru.andrewvhub.utils.extension.hasUsageStatsPermission

class AgreementFragment : BaseFragment(R.layout.fragment_agreement) {

    private val viewBinding by viewBinding(FragmentAgreementBinding::bind)
    override val viewModel by viewModel<AgreementViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.addSystemTopSpace(false)

        if (hasUsageStatsPermission()){

        } else {

        }
    }
}