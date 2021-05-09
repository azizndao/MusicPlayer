package com.musicplayer

import android.content.ComponentName
import com.musicplayer.playback.PlaybackConnection
import com.musicplayer.playback.PlaybackConnectionImplementation
import com.musicplayer.playback.services.BeatPlayerService
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModel = module {
  single {
    val component = ComponentName(get(), BeatPlayerService::class.java)
    PlaybackConnectionImplementation(get(), component)
  } bind PlaybackConnection::class
}