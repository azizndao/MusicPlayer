package com.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.databinding.ItemAlbumBinding
import com.musicplayer.models.Album


interface AlbumListListener {
  fun onAlbumClick(album: Album)

  fun onAlbumPlayClick(album: Album)

  fun onAlbumLongClick(album: Album)

  fun onAlbumMenuClick(album: Album)
}

class AlbumListAdapter(private val listener: AlbumListListener) :
  ListAdapter<Album, AlbumViewHolder>(AlbumItemDiffUtil) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
    return AlbumViewHolder.create(parent, listener)
  }

  override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class AlbumViewHolder(
  private val binding: ItemAlbumBinding,
  private val listener: AlbumListListener
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Album) {
    with(binding) {
      album = data
      root.also {
        it.setOnClickListener { listener.onAlbumClick(data) }
        it.setOnLongClickListener { listener.onAlbumLongClick(data); true }
      }
      btnPlay.setOnClickListener { listener.onAlbumPlayClick(data) }
      btnMore.setOnClickListener { listener.onAlbumMenuClick(data) }
    }
  }

  companion object {
    fun create(parent: ViewGroup, listener: AlbumListListener): AlbumViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      return AlbumViewHolder(ItemAlbumBinding.inflate(inflater, parent, false), listener)
    }
  }
}

object AlbumItemDiffUtil : DiffUtil.ItemCallback<Album>() {
  override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
    return oldItem == newItem
  }
}
