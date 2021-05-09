package com.musicplayer.ui.songs

import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.databinding.ItemSongBinding
import com.musicplayer.models.Song
import com.musicplayer.utils.GeneralUtils

class SongViewHolder(
  private val binding: ItemSongBinding,
  private val listener: SongListAdapter.Listener,
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Song) {
    with(binding) {
      this.song = data
      duration.text = GeneralUtils.formatMilliseconds(data.duration.toLong())
      root.setOnClickListener { listener.onSongClick(data) }
      root.setOnLongClickListener { listener.onSongLongClick(data);true }
    }
  }
}