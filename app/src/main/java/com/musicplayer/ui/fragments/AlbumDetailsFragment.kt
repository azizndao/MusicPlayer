package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.musicplayer.R
import com.musicplayer.databinding.FragmentAlbumDetailsBinding
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Song
import com.musicplayer.ui.adapters.SongListAdapter
import com.musicplayer.ui.adapters.SongListListener
import com.musicplayer.ui.bottomsheets.AlbumMenuDialogFragmentDirections
import com.musicplayer.ui.viewmodels.AlbumViewModel
import com.musicplayer.ui.viewmodels.MainViewModel
import com.musicplayer.utils.Constants
import com.musicplayer.utils.GeneralUtils.getExtraBundle
import org.koin.android.ext.android.inject

class AlbumDetailsFragment : BaseFragment(), SongListListener, Toolbar.OnMenuItemClickListener {

  private lateinit var songs: List<Song>
  private lateinit var binding: FragmentAlbumDetailsBinding
  private val args by navArgs<AlbumDetailsFragmentArgs>()
  private val albumViewModel by inject<AlbumViewModel>()
  private val mainViewModel by inject<MainViewModel>()
  private val mAlbum by lazy { albumViewModel.getAlbum(args.albumId) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    startPostponedEnterTransition()
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
      this.album = mAlbum
      setOnPlayAllClick { onPlayAllClick() }
      setOnPlayRandomlyClick { onPlayRandomlyClick() }
      toolbar.setOnMenuItemClickListener(this@AlbumDetailsFragment)
      songs.elements.adapter = SongListAdapter(this@AlbumDetailsFragment).also { adapter ->
        subscribeUi(adapter)
      }
    }
  }

  private fun subscribeUi(adapter: SongListAdapter) {
    albumViewModel.getSongsByAlbum(args.albumId).observe(viewLifecycleOwner) {
      adapter.submitList(it)
      this.songs = it
    }
  }

  private fun onPlayAllClick() {
    val extras = getExtraBundle(songs.toIDList(), Constants.ALBUM_KEY)
    mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
  }

  private fun onPlayRandomlyClick() {
    val extras = getExtraBundle(songs.toIDList(), mAlbum.title)
    mainViewModel.transportControls()?.sendCustomAction(Constants.PLAY_ALL_SHUFFLED, extras)
  }

  override fun onSongClick(song: Song) {
    val extras = getExtraBundle(songs.toIDList(), mAlbum.title)
    mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
  }

  override fun onSongLongClick(song: Song) {
  }

  override fun onMenuItemClick(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_search -> findNavController().navigate(SearchFragmentDirections.actionGlobalSearchFragment())
      R.id.action_sort -> {
      }
      R.id.action_more_menu -> findNavController().navigate(
        AlbumMenuDialogFragmentDirections.actionGlobalAlbumMenuDialogFragment(
          args.albumId
        )
      )
    }
    return true
  }
}