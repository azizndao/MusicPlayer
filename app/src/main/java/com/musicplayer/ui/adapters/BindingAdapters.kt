package com.musicplayer.ui.adapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.musicplayer.utils.GeneralUtils

@BindingAdapter("glideSrc", "albumArtPlaceholder", requireAll = false)
fun ImageView.loadAlbumArt(albumId: Long, placeholder: Drawable) {
  val uri = GeneralUtils.getAlbumArtUri(albumId)
  Glide.with(context).load(uri).placeholder(placeholder).into(this)
}

