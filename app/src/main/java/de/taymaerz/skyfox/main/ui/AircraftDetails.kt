package de.taymaerz.skyfox.main.ui

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.common.compose.InfoCell
import de.taymaerz.skyfox.common.flight.FlightRoute
import de.taymaerz.skyfox.common.flight.ui.HorizontalRouteBar
import de.taymaerz.skyfox.common.planespotters.PlanespottersMeta
import de.taymaerz.skyfox.common.planespotters.PlanespottersThumbnail
import de.taymaerz.skyfox.common.planespotters.toPlanespottersQuery
import de.taymaerz.skyfox.common.compose.Preview2
import de.taymaerz.skyfox.common.compose.PreviewWrapper
import de.taymaerz.skyfox.common.compose.preview.FakeAircraft
import de.taymaerz.skyfox.common.compose.preview.mockFlightRoute
import de.taymaerz.skyfox.main.core.aircraft.Aircraft
import de.taymaerz.skyfox.main.core.aircraft.isEmergencySquawk
import de.taymaerz.skyfox.main.core.aircraft.messageTypeLabel
import java.time.Instant

@Composable
fun AircraftDetails(
    aircraft: Aircraft,
    route: FlightRoute? = null,
    distanceInMeter: Float? = null,
    onThumbnailClick: ((PlanespottersMeta) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Header: description + distance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = aircraft.description
                    ?: stringResource(R.string.aircraft_details_description_unknown),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (distanceInMeter != null) {
                Text(
                    text = stringResource(
                        R.string.general_xdistance_away_label,
                        "${(distanceInMeter / 1000).toInt()} km",
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                )
            }
        }

        // Operator
        Text(
            text = aircraft.operator
                ?: stringResource(R.string.aircraft_details_operator_unknown),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        // Horizontal route bar
        if (route != null) {
            HorizontalRouteBar(
                route = route,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
            )
        }

        // Thumbnail + info grid side by side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            PlanespottersThumbnail(
                query = aircraft.toPlanespottersQuery(large = true),
                onImageClick = onThumbnailClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp),
            )

            InfoGrid(
                aircraft = aircraft,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )
        }

        // Footer: last seen + source
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = DateUtils.getRelativeTimeSpanString(
                    aircraft.seenAt.toEpochMilli(),
                    Instant.now().toEpochMilli(),
                    DateUtils.MINUTE_IN_MILLIS,
                ).toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    R.string.aircraft_details_datasource_x,
                    aircraft.messageTypeLabel,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview2
@Composable
private fun AircraftDetailsPreview() {
    PreviewWrapper { AircraftDetails(aircraft = FakeAircraft()) }
}

@Preview2
@Composable
private fun AircraftDetailsWithRoutePreview() {
    PreviewWrapper {
        AircraftDetails(
            aircraft = FakeAircraft(),
            route = mockFlightRoute(),
            distanceInMeter = 150_000f,
        )
    }
}

@Preview2
@Composable
private fun AircraftDetailsSquawkAlertPreview() {
    PreviewWrapper {
        AircraftDetails(aircraft = FakeAircraft(squawk = "7700"))
    }
}

@Composable
private fun InfoGrid(aircraft: Aircraft, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Row 1: Callsign | Registration
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell(
                value = aircraft.callsign?.takeIf { it.isNotBlank() } ?: "?",
                label = stringResource(R.string.common_callsign_label),
                modifier = Modifier.weight(1f),
            )
            InfoCell(
                value = aircraft.registration ?: "?",
                label = stringResource(R.string.common_registration_label),
                modifier = Modifier.weight(1f),
            )
        }
        // Row 2: Hex | Squawk
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell(
                value = "#${aircraft.hex.uppercase()}",
                label = stringResource(R.string.common_hex_label),
                modifier = Modifier.weight(1f),
                monoValue = true,
            )
            InfoCell(
                value = aircraft.squawk ?: "?",
                label = stringResource(R.string.common_squawk_label),
                modifier = Modifier.weight(1f),
                isAlert = aircraft.isEmergencySquawk,
                monoValue = true,
            )
        }
        // Row 3: Altitude | Speed
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell(
                value = "${aircraft.altitude ?: "?"} ft",
                label = stringResource(R.string.common_altitude_label),
                modifier = Modifier.weight(1f),
            )
            InfoCell(
                value = "${aircraft.indicatedAirSpeed ?: aircraft.groundSpeed ?: "?"} kts",
                label = stringResource(R.string.common_speed_label),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
