package dev.abdallah.rhythm

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import dev.abdallah.rhythm.player.service.PlaybackService
import dev.abdallah.rhythm.ui.component.Category
import dev.abdallah.rhythm.ui.component.MiniPlayer
import dev.abdallah.rhythm.ui.screen.Songs
import dev.abdallah.rhythm.ui.screen.albums.AlbumScreen
import dev.abdallah.rhythm.ui.screen.albums.Albums
import dev.abdallah.rhythm.ui.screen.artists.ArtistScreen
import dev.abdallah.rhythm.ui.screen.artists.Artists
import dev.abdallah.rhythm.ui.screen.folders.FolderScreen
import dev.abdallah.rhythm.ui.screen.folders.Folders
import dev.abdallah.rhythm.ui.screen.playlists.PlaylistScreen
import dev.abdallah.rhythm.ui.screen.playlists.Playlists
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.theme.RhythmTheme
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongFilter
import dev.abdallah.rhythm.ui.viewmodel.SongState
import dev.abdallah.rhythm.ui.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val categories = listOf("Songs", "Playlists", "Folders", "Artists", "Albums")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SongViewModel by viewModels()
    private var isServiceRunning = false

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rememberPermissionState(
                    permission = READ_MEDIA_AUDIO,
                )
            } else {
                rememberPermissionState(
                    permission = READ_EXTERNAL_STORAGE
                )
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(key1 = lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        permissionState.launchPermissionRequest()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            startService()
            RhythmTheme {
                DynamicColors.applyToActivityIfAvailable(this@MainActivity)
                val state by viewModel.state.collectAsState()
                Home(state = state, onEvent = { viewModel.onEvent(it) })
            }
        }
    }


    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, PlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Home(
        state: SongState, onEvent: (SongEvent) -> Unit
    ) {
        val navController = rememberNavController()
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(state.screen) {
            snapshotFlow { state.screen }.distinctUntilChanged().collect { screen ->
                val currentRoute = navController.currentDestination?.route
                if (screen.route != currentRoute) {
                    if (screen.route == Screen.HOME.route) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(screen.route)
                    }
                }
            }
        }
        NavHost(navController = navController, startDestination = "home", enterTransition = {
            fadeIn(animationSpec = tween(250))
        }, exitTransition = { fadeOut(animationSpec = tween(250)) }) {

            composable(Screen.HOME.route) {
                MainScreen(
                    state = state,
                )
            }

            composable(Screen.PLAYLIST.route) {
                PlaylistScreen(
                    state = state,
                    onEvent = { onEvent(it) },
                )
            }

            composable(Screen.FOLDER.route) {
                FolderScreen(state = state, onEvent = { onEvent(it) })
            }

            composable(Screen.ARTIST.route) {
                ArtistScreen(state = state, onEvent = { onEvent(it) })
            }

            composable(Screen.ALBUM.route) {
                AlbumScreen(state = state, onEvent = { onEvent(it) })
            }
        }

        onBackPressedDispatcher.addCallback {
            if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                }
            } else if (state.screen.route != Screen.HOME.route) {
                navController.popBackStack()
            } else {
                finish()
            }
        }

        BottomSheet(state, bottomSheetScaffoldState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomSheet(state: SongState, bottomSheetScaffoldState: BottomSheetScaffoldState) {
        val defaultPrimaryColor = Color.White.toArgb()
        val defaultAccentColor = Color.Gray.toArgb()
        val defaultSheetContainerColor = Surface.toArgb()

        val artworkPath = state.queue.getOrNull(state.index)?.artwork

        val artworkPalette = if (!artworkPath.isNullOrBlank()) {
            Palette.from(BitmapFactory.decodeFile(artworkPath)).generate()
        } else {
            null
        }

        val primaryColor =
            artworkPalette?.getVibrantColor(defaultPrimaryColor) ?: defaultPrimaryColor
        val accentColor = artworkPalette?.getMutedColor(defaultAccentColor) ?: defaultAccentColor
        val sheetContainerColor = artworkPalette?.getDominantColor(defaultSheetContainerColor)
            ?: defaultSheetContainerColor
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Box(modifier = Modifier.fillMaxSize()) {
                    MiniPlayer(
                        state = state,
                        onEvent = { viewModel.onEvent(it) },
                        primaryColor = Color(primaryColor),
                        accentColor = Color(accentColor),
                        sheetContainerColor = Color(sheetContainerColor),
                        targetValue = bottomSheetScaffoldState.bottomSheetState.targetValue,
                    )
                }
            },
            sheetPeekHeight = 96.dp,
            sheetDragHandle = null,
            sheetContainerColor = Color(sheetContainerColor),
        ) {

        }
    }

    @Composable
    fun MainScreen(state: SongState) {
        // Main Content
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { categories.size }
        val lazyRowState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Background)
        ) {
            // Categories
            LazyRow(modifier = Modifier.padding(top = 48.dp), state = lazyRowState) {
                items(categories.size) { page ->
                    Category(
                        page = page, onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = page,
                                )
                            }
                        }, targetPage = pagerState.targetPage, categories = categories
                    )
                }
            }

            HorizontalPager(modifier = Modifier.weight(1f), state = pagerState) { page ->
                when (page) {
                    0 -> {
                        Songs(state = state, onEvent = { viewModel.onEvent(it) })
                    }

                    1 -> {
                        Playlists(state = state, onEvent = { viewModel.onEvent(it) })
                    }

                    2 -> {
                        Folders(state = state, onEvent = { viewModel.onEvent(it) })
                    }

                    3 -> {
                        Artists(state = state, onEvent = { viewModel.onEvent(it) })
                    }

                    4 -> {
                        Albums(state = state, onEvent = { viewModel.onEvent(it) })
                    }
                }
            }
        }
    }
}

enum class Screen(val route: String) {
    HOME("home"), FOLDER("folder"), ARTIST("artist"), ALBUM("album"), PLAYLIST("playlist")
}