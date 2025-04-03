package ru.kaycom.usagetime.ui.fragments.agreement

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.kaycom.usagetime.R
import ru.kaycom.usagetime.core.BaseFragment
import ru.kaycom.usagetime.databinding.FragmentAgreementBinding
import ru.kaycom.usagetime.ui.viewBinding.viewBinding
import ru.kaycom.utils.extension.addSystemTopSpace

class AgreementFragment : BaseFragment(R.layout.fragment_agreement) {

    private val viewBinding by viewBinding(FragmentAgreementBinding::bind)
    override val viewModel by viewModel<AgreementViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(viewBinding) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.addSystemTopSpace(false)

    }

}