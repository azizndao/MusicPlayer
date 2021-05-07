package com.musicplayer.ui.adapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.musicplayer.R
import com.musicplayer.utils.GeneralUtils

@BindingAdapter("albumArtFromId", "albumArtPlaceholder", requireAll = false)
fun ImageView.loadAlbumArt(albumId: Long, placeholder: Drawable? = null) {
  val drawable = placeholder ?: ResourcesCompat.getDrawable(
    resources,
    R.drawable.album_placeholder,
    context.theme
  )
  val uri = GeneralUtils.getAlbumArtUri(albumId)
  Glide.with(context).load(uri)
    .placeholder(drawable)
    .error(drawable)
    .into(this)
}

@BindingAdapter("showBackButton")
fun MaterialToolbar.bindBackButton(prevValue: Boolean, value: Boolean) {
  if (prevValue == value) return
  navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, context.theme)
  setNavigationOnClickListener { it.findNavController().popBackStack() }
}