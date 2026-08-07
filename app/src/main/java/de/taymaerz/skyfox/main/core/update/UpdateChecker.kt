package de.taymaerz.skyfox.main.core.update

import dagger.Reusable
import de.taymaerz.skyfox.common.BuildConfigWrap
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.ERROR
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.INFO
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.VERBOSE
import de.taymaerz.skyfox.common.debug.logging.asLog
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.github.GithubApi
import de.taymaerz.skyfox.common.github.GithubReleaseCheck
import de.taymaerz.skyfox.main.core.GeneralSettings
import net.swiftzer.semver.SemVer
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@Reusable
class UpdateChecker @Inject constructor(
    private val githubReleaseCheck: GithubReleaseCheck,
    private val generalSettings: GeneralSettings,
) {

    suspend fun checkForUpdate(): GithubApi.ReleaseInfo? {
        if (BuildConfigWrap.FLAVOR != BuildConfigWrap.Flavor.FOSS) {
            log(TAG, VERBOSE) { "checkForUpdate(): Not FOSS flavor, skipping" }
            return null
        }

        if (!generalSettings.isUpdateCheckEnabled.value()) {
            log(TAG, VERBOSE) { "checkForUpdate(): Update check disabled, skipping" }
            return null
        }

        val release = try {
            githubReleaseCheck.latestRelease(GITHUB_OWNER, GITHUB_REPO)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log(TAG, ERROR) { "Release check failed: ${e.asLog()}" }
            return null
        }

        val current = try {
            SemVer.parse(BuildConfigWrap.VERSION_NAME.removePrefix("v"))
        } catch (e: IllegalArgumentException) {
            log(TAG, ERROR) { "Failed to parse current version: ${e.asLog()}" }
            return null
        }
        log(TAG, INFO) { "Current version is $current" }

        val latest = try {
            SemVer.parse(release.tagName.removePrefix("v"))
        } catch (e: IllegalArgumentException) {
            log(TAG, ERROR) { "Failed to parse latest version: ${e.asLog()}" }
            return null
        }
        log(TAG, INFO) { "Latest version is $latest" }

        return if (current < latest) release else null
    }

    companion object {
        private const val GITHUB_OWNER = "taynotfound"
        private const val GITHUB_REPO = "skyfox"
        private val TAG = logTag("Update", "Checker")
    }
}
