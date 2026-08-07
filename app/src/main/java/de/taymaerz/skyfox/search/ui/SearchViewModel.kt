package de.taymaerz.skyfox.search.ui

import android.location.Location
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.taymaerz.skyfox.common.WebpageTool
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.datastore.value
import de.taymaerz.skyfox.common.datastore.valueBlocking
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.INFO
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.common.flow.SingleEventFlow
import de.taymaerz.skyfox.common.flow.combine
import de.taymaerz.skyfox.common.flow.replayingShare
import de.taymaerz.skyfox.common.flow.throttleLatest
import de.taymaerz.skyfox.common.location.LocationManager2
import de.taymaerz.skyfox.common.uix.ViewModel4
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.SquawkCode
import de.taymaerz.skyfox.map.core.AirplanesLive
import de.taymaerz.skyfox.map.core.MapOptions
import de.taymaerz.skyfox.map.ui.DestinationMap
import de.taymaerz.skyfox.search.core.SearchQuery
import de.taymaerz.skyfox.search.core.SearchRepo
import de.taymaerz.skyfox.search.core.SearchSettings
import de.taymaerz.skyfox.search.ui.actions.DestinationSearchAction
import de.taymaerz.skyfox.watch.core.WatchRepo
import de.taymaerz.skyfox.watch.core.types.AircraftWatch
import de.taymaerz.skyfox.watch.core.types.Watch
import de.taymaerz.skyfox.watch.ui.DestinationWatchDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Clock
import java.time.Duration
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val searchRepo: SearchRepo,
    private val webpageTool: WebpageTool,
    private val locationManager2: LocationManager2,
    private val settings: SearchSettings,
    watchRepo: WatchRepo,
    private val clock: Clock,
) : ViewModel4(
    dispatcherProvider = dispatcherProvider,
    tag = logTag("Search", "ViewModel"),
) {

    private var targetHexes: Set<AircraftHex>? = null
    private var targetSquawks: Set<SquawkCode>? = null
    private var targetCallsigns: Set<String>? = null
    private var initialized = false

    val events = SingleEventFlow<SearchEvents>()

    private val currentInput = MutableStateFlow<Input?>(null)

    private val searchTrigger = MutableStateFlow(UUID.randomUUID())
    private val currentSearch: Flow<SearchRepo.Result?> = combine(
        searchTrigger,
        currentInput.filterNotNull(),
    ) { _, input ->
        val terms = input.raw.split(",").map { it.trim() }.toSet()
        when (input.mode) {
            State.Mode.ALL -> SearchQuery.All(terms)
            State.Mode.HEX -> SearchQuery.Hex(terms)
            State.Mode.CALLSIGN -> SearchQuery.Callsign(terms)
            State.Mode.REGISTRATION -> SearchQuery.Registration(terms)
            State.Mode.SQUAWK -> SearchQuery.Squawk(terms)
            State.Mode.AIRFRAME -> SearchQuery.Airframe(terms)
            State.Mode.INTERESTING -> SearchQuery.Interesting(
                military = terms.contains("military"),
                ladd = terms.contains("ladd"),
                pia = terms.contains("pia"),
            )

            State.Mode.POSITION -> {
                var location = input.rawMeta as? Location
                if (location == null && input.raw.isNotBlank()) {
                    location = locationManager2.fromName(input.raw.trim())
                }
                if (location != null) {
                    SearchQuery.Position(location)
                } else {
                    SearchQuery.Position()
                }
            }
        }.also { log(tag) { "Mapped raw query: '$input' to $it" } }
    }
        .debounce(300)
        .map { searchRepo.liveSearch(it, SearchRepo.CachePolicy.CACHE_FIRST_UI) }
        .flatMapLatest { it }
        .replayingShare(viewModelScope)

    fun init(
        targetHexes: List<String>? = null,
        targetSquawks: List<String>? = null,
        targetCallsigns: List<String>? = null,
    ) {
        if (initialized) return
        initialized = true

        this.targetHexes = targetHexes?.toSet()
        this.targetSquawks = targetSquawks?.toSet()
        this.targetCallsigns = targetCallsigns?.toSet()

        log(tag, INFO) { "init: targetHexes=${this.targetHexes}, targetSquawks=${this.targetSquawks}, targetCallsigns=${this.targetCallsigns}" }

        launch {
            if (currentInput.value != null) return@launch

            when {
                this@SearchViewModel.targetHexes != null -> {
                    currentInput.value =
                        Input(State.Mode.HEX, raw = this@SearchViewModel.targetHexes!!.joinToString(","))
                }

                this@SearchViewModel.targetSquawks != null -> {
                    currentInput.value =
                        Input(State.Mode.SQUAWK, raw = this@SearchViewModel.targetSquawks!!.joinToString(","))
                }

                this@SearchViewModel.targetCallsigns != null -> {
                    currentInput.value = Input(State.Mode.CALLSIGN, raw = this@SearchViewModel.targetCallsigns!!.joinToString(","))
                }

                else -> {
                    updateMode(settings.inputLastMode.value())
                }
            }
        }
    }

    private val errorShownForSearch = MutableStateFlow<Set<Throwable>>(emptySet())

    val state = combine(
        currentInput.filterNotNull(),
        currentSearch.throttleLatest(500),
        watchRepo.watches,
        settings.searchLocationDismissed.flow,
        locationManager2.state,
        errorShownForSearch,
    ) { input, result, alerts, locationDismissed, locationState, shownErrors ->
        if (result != null && !result.searching && result.errors.isNotEmpty()) {
            val newError = result.errors.firstOrNull { it !in shownErrors }
            if (newError != null) {
                val isNetworkError = newError is java.net.UnknownHostException ||
                        newError is java.net.SocketTimeoutException ||
                        newError is java.net.ConnectException
                val silenced = isNetworkError
                errorShownForSearch.value = shownErrors + newError
                if (!silenced) {
                    events.tryEmit(SearchEvents.SearchError(newError))
                }
            }
        }

        val items = mutableListOf<SearchItem>()

        if (!locationDismissed && (locationState as? LocationManager2.State.Unavailable)?.isPermissionIssue == true) {
            items.add(SearchItem.LocationPrompt)
        }

        if (result?.aircraft != null) {
            if (result.searching) {
                items.add(SearchItem.Searching(aircraftCount = result.aircraft.size))
            } else if (result.aircraft.isEmpty()) {
                items.add(SearchItem.NoResults)
            } else {
                items.add(SearchItem.Summary(aircraftCount = result.aircraft.size, cacheOnlyCount = result.cacheOnlyCount))
            }
        }

        result?.aircraft
            ?.map { ac ->
                val age = Duration.between(ac.seenAt, clock.instant()).coerceAtLeast(Duration.ZERO)
                val freshness = when {
                    age < Duration.ofMinutes(5) -> Freshness.LIVE
                    age < Duration.ofHours(1) -> Freshness.RECENT
                    age < Duration.ofHours(24) -> Freshness.STALE
                    else -> Freshness.OLD
                }
                SearchItem.AircraftResult(
                    aircraft = ac,
                    watch = alerts.filterIsInstance<AircraftWatch>().firstOrNull { it.matches(ac) },
                    distanceInMeter = if (locationState is LocationManager2.State.Available && ac.location != null) {
                        locationState.location.distanceTo(ac.location!!)
                    } else {
                        null
                    },
                    freshness = freshness,
                )
            }
            ?.sortedBy { it.distanceInMeter ?: Float.MAX_VALUE }
            ?.run { items.addAll(this) }

        State(
            input = input,
            isSearching = result?.searching ?: false,
            items = items,
        )
    }.catch { e -> log(tag, de.taymaerz.skyfox.common.debug.logging.Logging.Priority.ERROR) { "State flow failed: ${e.message}" } }.asStateFlow()

    fun search(input: Input) {
        log(tag) { "search($input)" }
        errorShownForSearch.value = emptySet()
        if (currentInput.value == input) {
            searchTrigger.value = UUID.randomUUID()
        } else {
            currentInput.value = input
        }
    }

    fun updateSearchText(raw: String) = launch {
        log(tag) { "updateSearchText($raw)" }
        val oldInput = currentInput.value ?: Input()
        val newInput = when (oldInput.mode) {
            State.Mode.ALL -> {
                settings.inputLastAll.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.HEX -> {
                settings.inputLastHex.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.CALLSIGN -> {
                settings.inputLastCallsign.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.REGISTRATION -> {
                settings.inputLastRegistration.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.SQUAWK -> {
                settings.inputLastSquawk.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.AIRFRAME -> {
                settings.inputLastAirframe.value(raw)
                Input(oldInput.mode, raw = raw)
            }

            State.Mode.INTERESTING -> {
                settings.inputLastInteresting.value(raw)
                Input(State.Mode.INTERESTING, raw = raw)
            }

            State.Mode.POSITION -> {
                settings.inputLastPosition.value(raw)
                Input(
                    oldInput.mode,
                    raw = raw,
                    rawMeta = raw.trim().takeIf { it.isNotBlank() }?.let { locationManager2.fromName(it) },
                )
            }
        }

        log(tag) { "updateSearchText(): $oldInput -> $newInput " }
        search(newInput)
    }

    fun updateMode(mode: State.Mode) = launch {
        log(tag) { "updateMode($mode)" }
        val newInput = when (mode) {
            State.Mode.ALL -> Input(mode, raw = settings.inputLastAll.value())
            State.Mode.REGISTRATION -> Input(mode, raw = settings.inputLastRegistration.value())
            State.Mode.HEX -> Input(mode, raw = settings.inputLastHex.value())
            State.Mode.CALLSIGN -> Input(mode, raw = settings.inputLastCallsign.value())
            State.Mode.AIRFRAME -> Input(mode, raw = settings.inputLastAirframe.value())
            State.Mode.SQUAWK -> Input(mode, raw = settings.inputLastSquawk.value())
            State.Mode.INTERESTING -> Input(mode, raw = settings.inputLastInteresting.value())
            State.Mode.POSITION -> Input(mode, raw = settings.inputLastPosition.value())
        }
        log(tag) { "updateMode(): -> $newInput" }
        search(newInput)
    }

    fun openAircraftAction(hex: AircraftHex) {
        navTo(DestinationSearchAction(hex = hex))
    }

    fun openThumbnail(meta: de.taymaerz.skyfox.common.planespotters.PlanespottersMeta) {
        navTo(de.taymaerz.skyfox.gallery.ui.DestinationGallery(hex = meta.hex))
    }

    fun openWatch(watch: Watch) {
        navTo(DestinationWatchDetails(watchId = watch.id))
    }

    fun showOnMap(aircraft: Collection<Aircraft>) {
        log(tag) { "showOnMap(${aircraft.size} items)" }
        if (aircraft.isEmpty()) return
        navTo(DestinationMap(mapOptions = MapOptions.focusAircraft(aircraft.toSet())))
    }

    fun requestLocationPermission() {
        events.emitBlocking(SearchEvents.RequestLocationPermission)
    }

    fun dismissLocationPrompt() {
        settings.searchLocationDismissed.valueBlocking = true
    }

    fun startFeeding() = launch {
        webpageTool.open(AirplanesLive.URL_START_FEEDING)
    }

    fun searchPositionHome() = launch {
        log(tag) { "searchPositionHome()" }
        val locationState = withTimeoutOrNull(2000) {
            locationManager2.state
                .filter { it !is LocationManager2.State.Waiting }
                .first()
        }

        if (locationState !is LocationManager2.State.Available) {
            log(tag) { "Location unavailable" }
            return@launch
        }

        val location = locationState.location

        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#.##", symbols)
        val roundedLat = formatter.format(location.latitude).toDouble()
        val roundedLon = formatter.format(location.longitude).toDouble()
        val altText = "${roundedLat},${roundedLon}"
        val address = locationManager2.toName(location)
        val input = Input(
            State.Mode.POSITION,
            raw = address?.let { "${it.locality}, ${it.countryName}" } ?: altText,
            rawMeta = location,
        )
        settings.inputLastPosition.value(input.raw)
        search(input)
    }

    enum class Freshness { LIVE, RECENT, STALE, OLD }

    sealed interface SearchItem {
        data object LocationPrompt : SearchItem
        data class Searching(val aircraftCount: Int) : SearchItem
        data object NoResults : SearchItem
        data class Summary(val aircraftCount: Int, val cacheOnlyCount: Int = 0) : SearchItem
        data class AircraftResult(
            val aircraft: Aircraft,
            val watch: Watch?,
            val distanceInMeter: Float?,
            val freshness: Freshness = Freshness.LIVE,
        ) : SearchItem
    }

    data class State(
        val input: Input,
        val items: List<SearchItem>,
        val isSearching: Boolean = false,
    ) {
        @Serializable
        enum class Mode {
            @SerialName("ALL") ALL,
            @SerialName("HEX") HEX,
            @SerialName("CALLSIGN") CALLSIGN,
            @SerialName("REGISTRATION") REGISTRATION,
            @SerialName("SQUAWK") SQUAWK,
            @SerialName("AIRFRAME") AIRFRAME,
            @SerialName("INTERESTING") INTERESTING,
            @SerialName("POSITION") POSITION,
            ;
        }
    }

    data class Input(
        val mode: State.Mode = State.Mode.INTERESTING,
        val raw: String = "military, pia, ladd",
        val rawMeta: Any? = null,
    )
}
