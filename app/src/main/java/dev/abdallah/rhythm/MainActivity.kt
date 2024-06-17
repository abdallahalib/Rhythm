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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import dev.abdallah.rhythm.player.service.PlaybackService
import dev.abdallah.rhythm.ui.component.Category
import dev.abdallah.rhythm.ui.component.MiniPlayer
import dev.abdallah.rhythm.ui.screen.AlbumView
import dev.abdallah.rhythm.ui.screen.Albums
import dev.abdallah.rhythm.ui.screen.ArtistView
import dev.abdallah.rhythm.ui.screen.Artists
import dev.abdallah.rhythm.ui.screen.FolderView
import dev.abdallah.rhythm.ui.screen.Folders
import dev.abdallah.rhythm.ui.screen.PlaylistView
import dev.abdallah.rhythm.ui.screen.Playlists
import dev.abdallah.rhythm.ui.screen.Songs
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.theme.RhythmTheme
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongViewModel
import dev.abdallah.rhythm.ui.viewmodel.UIEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.random.Random

private val categories = listOf("Songs", "Playlists", "Folders", "Artists", "Albums")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SongViewModel by viewModels()
    private var isServiceRunning = false
    private var maxOffset = -1.0f

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
            if (permissionState.status == PermissionStatus.Granted) {
                viewModel.refreshData()
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
            RhythmTheme {
                DynamicColors.applyToActivityIfAvailable(this@MainActivity)
                Home()
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
    @Preview
    @Composable
    fun Home() {
        val coroutineScope = rememberCoroutineScope()
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val navController = rememberNavController()

        LaunchedEffect(navController) {
            snapshotFlow { navController.currentDestination?.label }.collect {
                if (it == "home") {
                    coroutineScope.launch {
                        viewModel.setMediaItemList(viewModel.songs)
                    }
                }
            }
        }

        LaunchedEffect(viewModel.nowPlaying) {
            snapshotFlow { viewModel.nowPlaying }.distinctUntilChanged().collect {
                startService()
            }
        }

        onBackPressedDispatcher.addCallback {
            if (bottomSheetScaffoldState.bottomSheetState.requireOffset() == 0.0f) {
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                }
            } else {
                if (navController.currentDestination?.route != "home") {
                    navController.popBackStack()
                } else {
                    isEnabled = false
                    finish()
                }
            }
        }

        NavHost(navController = navController, startDestination = "home", enterTransition = {
            fadeIn(animationSpec = tween(250))
        }, exitTransition = { fadeOut(animationSpec = tween(250)) }) {

            composable("home") {
                MainScreen(
                    bottomSheetScaffoldState = bottomSheetScaffoldState,
                    navController = navController
                )
            }

            composable("playlist") {
                val playlistSongs = viewModel.getPlaylistSongs()
                PlaylistView(playlist = viewModel.selectedPlaylist,
                    songs = playlistSongs,
                    nowPlaying = viewModel.nowPlaying,
                    onItemClick = {
                        viewModel.setMediaItemList(playlistSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(it))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    onShuffle = {
                        viewModel.setMediaItemList(playlistSongs)
                        viewModel.onUiEvents(UIEvents.Shuffle(Random.nextInt(viewModel.getFolderSongs().size)))
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onPlay = {
                        viewModel.setMediaItemList(playlistSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(0))
                    })
            }

            composable("folder") {
                val folderSongs = viewModel.getFolderSongs()
                FolderView(folder = viewModel.selectedFolder,
                    songs = folderSongs,
                    nowPlaying = viewModel.nowPlaying,
                    onItemClick = {
                        viewModel.setMediaItemList(folderSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(it))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    onShuffle = {
                        viewModel.setMediaItemList(folderSongs)
                        viewModel.onUiEvents(UIEvents.Shuffle(Random.nextInt(viewModel.getFolderSongs().size)))
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onPlay = {
                        viewModel.setMediaItemList(folderSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(0))
                    })
            }

            composable("artist") {
                val artistSongs = viewModel.getArtistSongs()
                ArtistView(artist = viewModel.selectedArtist,
                    songs = artistSongs,
                    nowPlaying = viewModel.nowPlaying,
                    onItemClick = {
                        viewModel.setMediaItemList(artistSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(it))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    onShuffle = {
                        viewModel.setMediaItemList(artistSongs)
                        viewModel.onUiEvents(UIEvents.Shuffle(Random.nextInt(viewModel.getArtistSongs().size)))
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onPlay = {
                        viewModel.setMediaItemList(artistSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(0))
                    })
            }

            composable("album") {
                val albumSongs = viewModel.getAlbumSongs()
                AlbumView(album = viewModel.selectedAlbum,
                    songs = albumSongs,
                    nowPlaying = viewModel.nowPlaying,
                    onItemClick = {
                        viewModel.setMediaItemList(albumSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(it))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    },
                    onShuffle = {
                        viewModel.setMediaItemList(albumSongs)
                        viewModel.onUiEvents(UIEvents.Shuffle(Random.nextInt(viewModel.getAlbumSongs().size)))
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onPlay = {
                        viewModel.setMediaItemList(albumSongs)
                        viewModel.onUiEvents(UIEvents.ChangeSong(0))
                    })
            }
        }

        BottomSheet(bottomSheetScaffoldState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomSheet(bottomSheetScaffoldState: BottomSheetScaffoldState) {

        var bottomSheetOffset by remember { mutableFloatStateOf(1f) }

        LaunchedEffect(bottomSheetScaffoldState) {
            bottomSheetScaffoldState.bottomSheetState
            snapshotFlow { bottomSheetScaffoldState.bottomSheetState.requireOffset() }.collect { offset ->
                    if (maxOffset == -1.0f) {
                        maxOffset = offset
                    }
                    bottomSheetOffset = offset / maxOffset
                }
        }

        val artworkPalette = viewModel.nowPlaying.artworkLarge.takeIf { it.isNotEmpty() }
            ?.let { Palette.from(BitmapFactory.decodeFile(it)).generate() }
        val vibrantColor = artworkPalette?.getVibrantColor(Color.White.toArgb()) ?: Color.White.toArgb()
        val mutedColor = artworkPalette?.getMutedColor(Color.Gray.toArgb()) ?: Color.Gray.toArgb()
        val dominantColor = artworkPalette?.getDominantColor(Surface.toArgb()) ?: Surface.toArgb()
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Box(modifier = Modifier.fillMaxSize()) {
                    MiniPlayer(offset = bottomSheetOffset,
                        progress = viewModel.progress,
                        mutedColor = Color(mutedColor),
                        vibrantColor = Color(vibrantColor),
                        onProgress = {
                            viewModel.onUiEvents(UIEvents.SeekTo(it))
                        },
                        isPlaying = viewModel.isPlaying,
                        nowPlaying = viewModel.nowPlaying,
                        onStart = {
                            viewModel.onUiEvents(UIEvents.PlayPause)
                        },
                        onNext = {
                            viewModel.onUiEvents(UIEvents.Next)
                        },
                        onPrevious = {
                            viewModel.onUiEvents(UIEvents.Previous)
                        },
                        isFavorite = viewModel.favorites.contains(viewModel.nowPlaying),
                        onFavorite = {
                            viewModel.onFavorite(it)
                        },
                    )
                }
            },
            sheetPeekHeight = 96.dp,
            sheetDragHandle = null,
            sheetContainerColor = Color(dominantColor),
        ) {

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(
        bottomSheetScaffoldState: BottomSheetScaffoldState, navController: NavHostController
    ) {
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
                        Songs(songs = viewModel.songs,
                            nowPlaying = viewModel.nowPlaying,
                            onItemClick = {
                                viewModel.setMediaItemList(viewModel.songs)
                                viewModel.onUiEvents(UIEvents.ChangeSong(it))
                                coroutineScope.launch(Dispatchers.Default) {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                            },
                            onShuffle = {
                                viewModel.onUiEvents(UIEvents.Shuffle(Random.nextInt(viewModel.songs.size)))
                            })
                    }

                    1 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Playlists(
                                playlists = viewModel.playlists,
                                onItemClick = {
                                viewModel.selectPlaylist(viewModel.playlists[it])
                                navController.navigate("playlist")
                                },
                                onNewPlaylist = {
                                    viewModel.newPlaylist(it)
                                }
                            )
                        }
                    }

                    2 -> {
                        Folders(folderList = viewModel.folderList, onItemClick = {
                            viewModel.selectFolder(viewModel.folderList[it])
                            navController.navigate("folder")
                        })
                    }

                    3 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Artists(artistList = viewModel.artistList, onItemClick = {
                                viewModel.selectArtist(viewModel.artistList[it])
                                navController.navigate("artist")
                            })
                        }
                    }

                    4 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Albums(albumsList = viewModel.albumList, onItemClick = {
                                viewModel.selectAlbum(viewModel.albumList[it])
                                navController.navigate("album")
                            })
                        }
                    }
                }
            }
        }
    }
}