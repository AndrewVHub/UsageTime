package ru.andrewvhub.usagetime.ui.fragments.agreement

import ru.andrewvhub.usagetime.core.BaseViewModel

class AgreementViewModel: BaseViewModel() {

    fun navigateToAgreementDialog() {
        mainNavigate(AgreementFragmentDirections.actionAgreementFragmentToAgreementBottomSheetFragment())
    }
    fun navigateToMain() {
        mainNavigate(AgreementFragmentDirections.actionAgreementFragmentToMainFragment())
    }
}