package com.musicplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.repository.AlbumsRepository
import com.musicplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class AlbumViewModel(
  private val repository: AlbumsRepository
) : CoroutineViewModel(Main) {
  private val albumData = MutableLiveData<List<Album>>()
  private val songsByAlbum = MutableLiveData<List<Song>>()

  fun getAlbum(id: Long): Album {
    return repository.getAlbum(id)
  }

  fun getAlbums(): LiveData<List<Album>> {
    update()
    return albumData
  }

  fun getSongsByAlbum(id: Long): LiveData<List<Song>> {
    launch {
      val list = withContext(IO) {
        repository.getSongsForAlbum(id)
      }
      songsByAlbum.postValue(list)
    }
    return songsByAlbum
  }

  fun update() {
    launch {
      val albums = withContext(IO) {
        repository.getAlbums()
      }
      albumData.postValue(albums)
    }
  }
}