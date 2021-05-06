package com.musicplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.models.Album
import com.musicplayer.models.Artist
import com.musicplayer.repository.ArtistsRepository
import com.musicplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class ArtistViewModel(
    private val repository: ArtistsRepository
) : CoroutineViewModel(Main) {

    private val artistsData: MutableLiveData<List<Artist>> = MutableLiveData()
    private val albumLiveData = MutableLiveData<List<Album>>()

    fun update() {
        launch {
            val artists = withContext(IO){
                repository.getAllArtist()
            }
            artistsData.postValue(artists)
        }
    }

    fun getArtists(): LiveData<List<Artist>> {
        update()
        return artistsData
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        launch {
            val albums = withContext(IO) {
                repository.getAlbumsForArtist(artistId)
            }
            albumLiveData.postValue(albums)
        }
        return albumLiveData
    }

    fun getArtist(id: Long): Artist {
        return repository.getArtist(id)
    }
}
