package com.musicplayer.models

import android.database.Cursor
import com.musicplayer.utils.Constants.ARTIST_TYPE
import com.google.gson.Gson

data class Artist(
  var id: Long = 0,
  var albumId: Long = 0,
  var name: String = "",
  var albumCount: Int = 0,
  var songCount: Int = 0
) : MediaItem(id) {

  companion object {
    fun createFromCursor(cursor: Cursor): Artist {
      return Artist(
        cursor.getLong(0),
        cursor.getLong(1),
        cursor.getString(2),
        songCount = cursor.getInt(3)
      )
    }
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  fun toFavorite(): Favorite {
    return Favorite(id, name, name, albumId, 0, albumCount, ARTIST_TYPE)
  }
}