package com.musicplayer

import android.app.Application
import com.musicplayer.ui.viewmodels.viewModelModule
import com.musicplayer.utils.utilsModule
import com.musicplayer.notifications.notificationModule
import com.musicplayer.playback.playbackModule
import com.musicplayer.repository.repositoriesModule
import org.jaudiotagger.tag.TagOptionSingleton
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

  override fun onCreate() {
    super.onCreate()

    TagOptionSingleton.getInstance().isAndroid = true

    val modules = listOf(
      mainModel,
      notificationModule,
      playbackModule,
      repositoriesModule,
      viewModelModule,
      utilsModule
    )
    startKoin {
      androidContext(this@App)
      modules(modules)
    }
  }
}