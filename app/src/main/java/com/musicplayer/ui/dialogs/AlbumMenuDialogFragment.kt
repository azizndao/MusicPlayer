package com.musicplayer.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialElevationScale
import com.musicplayer.databinding.FragmentAlbumMenuDialogBinding
import com.musicplayer.viewmodels.AlbumViewModel
import com.musicplayer.utils.GeneralUtils.generatePaletteFromAlbumArt
import com.musicplayer.utils.sortToast
import org.koin.android.ext.android.inject

class AlbumMenuDialogFragment : AppCompatDialogFragment(),
  NavigationView.OnNavigationItemSelectedListener {

  private lateinit var binding: FragmentAlbumMenuDialogBinding
  private val albumViewModel by inject<AlbumViewModel>()
  private val args by navArgs<AlbumMenuDialogFragmentArgs>()
  private val mAlbum by lazy { albumViewModel.getAlbum(args.albumId) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialElevationScale(true)
    exitTransition = MaterialElevationScale(false)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAlbumMenuDialogBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      album = mAlbum
      btnClose.setOnClickListener { findNavController().popBackStack() }
      options.setNavigationItemSelectedListener(this@AlbumMenuDialogFragment)
      generatePaletteFromAlbumArt(mAlbum.id) { palette ->
        palette ?: return@generatePaletteFromAlbumArt root.sortToast("Palette can't be generated")
        palette.dominantSwatch?.let { swatch ->
          btnClose.setColorFilter(swatch.bodyTextColor)
          headerBg.setBackgroundColor(swatch.rgb)
          title.setTextColor(swatch.bodyTextColor)
          artist.setTextColor(swatch.bodyTextColor)
        }
      }
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    dismiss()
    return true
  }
}