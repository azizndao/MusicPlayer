package com.musicplayer.ui.adapters

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.musicplayer.ui.fragments.*


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