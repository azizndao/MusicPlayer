package com.musicplayer.models

import android.content.Context
import com.musicplayer.utils.Constants.FOLDER_TYPE
import com.musicplayer.extensions.fixedName
import com.musicplayer.extensions.fixedPath
import com.google.gson.Gson
import java.io.File

class Folder(
  val id: Long = -1,
  val name: String = "",
  val albumId: Long = -1,
  val path: String = "",
  val ids: LongArray = longArrayOf()
) : MediaItem(id) {

  companion object {
    fun fromSong(song: Song, songs: LongArray, context: Context): Folder {
      return Folder(
        song.id,
        File(song.path).fixedName(context),
        song.albumId,
        File(song.path).fixedPath(context),
        songs
      )
    }
  }

  override fun compare(other: MediaItem): Boolean {
    other as Folder
    return id == other.id && name == other.name && albumId == other.albumId
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  fun toFavorite(): Favorite {
    return Favorite(id, name, path, albumId, 0, ids.size, FOLDER_TYPE)
  }
}