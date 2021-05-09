package com.musicplayer.ui.home

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.musicplayer.ui.PlaylistListFragment
import com.musicplayer.ui.albums.AlbumListFragment
import com.musicplayer.ui.artists.ArtistListFragment
import com.musicplayer.ui.songs.SongListFragment


class HomeViewAdapter(fragment: HomeFragment) : FragmentStateAdapter(fragment) {

  private val fragments = listOf(
    PlaylistListFragment(),
    AlbumListFragment(),
    ArtistListFragment(),
    SongListFragment()
  )

  override fun getItemCount() = fragments.size

  override fun createFragment(position: Int) = fragments[position]
}