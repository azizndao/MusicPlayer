package com.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.musicplayer.R
import com.musicplayer.databinding.ItemAlbumDescriptionBinding
import com.musicplayer.databinding.ItemSongBinding
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.utils.GeneralUtils

interface AlbumDetailsListener {
  fun onPlayAllClick(album: Album)
  fun onPlayRandomlyClick(album: Album)
  fun onSongClick(song: Song)
  fun onSongLongClick(song: Song)
}

class AlbumDetailsAdapter(private val listener: AlbumDetailsListener) :
  ListAdapter<AlbumDetailElement, AlbumDetailViewHolder>(AlbumDetailElement.DiffUtils) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumDetailViewHolder {
    return AlbumDetailViewHolder.create(parent, viewType, listener)
  }

  override fun onBindViewHolder(holder: AlbumDetailViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemViewType(position: Int): Int {
    return when (position) {
      0 -> R.layout.item_album_description
      else -> R.layout.item_song
    }
  }
}

sealed class AlbumDetailElement {

  data class Album(val data: com.musicplayer.models.Album) : AlbumDetailElement()

  data class Song(val data: com.musicplayer.models.Song) : AlbumDetailElement()

  object DiffUtils : DiffUtil.ItemCallback<AlbumDetailElement>() {
    override fun areItemsTheSame(
      oldItem: AlbumDetailElement,
      newItem: AlbumDetailElement
    ): Boolean {
      return oldItem is Album && newItem is Album ||
          (oldItem is Song && newItem is Song && oldItem.data.id == newItem.data.id)
    }

    override fun areContentsTheSame(
      oldItem: AlbumDetailElement,
      newItem: AlbumDetailElement
    ): Boolean {
      return oldItem == newItem
    }
  }
}

sealed class AlbumDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

  abstract fun bind(data: AlbumDetailElement)

  class SongViewHolder(
    private val binding: ItemSongBinding,
    private val onClick: (Song) -> Unit,
    private val onLongClick: (Song) -> Unit,
  ) : AlbumDetailViewHolder(binding.root) {

    override fun bind(data: AlbumDetailElement) {
      val song = (data as AlbumDetailElement.Song).data
      with(binding) {
        this.song = song
        duration.text = GeneralUtils.formatMilliseconds(song.duration.toLong())
        root.setOnClickListener { onClick(song) }
        root.setOnLongClickListener {
          onLongClick(song)
          true
        }
      }
    }
  }

  class DescriptionViewHolder(
    private val binding: ItemAlbumDescriptionBinding,
    private val onPlayAllClick: (Album) -> Unit,
    private val onPlayRandomlyClick: (Album) -> Unit,
  ) : AlbumDetailViewHolder(binding.root) {

    override fun bind(data: AlbumDetailElement) {
      val album = (data as AlbumDetailElement.Album).data
      with(binding) {
        setOnPlayAllClick { onPlayAllClick(album) }
        setOnPlayRandomlyClick { onPlayRandomlyClick(album) }
        this.album = album
      }
    }
  }

  companion object {
    fun create(
      parent: ViewGroup,
      viewType: Int,
      listener: AlbumDetailsListener
    ): AlbumDetailViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      return when (viewType) {
        R.layout.item_album_description -> DescriptionViewHolder(
          ItemAlbumDescriptionBinding.inflate(inflater, parent, false),
          onPlayAllClick = listener::onPlayAllClick,
          onPlayRandomlyClick = listener::onPlayRandomlyClick
        )
        R.layout.item_song -> SongViewHolder(
          ItemSongBinding.inflate(inflater, parent, false),
          onClick = listener::onSongClick,
          onLongClick = listener::onSongLongClick
        )
        else -> throw Exception()
      }
    }
  }
}