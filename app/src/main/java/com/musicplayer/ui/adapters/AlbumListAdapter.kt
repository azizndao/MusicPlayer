package com.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.databinding.ItemAlbumBinding
import com.musicplayer.models.Album


class AlbumListAdapter : ListAdapter<Album, AlbumViewHolder>(AlbumItemDiffUtil) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
    return AlbumViewHolder.create(parent)
  }

  override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

class AlbumViewHolder(private val binding: ItemAlbumBinding) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Album) {
    with(binding) {
      album = data
      root.setOnClickListener { }
    }
  }

  companion object {
    fun create(parent: ViewGroup): AlbumViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      return AlbumViewHolder(ItemAlbumBinding.inflate(inflater, parent, false))
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
