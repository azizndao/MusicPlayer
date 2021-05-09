package com.musicplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.musicplayer.R
import com.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
  }
}