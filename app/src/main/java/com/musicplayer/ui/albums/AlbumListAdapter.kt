package com.musicplayer.ui.albums

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.musicplayer.R
import com.musicplayer.models.Album
import com.musicplayer.utils.GeneralUtils.inflateBinding


class AlbumListAdapter(
  private val listener: Listener
) : ListAdapter<Album, AlbumViewHolder>(AlbumItemDiffUtil) {

  interface Listener {
    fun onAlbumClick(view: View, album: Album)

    fun onAlbumPlayClick(album: Album)

    fun onAlbumLongClick(view: View, album: Album)

    fun onAlbumMenuClick(album: Album)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
    return AlbumViewHolder(parent.inflateBinding(R.layout.item_album), listener)
  }

  override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  object AlbumItemDiffUtil : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
      return oldItem == newItem
    }
  }

}

