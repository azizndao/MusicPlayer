package com.musicplayer.notifications

import android.app.*
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.core.graphics.toColorInt
import androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent
import androidx.palette.graphics.Palette
import com.musicplayer.utils.Constants.CHANNEL_ID
import com.musicplayer.utils.Constants.NEXT
import com.musicplayer.utils.Constants.NOTIFICATION_ID
import com.musicplayer.utils.Constants.PLAY_PAUSE
import com.musicplayer.utils.Constants.PREVIOUS
import com.musicplayer.utils.GeneralUtils.isOreo
import com.musicplayer.R
import com.musicplayer.extensions.isPlaying
import com.musicplayer.models.ExtraInfo
import com.musicplayer.playback.services.BeatPlayerService
import com.musicplayer.ui.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.media.app.NotificationCompat as NotificationMediaCompat

interface Notifications {
  fun updateNotification(mediaSession: MediaSessionCompat)
  fun buildNotification(mediaSession: MediaSessionCompat): Notification
  fun clearNotifications()
}

class NotificationsImplementation(
  private val context: Application,
  private val notificationManager: NotificationManager
) : Notifications {

  override fun updateNotification(mediaSession: MediaSessionCompat) {
    if (!BeatPlayerService.IS_RUNNING) return
    GlobalScope.launch {
      notificationManager.notify(NOTIFICATION_ID, buildNotification(mediaSession))
    }
  }

  override fun buildNotification(mediaSession: MediaSessionCompat): Notification {
    if (mediaSession.controller.metadata == null || mediaSession.controller.playbackState == null) {
      return createEmptyNotification()
    }

    val albumName = mediaSession.controller.metadata.getString(METADATA_KEY_ALBUM)
    val artistName = mediaSession.controller.metadata.getString(METADATA_KEY_ARTIST)
    val trackName = mediaSession.controller.metadata.getString(METADATA_KEY_TITLE)
    val artwork = mediaSession.controller.metadata.getBitmap(METADATA_KEY_ALBUM_ART)
    val isPlaying = mediaSession.isPlaying()
    val description = ExtraInfo.fromString(
      mediaSession.controller.metadata.getString(
        METADATA_KEY_DISPLAY_DESCRIPTION
      )
    )

    val playButtonResId = if (isPlaying) {
      R.drawable.ic_pause
    } else {
      R.drawable.ic_play
    }

    val nowPlayingIntent = Intent(context, MainActivity::class.java)
    val clickIntent =
      PendingIntent.getActivity(context, 0, nowPlayingIntent, FLAG_UPDATE_CURRENT)

    createNotificationChannel()

    val style = NotificationMediaCompat.MediaStyle()
      .setMediaSession(mediaSession.sessionToken)
      .setShowCancelButton(true)
      .setShowActionsInCompactView(0, 1, 2)
      .setCancelButtonIntent(buildMediaButtonPendingIntent(context, ACTION_STOP))

    val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
      setStyle(style)
      setSmallIcon(R.drawable.ic_library_music)
      setLargeIcon(artwork)
      setContentIntent(clickIntent)
      setContentTitle(trackName)
      setContentText(context.getString(R.string.with_separator, artistName, albumName))
      setSubText(description.queuePosition)
      setColorized(true)
      setShowWhen(false)
      setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      setDeleteIntent(buildMediaButtonPendingIntent(context, ACTION_STOP))
      addAction(getPreviousAction(context))
      addAction(getPlayPauseAction(context, playButtonResId))
      addAction(getNextAction(context))
    }

    if (artwork != null) {
      builder.color = Palette.from(artwork)
        .generate()
        .getVibrantColor("#403f4d".toColorInt())
    }

    return builder.build()
  }

  override fun clearNotifications() {
    notificationManager.cancel(NOTIFICATION_ID)
  }

  private fun getPreviousAction(context: Context): NotificationCompat.Action {
    val actionIntent = Intent(context, BeatPlayerService::class.java).apply {
      action = PREVIOUS
    }
    val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
    return NotificationCompat.Action(R.drawable.ic_previous, "", pendingIntent)
  }

  private fun getPlayPauseAction(
    context: Context,
    @IdRes playButtonResId: Int
  ): NotificationCompat.Action {
    val actionIntent = Intent(context, BeatPlayerService::class.java).apply {
      action = PLAY_PAUSE
    }
    val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
    return NotificationCompat.Action(playButtonResId, "", pendingIntent)
  }

  private fun getNextAction(context: Context): NotificationCompat.Action {
    val actionIntent = Intent(context, BeatPlayerService::class.java).apply {
      action = NEXT
    }
    val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
    return NotificationCompat.Action(R.drawable.ic_next, "", pendingIntent)
  }

  private fun createEmptyNotification(): Notification {
    createNotificationChannel()
    return NotificationCompat.Builder(context, CHANNEL_ID).apply {
      setSmallIcon(R.drawable.ic_music_note)
      setContentTitle(context.getString(R.string.app_name))
      setColorized(true)
      setShowWhen(false)
      setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }.build()
  }

  private fun createNotificationChannel() {
    if (!isOreo()) return
    val name = context.getString(R.string.app_name)
    val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_LOW).apply {
      description = context.getString(R.string.app_name)
      setShowBadge(false)
      lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    notificationManager.createNotificationChannel(channel)
  }
}