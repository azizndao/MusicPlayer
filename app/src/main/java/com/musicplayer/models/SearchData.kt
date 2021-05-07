package com.musicplayer.models

import com.musicplayer.utils.Constants.ALBUM_TYPE
import com.musicplayer.utils.Constants.ARTIST_TYPE

data class SearchData(
    var songList: MutableList<Song> = mutableListOf(),
    var albumList: MutableList<Album> = mutableListOf(),
    var artistList: MutableList<Artist> = mutableListOf()
) {

    fun isNotEmpty(): Boolean {
        return isNotSongListEmpty() || isNotAlbumListEmpty() || isNotArtistListEmpty()
    }

    fun isNotEmpty(type: String): Boolean {
        return when (type) {
            ALBUM_TYPE -> isNotAlbumListEmpty()
            ARTIST_TYPE -> isNotArtistListEmpty()
            else -> isNotSongListEmpty()
        }
    }

    fun isNotSongListEmpty(): Boolean {
        return songList.isNotEmpty()
    }

    fun isNotAlbumListEmpty(): Boolean {
        return albumList.isNotEmpty()
    }

    fun isNotArtistListEmpty(): Boolean {
        return artistList.isNotEmpty()
    }

    fun flush(): SearchData {
        return SearchData()
    }
}