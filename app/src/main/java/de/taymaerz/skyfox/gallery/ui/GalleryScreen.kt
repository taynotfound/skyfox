package de.taymaerz.skyfox.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import de.taymaerz.skyfox.common.compose.aplContentWindowInsets
import de.taymaerz.skyfox.common.error.ErrorEventHandler
import de.taymaerz.skyfox.common.navigation.NavigationEventHandler

@Composable
fun GalleryScreenHost(
    hex: String,
    registration: String?,
    vm: GalleryViewModel = hiltViewModel(),
) {
    NavigationEventHandler(vm)
    ErrorEventHandler(vm)

    LaunchedEffect(hex, registration) { vm.load(hex, registration) }

    val state by vm.state.collectAsState()

    Scaffold(
        contentWindowInsets = aplContentWindowInsets(),
        topBar = {
            TopAppBar(
                title = { Text(registration ?: hex.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = { vm.navUp() }) {
                        Icon(Icons.AutoMirrored.TwoTone.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.photos.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No photos found for this aircraft.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(state.photos, key = { it.id }) { photo ->
                    Card(onClick = { vm.openLink(photo.link) }) {
                        Box {
                            AsyncImage(
                                model = photo.thumbnailLarge.src,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(photo.thumbnailLarge.size.let { it.width.toFloat() / it.height }),
                            )
                            Text(
                                text = "© ${photo.photographer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
