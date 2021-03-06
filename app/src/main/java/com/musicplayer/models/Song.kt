package com.musicplayer.models

import android.database.Cursor
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.musicplayer.utils.Constants.FAVORITE_TYPE
import com.musicplayer.utils.Constants.SONG_ID_DEFAULT
import com.musicplayer.utils.Constants.SONG_TYPE
import com.musicplayer.utils.GeneralUtils.getAlbumArtUri
import com.musicplayer.utils.GeneralUtils.getSongUri
import com.musicplayer.extensions.fix
import com.musicplayer.repository.FavoritesRepositoryImplementation.Companion.COLUMN_FAVORITE
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ALBUM
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ALBUM_ID
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ARTIST
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ARTIST_ID
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_DURATION
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ID
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_PLAYLIST
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_TITLE
import com.musicplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_TRACK
import com.google.gson.Gson
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.FieldKey
import java.io.File

data class Song(
  val id: Long = -1,
  var albumId: Long = 0,
  var artistId: Long = 0,
  var title: String = "Title",
  val artist: String = "Artist",
  val album: String = "Album",
  val duration: Int = 0,
  val trackNumber: Int = 0,
  val path: String = "",
  var isFav: Boolean = false,
  var isSelected: Boolean = false,
  var playListId: Long = -1,
  var played: Boolean = false
) : MediaItem(id) {
  companion object {
    fun createFromCursor(cursor: Cursor, album_id: Long = 0): Song {
      return Song(
        id = cursor.getLong(0),
        title = cursor.getString(1),
        artist = cursor.getString(2),
        album = cursor.getString(3),
        duration = cursor.getInt(4),
        trackNumber = cursor.getInt(5).fix(),
        artistId = cursor.getLong(6),
        albumId = if (album_id == 0L) cursor.getLong(7) else album_id,
        path = if (album_id != 0L) cursor.getString(7) else cursor.getString(8)
      )
    }

    fun createFromPlaylistCursor(cursor: Cursor): Song {
      return Song(
        id = cursor.getLong(0),
        title = cursor.getString(1),
        artist = cursor.getString(2),
        album = cursor.getString(3),
        duration = cursor.getInt(4),
        trackNumber = cursor.getInt(5),
        artistId = cursor.getLong(6),
        albumId = cursor.getLong(7),
        playListId = cursor.getLong(8),
        path = getSongUri(cursor.getLong(0)).toString()
      )
    }

    fun createFromFolderCursor(cursor: Cursor): Song {
      return Song(
        id = cursor.getLong(0),
        title = cursor.getString(1),
        artist = cursor.getString(2),
        album = cursor.getString(3),
        duration = cursor.getInt(4),
        trackNumber = cursor.getInt(5),
        artistId = cursor.getLong(6),
        albumId = cursor.getLong(7),
        path = File(cursor.getString(8)).parent!!
      )
    }

    fun createFromAudioFile(audioFile: AudioFile): Song {
      return Song(
        id = SONG_ID_DEFAULT,
        title = audioFile.tag.getFirst(FieldKey.TITLE),
        artist = audioFile.tag.getFirst(FieldKey.ARTIST),
        album = audioFile.tag.getFirst(FieldKey.ALBUM),
        duration = audioFile.audioHeader.trackLength * 1000,
        trackNumber = try {
          audioFile.tag.getFirst(FieldKey.TRACK).toInt()
        } catch (ex: NumberFormatException) {
          -1
        },
        artistId = -1,
        albumId = -1,
        path = audioFile.file.path
      )
    }
  }

  override fun compare(other: MediaItem): Boolean {
    other as Song
    return id == other.id && title == other.title && artist == other.artist && album == other.album
        && duration == other.duration && trackNumber == other.trackNumber && artistId == other.artistId
        && albumId == other.albumId && path == other.path
  }


  fun columns(type: String): Array<String> {
    return arrayOf(
      COLUMN_ID,
      COLUMN_TITLE,
      COLUMN_ARTIST,
      COLUMN_ALBUM,
      COLUMN_DURATION,
      COLUMN_TRACK,
      COLUMN_ARTIST_ID,
      COLUMN_ALBUM_ID,
      when (type) {
        FAVORITE_TYPE -> COLUMN_FAVORITE
        else -> COLUMN_PLAYLIST
      }
    )
  }

  fun values(): Array<String> {
    return arrayOf(
      "$id",
      title,
      artist,
      album,
      "$duration",
      "$trackNumber",
      "$artistId",
      "$albumId",
      "$playListId"
    )
  }

  fun toMediaItem(): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(
      MediaDescriptionCompat.Builder()
        .setMediaId(MediaId(SONG_TYPE, id.toString(), null).toString())
        .setTitle(title)
        .setIconUri(getAlbumArtUri(albumId))
        .setSubtitle(artist)
        .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }
}
