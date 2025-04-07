package ru.andrewvhub.usagetime.ui.fragments.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ru.andrewvhub.usagetime.core.BaseViewModel

private const val MIN_DELAY = 2000L

class SplashViewModel: BaseViewModel() {

    private val delayFlow = flow {
        delay(MIN_DELAY)
        emit(Unit)
    }

    val delayLiveData: LiveData<Unit> = delayFlow.asLiveData()

    fun navigateToAgreement() {
        mainNavigate(SplashFragmentDirections.actionSplashFragmentToAgreementFragment())
    }
    fun navigateToMain() {
        mainNavigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())
    }
}