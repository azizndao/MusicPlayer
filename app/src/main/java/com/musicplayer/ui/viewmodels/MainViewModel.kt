package com.musicplayer.ui.viewmodels

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.os.bundleOf
import com.musicplayer.R
import com.musicplayer.databinding.ActivityMainBinding
import com.musicplayer.extensions.filter
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.playback.PlaybackConnection
import com.musicplayer.repository.AlbumsRepository
import com.musicplayer.repository.ArtistsRepository
import com.musicplayer.repository.FavoritesRepository
import com.musicplayer.repository.SongsRepository
import com.musicplayer.ui.viewmodels.base.CoroutineViewModel
import com.musicplayer.utils.Constants.PLAY_SONG_FROM_INTENT
import com.musicplayer.utils.Constants.QUEUE_LIST_TYPE_KEY
import com.musicplayer.utils.Constants.SONG_KEY
import com.musicplayer.utils.Constants.UPDATE_QUEUE
import com.musicplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import com.musicplayer.utils.SettingsUtility.Companion.QUEUE_LIST_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MainViewModel(
  private val favoritesRepository: FavoritesRepository,
  private val albumsRepository: AlbumsRepository,
  private val songsRepository: SongsRepository,
  private val artistsRepository: ArtistsRepository,
  private val playbackConnection: PlaybackConnection,
  private val favoriteViewModel: FavoriteViewModel
) : CoroutineViewModel(Main) {

  lateinit var binding: ActivityMainBinding
  val albums = flow {
    val albums = withContext(Dispatchers.IO) {
      albumsRepository.getAlbums()
    }
    emit(albums)
  }
  val songs = flow {
    val albums = withContext(Dispatchers.IO) {
      songsRepository.getSongs()
    }
    emit(albums)
  }
  val artists = flow {
    val albums = withContext(Dispatchers.IO) {
      artistsRepository.getAllArtist()
    }
    emit(albums)
  }

  fun mediaItemClicked(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle? = null) {
    transportControls()?.playFromMediaId(mediaItem.mediaId, extras)
  }

  fun mediaItemClickFromIntent(context: Context, song: Song) {
    transportControls() ?: playbackConnection.isConnected.filter { it }.observeForever {
      mediaItemClickFromIntent(context, song)
    }
    transportControls()?.sendCustomAction(
      PLAY_SONG_FROM_INTENT,
      bundleOf(
        SONG_KEY to song.toString(),
        QUEUE_INFO_KEY to context.getString(R.string.others)
      )
    )
  }

  fun transportControls() = playbackConnection.transportControls

  fun reloadQueueIds(ids: LongArray, type: String) {
    transportControls()?.sendCustomAction(
      UPDATE_QUEUE,
      bundleOf(QUEUE_LIST_KEY to ids, QUEUE_LIST_TYPE_KEY to type)
    )
  }

  fun getAlbum(albumId: Long): Album {
    return albumsRepository.getAlbum(albumId)
  }
}

