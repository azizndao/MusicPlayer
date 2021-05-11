package com.musicplayer.playback.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.core.os.bundleOf
import androidx.media.session.MediaButtonReceiver
import com.crrl.beatplayer.playback.receivers.BecomingNoisyReceiver
import com.musicplayer.playback.services.base.CoroutineService
import com.google.gson.Gson
import com.musicplayer.R
import com.musicplayer.extensions.*
import com.musicplayer.models.MediaId
import com.musicplayer.models.MediaId.Companion.CALLER_OTHER
import com.musicplayer.models.MediaId.Companion.CALLER_SELF
import com.musicplayer.models.QueueInfo
import com.musicplayer.notifications.Notifications
import com.musicplayer.playback.players.BeatPlayer
import com.musicplayer.repository.*
import com.musicplayer.utils.Constants
import com.musicplayer.utils.Constants.BY_UI_KEY
import com.musicplayer.utils.Constants.NEXT
import com.musicplayer.utils.Constants.NOTIFICATION_ID
import com.musicplayer.utils.Constants.PAUSE_ACTION
import com.musicplayer.utils.Constants.PLAY_ACTION
import com.musicplayer.utils.Constants.PLAY_PAUSE
import com.musicplayer.utils.Constants.PREVIOUS
import com.musicplayer.utils.Constants.SONG_ID_DEFAULT
import com.musicplayer.utils.SettingsUtility
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class BeatPlayerService : CoroutineService(Main), KoinComponent {

    companion object {
        var IS_RUNNING = false
    }

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private val beatPlayer by inject<BeatPlayer>()
    private val notifications by inject<Notifications>()
    private val songsRepository by inject<SongsRepository>()
    private val albumsRepository by inject<AlbumsRepository>()
    private val foldersRepository by inject<FoldersRepository>()
    private val favoritesRepository by inject<FavoritesRepository>()
    private val playlistRepository by inject<PlaylistRepository>()
    private val settingsUtility by inject<SettingsUtility>()

    override fun onCreate() {
        super.onCreate()

        sessionToken = beatPlayer.getSession().sessionToken
        becomingNoisyReceiver = BecomingNoisyReceiver(this, sessionToken!!)

        beatPlayer.onPlayingState { isPlaying, byUi ->
            if (isPlaying) {
                startForeground(NOTIFICATION_ID, notifications.buildNotification(getSession()))
                becomingNoisyReceiver.register()
            } else {
                becomingNoisyReceiver.unregister()
                saveCurrentData()
                stopForeground(byUi)
                if (!byUi) notifications.updateNotification(getSession())
            }
            IS_RUNNING = isPlaying
        }

        beatPlayer.onMetaDataChanged {
            notifications.updateNotification(getSession())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }

        val mediaSession = beatPlayer.getSession()
        val controller = mediaSession.controller

        when (intent.action) {
            PLAY_PAUSE -> {
                controller.playbackState?.let { playbackState ->
                    when {
                        playbackState.isPlaying -> controller.transportControls.sendCustomAction(
                            PAUSE_ACTION,
                            bundleOf(BY_UI_KEY to false)
                        )
                        playbackState.isPlayEnabled -> controller.transportControls.sendCustomAction(
                            PLAY_ACTION,
                            bundleOf(BY_UI_KEY to false)
                        )
                    }
                }
            }
            NEXT -> {
                controller.transportControls.skipToNext()
            }
            PREVIOUS -> {
                controller.transportControls.skipToPrevious()
            }
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_STICKY
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        launch {
            val itemList = withContext(IO) {
                loadChildren(parentId)
            }
            result.sendResult(itemList)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        val caller = if (clientPackageName == applicationContext.packageName) {
            CALLER_SELF
        } else {
            CALLER_OTHER
        }
        return BrowserRoot(MediaId("-1", null, caller).toString(), null)
    }

    private fun saveCurrentData() {
        launch(IO) {
            val mediaSession = beatPlayer.getSession()
            val controller = mediaSession.controller
            if (controller == null ||
                controller.playbackState == null ||
                controller.playbackState.state == STATE_NONE
            ) {
                return@launch
            }

            val id = controller.metadata?.getString(METADATA_KEY_MEDIA_ID)?.toLong() ?: 0
            if (id == SONG_ID_DEFAULT) return@launch

            val queueData = QueueInfo(
                id = id,
                seekPos = controller.playbackState?.position ?: 0,
                repeatMode = controller.repeatMode,
                shuffleMode = controller.shuffleMode,
                state = controller.playbackState?.state ?: STATE_NONE,
                name = controller.queueTitle?.toString() ?: getString(R.string.all_songs)
            )
            settingsUtility.currentQueueList =
                Gson().toJson(mediaSession.controller.queue.toIdList())
            settingsUtility.currentQueueInfo = Gson().toJson(queueData)
        }
    }

    private fun loadChildren(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        val list = mutableListOf<MediaBrowserCompat.MediaItem>()
        val mediaId = parentId.toMediaId()

        when (mediaId.type) {
            Constants.SONG_TYPE -> launch {
                list.addAll(songsRepository.loadSongs().toMediaItemList())
            }
            Constants.ALBUM_TYPE -> launch {
                list.addAll(
                    albumsRepository.getSongsForAlbum(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
            Constants.PLAY_LIST_TYPE -> launch {
                list.addAll(
                    playlistRepository.getSongsInPlaylist(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
            Constants.FOLDER_TYPE -> launch {
                val ids = Gson().fromJson<LongArray>(mediaId.caller ?: "{}")
                list.addAll(
                    foldersRepository.getSongs(ids).toMediaItemList()
                )
            }
            Constants.FAVORITE_TYPE -> launch {
                list.addAll(
                    favoritesRepository.getSongsForFavorite(mediaId.caller?.toLong() ?: 0)
                        .toMediaItemList()
                )
            }
        }
        return list
    }
}