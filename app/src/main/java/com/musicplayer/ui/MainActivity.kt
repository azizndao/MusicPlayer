package com.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.databinding.DataBindingUtil
import com.crrl.beatplayer.ui.viewmodels.SongDetailViewModel
import com.musicplayer.R
import com.musicplayer.databinding.ActivityMainBinding
import com.musicplayer.extensions.shortToast
import com.musicplayer.viewmodels.MainViewModel
import com.musicplayer.viewmodels.SongViewModel
import com.musicplayer.utils.BaseActivity
import com.musicplayer.utils.Constants.BIND_STATE_BOUND
import com.musicplayer.utils.SettingsUtility
import com.musicplayer.utils.TagUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

  private val songViewModel by inject<SongViewModel>()
  private val songDetailViewModel by inject<SongDetailViewModel>()
  private val settingsUtility by inject<SettingsUtility>()

  private val viewModel by viewModel<MainViewModel>()
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    if (didPermissionsGrant()) finishCreatingView()
  }

  override fun onStop() {
    super.onStop()
    settingsUtility.didStop = true
  }

  private fun finishCreatingView() {
    songDetailViewModel.currentState.observe(this) {
      songDetailViewModel.update(it.position)
      if (it.state == PlaybackStateCompat.STATE_PLAYING) {
        songDetailViewModel.update(BIND_STATE_BOUND)
      } else songDetailViewModel.update()
    }

    handlePlaybackIntent(intent)
  }

  private fun handlePlaybackIntent(intent: Intent?) {
    intent?.action ?: return

    when (intent.action!!) {
      Intent.ACTION_VIEW -> {
        val path = intent.data?.path ?: return
        // Some file managers still send a file path instead of media uri,
        // so data needs to be verified.
        settingsUtility.intentPath = path
        val song =
          if (path.contains(Regex("(\\.[mM][pP]3)|(\\.[mM]4[aA])|(\\.[oO][gG][gG])"))) {
            // Get song from a path
            TagUtils.readTagsAsSong(this, path)
          } else {
            // Get song by id if it is a media uri
            // The last part of an URI is the id, so just need to get it
            songViewModel.getSongById(path.split("/").last().toLong())
          }
        if (song.id == -1L) {
          shortToast(
            getString(R.string.cannot_read_file, song.path),
          )
        } else viewModel.mediaItemClickFromIntent(this, song)
      }
    }
  }

  private fun didPermissionsGrant() = permissionsGranted
}