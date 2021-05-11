package com.musicplayer.ui.albums

import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.databinding.ItemAlbumBinding
import com.musicplayer.models.Album

class AlbumViewHolder(
  private val binding: ItemAlbumBinding,
  private val listener: AlbumListAdapter.Listener
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Album) {
    with(binding) {
      album = data
      root.also {
        it.setOnClickListener { view -> listener.onAlbumClick(view, data) }
        it.setOnLongClickListener { view -> listener.onAlbumLongClick(view, data); true }
      }
      btnPlay.setOnClickListener { listener.onAlbumPlayClick(data) }
    }
  }
}