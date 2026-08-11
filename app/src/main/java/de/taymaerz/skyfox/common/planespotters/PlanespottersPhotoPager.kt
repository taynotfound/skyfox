package de.taymaerz.skyfox.common.planespotters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.taymaerz.skyfox.common.planespotters.coil.AircraftThumbnailQuery
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.Registration

/**
 * Swipeable photo pager for aircraft images from Planespotters.net.
 * Automatically fetches the total photo count and shows all available photos
 * as swipeable pages with dot indicators.
 */
@Composable
fun PlanespottersPhotoPager(
    hex: AircraftHex,
    registration: Registration?,
    modifier: Modifier = Modifier,
    onImageClick: ((PlanespottersMeta) -> Unit)? = null,
    vm: PlanespottersPhotoCountViewModel = hiltViewModel(),
) {
    LaunchedEffect(hex, registration) {
        vm.load(hex, registration)
    }

    val photoCount by vm.photoCount.collectAsState()
    val pagerState = rememberPagerState(pageCount = { photoCount })

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            PlanespottersThumbnail(
                query = AircraftThumbnailQuery(
                    hex = hex,
                    registration = registration,
                    large = true,
                    photoIndex = page,
                ),
                modifier = Modifier.fillMaxWidth(),
                onImageClick = onImageClick,
            )
        }

        // Dot indicators — only shown when multiple photos are available
        if (photoCount > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(photoCount.coerceAtMost(10)) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (selected) 7.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) Color.White else Color.White.copy(alpha = 0.45f)
                            ),
                    )
                }
            }
        }
    }
}
