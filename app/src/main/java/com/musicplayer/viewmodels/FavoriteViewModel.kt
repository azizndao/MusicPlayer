package com.musicplayer.viewmodels

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.models.Favorite
import com.musicplayer.models.Song
import com.musicplayer.repository.FavoritesRepository
import com.musicplayer.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteViewModel(
  private val repository: FavoritesRepository
) : CoroutineViewModel(Dispatchers.Main) {

  private val favoriteListData = MutableLiveData<List<Favorite>>()
  private val songListData = MutableLiveData<List<Song>>()

  fun deleteFavorite(id: Long) {
    repository.deleteFavorites(longArrayOf(id))
  }

  fun deleteSongByFavorite(favoriteId: Long, ids: LongArray): Int {
    return repository.deleteSongByFavorite(favoriteId, ids)
  }

  fun songListFavorite(idFavorites: Long): LiveData<List<Song>> {
    launch {
      val songs = withContext(Dispatchers.IO) {
        repository.getSongsForFavorite(idFavorites)
      }
      songListData.postValue(songs)
    }
    return songListData
  }

  fun getFavorites(): LiveData<List<Favorite>> {
    launch {
      val favorites = withContext(Dispatchers.IO) {
        try {
          repository.getFavorites()
        } catch (ex: SQLiteException) {
          emptyList<Favorite>()
        } catch (ex: IllegalStateException) {
          emptyList<Favorite>()
        }
      }
      favoriteListData.postValue(favorites)
    }
    return favoriteListData
  }

  fun getFavorite(id: Long): Favorite {
    return repository.getFavorite(id)
  }

  fun deleteFavorites(ids: LongArray): Int {
    return repository.deleteFavorites(ids)
  }

  fun addToFavorite(favoriteId: Long, songs: List<Song>): Int {
    return repository.addSongByFavorite(favoriteId, songs)
  }

  fun remove(favoriteId: Long, ids: LongArray) {
    repository.deleteSongByFavorite(favoriteId, ids)
  }

  fun favExist(favoriteId: Long): Boolean {
    return repository.favExist(favoriteId)
  }

  fun songExist(songId: Long): Boolean {
    return repository.songExist(songId)
  }

  fun create(favorite: Favorite): Int {
    return repository.createFavorite(favorite)
  }

  fun update(parentId: Long, id: Long): Int {
    return repository.updateFavoriteCount(parentId, id)
  }
}