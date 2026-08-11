package de.taymaerz.skyfox.main.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.WARN
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.github.GithubApi
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.GeneralSettings
import de.taymaerz.skyfox.main.core.ThemeState
import de.taymaerz.skyfox.main.core.themeState
import de.taymaerz.skyfox.main.core.update.UpdateChecker
import de.taymaerz.skyfox.map.core.MapOptions
import de.taymaerz.skyfox.map.ui.DestinationMap
import de.taymaerz.skyfox.watch.core.WatchId
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.getStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MainActivityVM @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val watchRepo: WatchRepo,
    private val updateChecker: UpdateChecker,
    private val generalSettings: GeneralSettings,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Main", "Activity", "ViewModel")
) {

    private val readyStateInternal = MutableStateFlow(true)
    val readyState = readyStateInternal.asStateFlow()

    val themeState = generalSettings.themeState
        .stateIn(vmScope, SharingStarted.Eagerly, ThemeState())

    val updateRelease = flow<GithubApi.ReleaseInfo?> {
        val release = updateChecker.checkForUpdate()
        if (release != null) {
            val dismissed = generalSettings.dismissedUpdateVersion.value()
            if (release.tagName == dismissed) {
                log(TAG) { "Update ${release.tagName} was snoozed, skipping dialog" }
                emit(null)
            } else {
                emit(release)
            }
        }
    }.stateIn(vmScope, SharingStarted.Lazily, null)

    fun snoozeUpdate(release: GithubApi.ReleaseInfo) = launch {
        log(TAG) { "snoozeUpdate(${release.tagName})" }
        generalSettings.dismissedUpdateVersion.value(release.tagName)
    }

    fun onGo() {
        // Ready
    }

    fun showWatchAlert(watchId: WatchId) = launch {
        val status = watchRepo.getStatus(watchId)
        if (status == null) {
            log(TAG, WARN) { "Watch with id $watchId no longer exists" }
            return@launch
        }
        if (status.tracked.isEmpty()) {
            log(TAG) { "No aircraft: $status" }
        } else {
            val mapOptions = MapOptions.focus(status.tracked.map { it.hex })
            navTo(DestinationMap(mapOptions = mapOptions))
        }
    }

    companion object {
        private val TAG = logTag("Main", "Activity", "ViewModel")
    }
}
