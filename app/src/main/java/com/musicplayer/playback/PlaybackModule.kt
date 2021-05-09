package com.musicplayer.playback

import com.musicplayer.playback.players.AudioPlayer
import com.musicplayer.playback.players.AudioPlayerImplementation
import com.musicplayer.playback.players.BeatPlayer
import com.musicplayer.playback.players.BeatPlayerImplementation
import org.koin.dsl.bind
import org.koin.dsl.module

val playbackModule = module {
  factory {
    AudioPlayerImplementation(get())
  } bind (AudioPlayer::class)

  factory {
    AudioFocusHelperImplementation(get())
  } bind (AudioFocusHelper::class)

  factory {
    BeatPlayerImplementation(
      get(),
      get(),
      get(),
      get(),
      get(),
      get()
    )
  } bind (BeatPlayer::class)
}