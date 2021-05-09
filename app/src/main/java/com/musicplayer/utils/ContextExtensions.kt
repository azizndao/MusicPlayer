package com.musicplayer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use

/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
  @AttrRes themeAttrId: Int
): Int {
  return obtainStyledAttributes(
    intArrayOf(themeAttrId)
  ).use {
    it.getColor(0, Color.MAGENTA)
  }
}

/**
 * Retrieve a style from the current [android.content.res.Resources.Theme].
 */
@StyleRes
fun Context.themeStyle(@AttrRes attr: Int): Int {
  val tv = TypedValue()
  theme.resolveAttribute(attr, tv, true)
  return tv.data
}

@SuppressLint("Recycle")
fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
  return AnimationUtils.loadInterpolator(
    this,
    obtainStyledAttributes(intArrayOf(attr)).use {
      it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
    }
  )
}

fun Context.getDrawableOrNull(@DrawableRes id: Int?): Drawable? {
  return if (id == null || id == 0) null else AppCompatResources.getDrawable(this, id)
}

fun Context.sortToast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun View.sortToast(text: String) {
  Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun View.longToast(text: String) {
  Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}