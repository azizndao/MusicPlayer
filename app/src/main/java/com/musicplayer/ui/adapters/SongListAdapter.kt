package com.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.R
import com.musicplayer.databinding.ItemSongBinding
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.utils.GeneralUtils

interface SongListListener {
  fun onSongClick(song: Song)
  fun onSongLongClick(song: Song)
}

class SongListAdapter(
  private val listener: SongListListener
) : ListAdapter<Song, SongViewHolder>(SongDiffUtils) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
    return SongViewHolder.create(parent, listener)
  }

  override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}

object SongDiffUtils : DiffUtil.ItemCallback<Song>() {
  override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
}

class SongViewHolder(
  private val binding: ItemSongBinding,
  private val onClick: (Song) -> Unit,
  private val onLongClick: (Song) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Song) {
    with(binding) {
      this.song = data
      duration.text = GeneralUtils.formatMilliseconds(data.duration.toLong())
      root.setOnClickListener { onClick(data) }
      root.setOnLongClickListener { onLongClick(data);true }
    }
  }

  companion object {
    fun create(parent: ViewGroup, listener: SongListListener): SongViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      return SongViewHolder(
        ItemSongBinding.inflate(inflater, parent, false),
        onClick = listener::onSongClick,
        onLongClick = listener::onSongLongClick
      )
    }
  }
}