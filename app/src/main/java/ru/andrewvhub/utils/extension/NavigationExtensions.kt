package ru.andrewvhub.utils.extension

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber

fun NavController.navigateSafe(destination: NavDirections) =
    takeIf {
        it.currentDestination?.getAction(destination.actionId) != null
    }?.navigate(destination) ?: Timber.wtf(
        "BaseFragment::navigateSafety, error destination ${destination.actionId}"
    )