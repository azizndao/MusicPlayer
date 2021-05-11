package com.musicplayer.ui.artists

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.databinding.ItemArtistBinding
import com.musicplayer.models.Album
import com.musicplayer.models.Artist
import com.musicplayer.utils.loadAlbumArt
import kotlinx.coroutines.delay

class ArtistViewHolder(
  private val binding: ItemArtistBinding,
  private val listener: ArtistListFragment
) : RecyclerView.ViewHolder(binding.root) {

  private val viewModel = listener.viewModel

  fun bind(data: Artist) {
    with(binding) {
      artist = data
      root.setOnClickListener { listener.onArtistClick(it, data) }
      root.setOnLongClickListener { listener.onArtistLongClick(it, data); true }
      btnPlay.setOnClickListener { listener.onArtistPlayClick(it, data) }
      viewModel.getArtistAlbums(data.id).observe(listener.viewLifecycleOwner) { albums ->
        animateImageChanged(albums)
      }
    }
  }

  private fun ItemArtistBinding.animateImageChanged(albums: List<Album>) {
    listener.lifecycleScope.launchWhenResumed {
      repeat(albums.size) {
        albums.forEach { album ->
          albumArts.loadAlbumArt(album.id)
          delay(3000)
        }
      }
    }
  }
}