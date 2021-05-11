package com.musicplayer.viewmodels

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.musicplayer.R
import com.musicplayer.extensions.filter
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.playback.PlaybackConnection
import com.musicplayer.repository.AlbumsRepository
import com.musicplayer.repository.ArtistsRepository
import com.musicplayer.repository.SongsRepository
import com.musicplayer.utils.Constants
import com.musicplayer.utils.Constants.PLAY_SONG_FROM_INTENT
import com.musicplayer.utils.Constants.QUEUE_LIST_TYPE_KEY
import com.musicplayer.utils.Constants.SONG_KEY
import com.musicplayer.utils.Constants.UPDATE_QUEUE
import com.musicplayer.utils.GeneralUtils
import com.musicplayer.utils.GeneralUtils.getExtraBundle
import com.musicplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import com.musicplayer.utils.SettingsUtility.Companion.QUEUE_LIST_KEY
import com.musicplayer.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class MainViewModel(
  private val albumsRepository: AlbumsRepository,
  private val songsRepository: SongsRepository,
  private val artistsRepository: ArtistsRepository,
  private val playbackConnection: PlaybackConnection,
) : CoroutineViewModel(Main) {

  fun getAllAlbums() = liveData(Dispatchers.IO) { emit(albumsRepository.getAlbums()) }

  fun getAlbum(albumId: Long): Album {
    return albumsRepository.getAlbum(albumId)
  }

  fun getAllSongs() = liveData(Dispatchers.IO) { emit(songsRepository.getSongs()) }

  fun getAllArtists() = liveData(Dispatchers.IO) { emit(artistsRepository.getAllArtist()) }

  fun getSongsByAlbum(id: Long): LiveData<List<Song>> {
    return liveData(Dispatchers.IO) { emit(albumsRepository.getSongsForAlbum(id)) }
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

  fun playAlbum(album: Album) {
    launch(Dispatchers.IO) {
      val songs = albumsRepository.getSongsForAlbum(album.id)
      val extras = getExtraBundle(songs.toIDList(), Constants.ALBUM_KEY)
      mediaItemClicked(songs.first().toMediaItem(), extras)
    }
  }

  fun getArtistAlbums(artistId: Long) = liveData(Dispatchers.IO) {
    emit(artistsRepository.getAlbumsForArtist(artistId))
  }
}

