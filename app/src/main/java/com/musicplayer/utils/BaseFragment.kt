package com.musicplayer.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialElevationScale

abstract class BaseFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialElevationScale(true)
    exitTransition = MaterialElevationScale(false)
  }
}