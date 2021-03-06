@file:Suppress("DEPRECATION")

package com.musicplayer.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.elevation.ElevationOverlayProvider
import com.musicplayer.R
import com.musicplayer.extensions.getColorByTheme

@BindingAdapter("peekHeight")
fun View.peekHeight(previous: Boolean = false, value: Boolean = false) {
  if (previous == value) return
  val behavior = BottomSheetBehavior.from(this)
  doOnApplyWindowInsets { _, insets, _, _, _ ->
    with(behavior) {
      setPeekHeight(
        insets.systemWindowInsetBottom + context.pixelsToDimen(64f).toInt()
      )
    }
  }
}

@BindingAdapter("albumArtFromId", "albumArtPlaceholder", "albumErrorDrawable", requireAll = false)
fun ImageView.loadAlbumArt(
  albumId: Long,
  placeholderDrawable: Drawable? = null,
  errorDrawable: Drawable? = null
) {
  val placeholder = placeholderDrawable
    ?: ColorDrawable(context.getColorByTheme(R.attr.colorControlHighlight))

  val error = errorDrawable
    ?: DrawableDecoderCompat.getDrawable(context, R.drawable.album_placeholder, context.theme)

  val uri = GeneralUtils.getAlbumArtUri(albumId)
  Glide.with(context).load(uri)
    .placeholder(placeholder)
    .error(error)
    .transition(DrawableTransitionOptions.withCrossFade())
    .into(this)
}

@BindingAdapter("showBackButton")
fun MaterialToolbar.bindBackButton(prevValue: Boolean, value: Boolean) {
  if (prevValue == value) return
  navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, context.theme)
  setNavigationOnClickListener { it.findNavController().popBackStack() }
}


@BindingAdapter(
  "popupElevationOverlay"
)
fun Spinner.bindPopupElevationOverlay(popupElevationOverlay: Float) {
  setPopupBackgroundDrawable(
    ColorDrawable(
      ElevationOverlayProvider(context)
        .compositeOverlayWithThemeSurfaceColorIfNeeded(popupElevationOverlay)
    )
  )
}

@BindingAdapter(
  "drawableStart",
  "drawableLeft",
  "drawableTop",
  "drawableEnd",
  "drawableRight",
  "drawableBottom",
  requireAll = false
)
fun TextView.bindDrawables(
  @DrawableRes drawableStart: Int? = null,
  @DrawableRes drawableLeft: Int? = null,
  @DrawableRes drawableTop: Int? = null,
  @DrawableRes drawableEnd: Int? = null,
  @DrawableRes drawableRight: Int? = null,
  @DrawableRes drawableBottom: Int? = null
) {
  setCompoundDrawablesWithIntrinsicBounds(
    context.getDrawableOrNull(drawableStart ?: drawableLeft),
    context.getDrawableOrNull(drawableTop),
    context.getDrawableOrNull(drawableEnd ?: drawableRight),
    context.getDrawableOrNull(drawableBottom)
  )
}

@BindingAdapter(
  "glideSrc",
  "glideCenterCrop",
  "glideCircularCrop",
  requireAll = false
)
fun ImageView.bindGlideSrc(
  @DrawableRes drawableRes: Int?,
  centerCrop: Boolean = false,
  circularCrop: Boolean = false
) {
  if (drawableRes == null) return

  createGlideRequest(
    context,
    drawableRes,
    centerCrop,
    circularCrop
  ).into(this)
}

private fun createGlideRequest(
  context: Context,
  @DrawableRes src: Int,
  centerCrop: Boolean,
  circularCrop: Boolean
): RequestBuilder<Drawable> {
  val req = Glide.with(context).load(src)
  if (centerCrop) req.centerCrop()
  if (circularCrop) req.circleCrop()
  return req
}

