package de.taymaerz.skyfox.main.ui.settings

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.PrivacyPolicy
import de.taymaerz.skyfox.common.SponsorHelper
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.github.GithubApi
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.update.UpdateChecker
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SettingsIndexViewModel @Inject constructor(
    @Suppress("unused") private val handle: SavedStateHandle,
    dispatcherProvider: DispatcherProvider,
    private val webpageTool: WebpageTool,
    private val sponsorHelper: SponsorHelper,
    updateChecker: UpdateChecker,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Settings", "Index", "VM"),
) {

    val newRelease = flow {
        emit(updateChecker.checkForUpdate())
    }.asStateFlow()

    fun goGeneralSettings() = navTo(DestinationGeneralSettings)

    fun goMapSettings() = navTo(DestinationMapSettings)

    fun goWatchSettings() = navTo(DestinationWatchSettings)

    fun goFeederSettings() = navTo(DestinationFeederSettings)

    fun goFeederList() = navTo(de.taymaerz.skyfox.feeder.ui.DestinationFeederList)

    fun goForkRepo() {
        webpageTool.open("https://github.com/taynotfound/prettier-airplanes-live")
    }

    fun goUpstreamRepo() {
        webpageTool.open("https://github.com/d4rken-org/airplanes-live-app")
    }

    fun goSponsor() = launch {
        log(tag) { "goSponsor()" }
        sponsorHelper.openSponsorPage()
    }

    fun goChangelog() {
        log(tag) { "goChangelog()" }
        webpageTool.open("https://github.com/taynotfound/prettier-airplanes-live/releases/latest")
    }

    fun openUpdate(release: GithubApi.ReleaseInfo) {
        log(tag) { "openUpdate(${release.tagName})" }
        val apkAsset = release.assets.find { it.name.endsWith(".apk", ignoreCase = true) }
        val url = apkAsset?.downloadUrl ?: release.htmlUrl
        webpageTool.open(url)
    }

    fun goSupport() = navTo(DestinationSupport)

    fun goAcknowledgements() = navTo(DestinationAcknowledgements)

    fun goPrivacyPolicy() {
        log(tag) { "goPrivacyPolicy()" }
        webpageTool.open(PrivacyPolicy.URL)
    }

    fun goBackupRestore() = navTo(DestinationBackupRestore)
}
