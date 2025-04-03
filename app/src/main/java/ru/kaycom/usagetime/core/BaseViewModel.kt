package ru.kaycom.usagetime.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import ru.kaycom.utils.SingleLiveEvent
import timber.log.Timber

abstract class BaseViewModel : ViewModel(), KoinComponent {

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
): Job = viewModelScope.launch {
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

fun <T> CoroutineScope.asyncSafe(
    body: suspend () -> T,
    onFailure: ((error: Throwable) -> Unit)? = null,
): Deferred<T?> =
    async {
        runCatching {
            body()
        }.getOrElse {
            onFailure?.invoke(it)
            null
        }
    }