package com.musicplayer.ui.artists

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.musicplayer.R
import com.musicplayer.models.Artist
import com.musicplayer.utils.GeneralUtils.inflateBinding


class ArtistLitAdapter(
  private val listener: ArtistListFragment
) : ListAdapter<Artist, ArtistViewHolder>(ItemDiffUtil) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
    return ArtistViewHolder(parent.inflateBinding(R.layout.item_artist), listener)
  }

  override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  interface Listener {
    fun onArtistClick(view: View, artist: Artist)
    fun onArtistLongClick(view: View, artist: Artist)
    fun onArtistPlayClick(view: View, artist: Artist)
    fun onArtistMenuClick(view: View, artist: Artist)
  }

  object ItemDiffUtil : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Artist, newItem: Artist) = oldItem == newItem
  }
}