@BindingAdapter("goneIf")
fun View.bindGoneIf(gone: Boolean) {
  visibility = if (gone) {
    View.GONE
  } else {
    View.VISIBLE
  }
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
  if (previousFullscreen != fullscreen && fullscreen) {
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
  }
}

@BindingAdapter(
  "paddingLeftSystemWindowInsets",
  "paddingTopSystemWindowInsets",
  "paddingRightSystemWindowInsets",
  "paddingBottomSystemWindowInsets",
  requireAll = false
)
fun View.applySystemWindowInsetsPadding(
  previousApplyLeft: Boolean,
  previousApplyTop: Boolean,
  previousApplyRight: Boolean,
  previousApplyBottom: Boolean,
  applyLeft: Boolean,
  applyTop: Boolean,
  applyRight: Boolean,
  applyBottom: Boolean
) {
  if (previousApplyLeft == applyLeft &&
    previousApplyTop == applyTop &&
    previousApplyRight == applyRight &&
    previousApplyBottom == applyBottom
  ) {
    return
  }

  doOnApplyWindowInsets { view, insets, padding, _, _ ->
    val left = if (applyLeft) insets.systemWindowInsetLeft else 0
    val top = if (applyTop) insets.systemWindowInsetTop else 0
    val right = if (applyRight) insets.systemWindowInsetRight else 0
    val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

    view.setPadding(
      padding.left + left,
      padding.top + top,
      padding.right + right,
      padding.bottom + bottom
    )
  }
}

@BindingAdapter(
  "marginLeftSystemWindowInsets",
  "marginTopSystemWindowInsets",
  "marginRightSystemWindowInsets",
  "marginBottomSystemWindowInsets",
  requireAll = false
)
fun View.applySystemWindowInsetsMargin(
  previousApplyLeft: Boolean,
  previousApplyTop: Boolean,
  previousApplyRight: Boolean,
  previousApplyBottom: Boolean,
  applyLeft: Boolean,
  applyTop: Boolean,
  applyRight: Boolean,
  applyBottom: Boolean
) {
  if (previousApplyLeft == applyLeft &&
    previousApplyTop == applyTop &&
    previousApplyRight == applyRight &&
    previousApplyBottom == applyBottom
  ) {
    return
  }

  doOnApplyWindowInsets { view, insets, _, margin, _ ->
    val left = if (applyLeft) insets.systemWindowInsetLeft else 0
    val top = if (applyTop) insets.systemWindowInsetTop else 0
    val right = if (applyRight) insets.systemWindowInsetRight else 0
    val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
      leftMargin = margin.left + left
      topMargin = margin.top + top
      rightMargin = margin.right + right
      bottomMargin = margin.bottom + bottom
    }
  }
}

fun View.doOnApplyWindowInsets(
  block: (View, WindowInsets, InitialPadding, InitialMargin, Int) -> Unit
) {
  // Create a snapshot of the view's padding & margin states
  val initialPadding = recordInitialPaddingForView(this)
  val initialMargin = recordInitialMarginForView(this)
  val initialHeight = recordInitialHeightForView(this)
  // Set an actual OnApplyWindowInsetsListener which proxies to the given
  // lambda, also passing in the original padding & margin states
  setOnApplyWindowInsetsListener { v, insets ->
    block(v, insets, initialPadding, initialMargin, initialHeight)
    // Always return the insets, so that children can also use them
    insets
  }
  // request some insets
  requestApplyInsetsWhenAttached()
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
  view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
  val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
    ?: throw IllegalArgumentException("Invalid view layout params")
  return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

private fun recordInitialHeightForView(view: View): Int {
  return view.layoutParams.height
}

fun View.requestApplyInsetsWhenAttached() {
  if (isAttachedToWindow) {
    // We're already attached, just request as normal
    requestApplyInsets()
  } else {
    // We're not attached to the hierarchy, add a listener to
    // request when we are
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        v.requestApplyInsets()
      }

      override fun onViewDetachedFromWindow(v: View) = Unit
    })
  }
}