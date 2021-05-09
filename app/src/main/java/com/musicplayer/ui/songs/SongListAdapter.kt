package com.musicplayer.ui.songs

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.musicplayer.R
import com.musicplayer.models.Song
import com.musicplayer.utils.GeneralUtils.inflateBinding


class SongListAdapter(
  private val listener: Listener
) : ListAdapter<Song, SongViewHolder>(SongDiffUtils) {

  interface Listener {
    fun onSongClick(song: Song)
    fun onSongLongClick(song: Song)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
    return SongViewHolder(parent.inflateBinding(R.layout.item_song), listener)
  }

  override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  object SongDiffUtils : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
  }
}
