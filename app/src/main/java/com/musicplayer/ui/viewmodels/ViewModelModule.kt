package com.musicplayer.ui.viewmodels

import com.crrl.beatplayer.ui.viewmodels.SettingsViewModel
import com.crrl.beatplayer.ui.viewmodels.SongDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
  single { MainViewModel(get(), get(), get(), get(), get(), get()) }
  viewModel { SongDetailViewModel(get(), get(), get()) }
  viewModel { SettingsViewModel(get()) }
  viewModel { PlaylistViewModel(get()) }
  viewModel { ArtistViewModel(get()) }
  viewModel { FavoriteViewModel(get()) }
  viewModel { AlbumViewModel(get()) }
  viewModel { SearchViewModel(get(), get(), get()) }
  viewModel { SongViewModel(get()) }
  viewModel { FolderViewModel(get()) }
}