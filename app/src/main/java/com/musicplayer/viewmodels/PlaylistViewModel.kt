package com.musicplayer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.models.Playlist
import com.musicplayer.models.Song
import com.musicplayer.repository.PlaylistRepository
import com.musicplayer.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class PlaylistViewModel(
  private val repository: PlaylistRepository
) : CoroutineViewModel(Main) {

  private val playlistLiveData = MutableLiveData<List<Playlist>>()
  private val songsByPlaylist = MutableLiveData<List<Song>>()

  val count: Int
    get() = repository.getPlayListsCount()

  fun playLists(): LiveData<List<Playlist>> {
    launch {
      val playlists = withContext(Dispatchers.IO) {
        repository.getPlayLists()
      }
      playlistLiveData.postValue(playlists)
    }
    return playlistLiveData
  }

  fun getSongs(id: Long): LiveData<List<Song>> {
    launch {
      val songs = withContext(Dispatchers.IO) {
        repository.getSongsInPlaylist(id)
      }
      songsByPlaylist.postValue(songs)
    }
    return songsByPlaylist
  }

  fun remove(playListId: Long, id: Long) {
    repository.removeFromPlaylist(playListId, id)
  }

  fun exists(name: String): Boolean {
    return repository.existPlaylist(name)
  }

  fun create(name: String, songs: List<Song>): Long {
    return repository.createPlaylist(name, songs)
  }

  fun addToPlaylist(playListId: Long, song: List<Song>): Long {
    return repository.addToPlaylist(playListId, song)
  }

  fun getPlaylist(id: Long): Playlist {
    return repository.getPlaylist(id)
  }

  fun delete(id: Long): Int {
    return repository.deletePlaylist(id)
  }
}