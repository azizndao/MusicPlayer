package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.platform.MaterialElevationScale
import com.musicplayer.databinding.FragmentAlbumDetailsBinding
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Album
import com.musicplayer.models.Song
import com.musicplayer.ui.adapters.AlbumDetailElement
import com.musicplayer.ui.adapters.AlbumDetailsAdapter
import com.musicplayer.ui.adapters.AlbumDetailsListener
import com.musicplayer.ui.viewmodels.AlbumViewModel
import com.musicplayer.ui.viewmodels.MainViewModel
import com.musicplayer.utils.Constants
import com.musicplayer.utils.GeneralUtils.getExtraBundle
import org.koin.android.ext.android.inject

class AlbumDetailsFragment : Fragment(), AlbumDetailsListener {

  private lateinit var songs: List<Song>
  private lateinit var binding: FragmentAlbumDetailsBinding
  private val args by navArgs<AlbumDetailsFragmentArgs>()
  private val albumViewModel by inject<AlbumViewModel>()
  private val mainViewModel by inject<MainViewModel>()
  private val album by lazy { albumViewModel.getAlbum(args.albumId) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialElevationScale(true)
    exitTransition = MaterialElevationScale(false)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      this.album = album
      elements.adapter = AlbumDetailsAdapter(this@AlbumDetailsFragment).also { adapter ->
        subscribeUi(adapter)
      }
    }
  }

  private fun subscribeUi(adapter: AlbumDetailsAdapter) {
    albumViewModel.getSongsByAlbum(args.albumId).observe(viewLifecycleOwner) { songs ->
      this.songs = songs
      val details = mutableListOf<AlbumDetailElement>(AlbumDetailElement.Album(album))
      details.addAll(songs.map { AlbumDetailElement.Song(it) })
      adapter.submitList(details)
    }
  }

  override fun onPlayAllClick(album: Album) {
    val extras = getExtraBundle(songs.toIDList(), Constants.ALBUM_KEY)
    mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
  }

  override fun onPlayRandomlyClick(album: Album) {
    val extras = getExtraBundle(songs.toIDList(), album.title)
    mainViewModel.transportControls()?.sendCustomAction(Constants.PLAY_ALL_SHUFFLED, extras)
  }

  override fun onSongClick(song: Song) {
    val extras = getExtraBundle(songs.toIDList(), album.title)
    mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
  }

  override fun onSongLongClick(song: Song) {
  }
}