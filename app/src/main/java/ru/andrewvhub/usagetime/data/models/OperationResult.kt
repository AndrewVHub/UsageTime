package ru.andrewvhub.usagetime.data.models

sealed class OperationResult<out T> {
    data class Success<out T>(val data: T) : OperationResult<T>()
    data class Error(val error: Throwable) : OperationResult<Nothing>()
}

fun <T> OperationResult<T>.getOrElse(handler: (Throwable) -> T): T {
    return when (this) {
        is OperationResult.Success -> data
        is OperationResult.Error -> handler(error)
    }
}

fun <T> OperationResult<T>.getOrError() = getOrElse {
    throw it
}