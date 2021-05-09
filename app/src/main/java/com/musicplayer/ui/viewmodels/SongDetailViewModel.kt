/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.ui.viewmodels

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.musicplayer.models.MediaItemData
import com.musicplayer.models.PlaybackState
import com.musicplayer.models.Queue
import com.musicplayer.playback.PlaybackConnection
import com.musicplayer.repository.FavoritesRepository
import com.musicplayer.ui.viewmodels.base.CoroutineViewModel
import com.musicplayer.utils.Constants.BIND_STATE_BOUND
import com.musicplayer.utils.Constants.BIND_STATE_CANCELED
import com.musicplayer.utils.Constants.SET_MEDIA_STATE
import com.musicplayer.utils.GeneralUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongDetailViewModel(
  private val favoritesRepository: FavoritesRepository,
  mediaPlaybackConnection: PlaybackConnection,
  private val context: Context
) : CoroutineViewModel(Main) {

  private var state = BIND_STATE_CANCELED

  private val currentDataBase = MutableLiveData<MediaItemData>()
  val currentData: LiveData<MediaItemData> = currentDataBase

  private val currentStateBase = MutableLiveData<PlaybackState>()
  val currentState: LiveData<PlaybackState> = currentStateBase

  private val queueDataBase = MutableLiveData<Queue>()
  val queueData: LiveData<Queue> = queueDataBase

  private val timeLiveData = MutableLiveData<Int>()
  val time: LiveData<Int> = timeLiveData

  private val rawData = MutableLiveData<ByteArray>().apply { value = byteArrayOf() }
  val raw: LiveData<ByteArray> = rawData

  private val isNavBarShownData = MutableLiveData<Boolean>()
  val isNavBarShown: LiveData<Boolean>
    get() {
      launch {
        val isEnable = withContext(IO) {
          GeneralUtils.hasNavBar(context.resources)
        }
        isNavBarShownData.postValue(isEnable)
      }
      return isNavBarShownData
    }

  private val isSongFavLiveData = MutableLiveData<Boolean>()
  private val lyrics: MutableLiveData<String> = MutableLiveData()

  private val playbackStateObserver = Observer<PlaybackStateCompat> { playbackState ->
    playbackState?.let {
      currentStateBase.postValue(PlaybackState.pullPlaybackState(it))
    }
  }

  private val nowMediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetaData ->
    mediaMetaData?.let {
      currentDataBase.postValue(MediaItemData.pullMediaMetadata(it) ?: return@let)
    }
  }

  private val queueDataObserver = Observer<Queue> { queueData ->
    queueData?.let {
      queueDataBase.postValue(queueData)
    }
  }

  private val mediaMediaConnection = mediaPlaybackConnection.also {
    it.playbackState.observeForever(playbackStateObserver)
    it.nowPlaying.observeForever(nowMediaMetadataObserver)
    it.queueLiveData.observeForever(queueDataObserver)

    it.isConnected.observeForever { connected ->
      if (connected) {
        it.transportControls?.sendCustomAction(SET_MEDIA_STATE, null)
      }
    }
  }

  fun update(newTime: Int) {
    timeLiveData.postValue(newTime)
  }

  fun update(raw: ByteArray) {
    if (rawData.value == null) {
      rawData.postValue(raw)
    } else if (!rawData.value!!.contentEquals(raw)) rawData.postValue(raw)
  }

  fun update(bindState: String = BIND_STATE_CANCELED) {
    state = bindState
    if (state == BIND_STATE_BOUND) bindTime()
  }

  private fun bindTime() {
    GlobalScope.launch {
      while (true) {
        delay(100)
        mediaMediaConnection.mediaController ?: continue
        val newTime = mediaMediaConnection.mediaController?.playbackState!!.position
        if (state == BIND_STATE_BOUND) update(newTime.toInt())
        if (state == BIND_STATE_CANCELED) break
      }
    }
  }

  fun isSongFav(id: Long): LiveData<Boolean> {
    launch {
      val isFav = withContext(IO) {
        favoritesRepository.songExist(id)
      }
      isSongFavLiveData.postValue(isFav)
    }
    return isSongFavLiveData
  }

  fun getLyrics(): LiveData<String> {
    return lyrics
  }

  fun updateLyrics(lyric: String? = null) {
    lyrics.postValue(lyric)
  }

  override fun onCleared() {
    super.onCleared()
    mediaMediaConnection.playbackState.removeObserver(playbackStateObserver)
    mediaMediaConnection.nowPlaying.removeObserver(nowMediaMetadataObserver)
    mediaMediaConnection.queueLiveData.removeObserver(queueDataObserver)
  }
}