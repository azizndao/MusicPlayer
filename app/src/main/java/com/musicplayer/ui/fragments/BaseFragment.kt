package com.musicplayer.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.transition.platform.MaterialElevationScale

abstract class BaseFragment : Fragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialElevationScale(true)
    exitTransition = MaterialElevationScale(false)
  }
}