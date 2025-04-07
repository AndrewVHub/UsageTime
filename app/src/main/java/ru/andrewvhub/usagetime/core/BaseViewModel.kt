package ru.andrewvhub.usagetime.core

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.andrewvhub.utils.SingleLiveEvent
import timber.log.Timber

abstract class BaseViewModel : ViewModel(), KoinComponent {

    protected val resources: Resources by inject()

    private val _navigate = SingleLiveEvent<NavDirections>()
    val navigate: LiveData<NavDirections> = _navigate

    private val _mainNavigate = SingleLiveEvent<NavDirections>()
    val mainNavigate: LiveData<NavDirections> = _mainNavigate

    fun mainNavigate(destination: NavDirections) {
        _mainNavigate.value = destination
    }
}

fun ViewModel.launchSafe(
    body: suspend () -> Unit,
    onError: ((error: Throwable) -> Unit)? = null,
    start: (() -> Unit)? = null,
    final: (() -> Unit)? = null
): Job = viewModelScope.launch(Dispatchers.IO) {
    try {
        start?.invoke()
        body()
    } catch (error: Exception) {
        Timber.tag("LAUNCH_SAFE").e(error)
        onError?.invoke(error)
    } finally {
        final?.invoke()
    }
}