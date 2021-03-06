package com.musicplayer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.models.SearchData
import com.musicplayer.repository.AlbumsRepository
import com.musicplayer.repository.ArtistsRepository
import com.musicplayer.repository.SongsRepository
import com.musicplayer.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SearchViewModel(
  private val songRepository: SongsRepository,
  private val albumRepository: AlbumsRepository,
  private val artistsRepository: ArtistsRepository
) : CoroutineViewModel(Main) {
  private val searchLiveData = MutableLiveData<SearchData>()
  private val searchData = SearchData()

  fun search(searchString: String) {
    if (searchString.isNotEmpty()) {
      launch {
        val songs = withContext(Dispatchers.IO) {
          songRepository.search(searchString, 10).toMutableList()
        }
        searchData.songList = songs
        searchLiveData.postValue(searchData)
      }

      launch {
        val albums = withContext(Dispatchers.IO) {
          albumRepository.search(searchString, 10).toMutableList()
        }
        searchData.albumList = albums
        searchLiveData.postValue(searchData)
      }

      launch {
        val artist = withContext(Dispatchers.IO) {
          artistsRepository.search(searchString, 10).toMutableList()
        }
        searchData.artistList = artist
        searchLiveData.postValue(searchData)
      }
    } else {
      searchLiveData.postValue(searchData.flush())
    }
  }

  fun searchLiveData(): LiveData<SearchData> {
    if (searchLiveData.value == null) searchLiveData.value = SearchData()
    return searchLiveData
  }
}