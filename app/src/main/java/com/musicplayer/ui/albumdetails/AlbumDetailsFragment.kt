package com.musicplayer.ui.albumdetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.musicplayer.R
import com.musicplayer.databinding.FragmentAlbumDetailsBinding
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Song
import com.musicplayer.ui.dialogs.AlbumMenuDialogFragmentDirections
import com.musicplayer.ui.search.SearchFragmentDirections
import com.musicplayer.ui.songs.SongListAdapter
import com.musicplayer.utils.Constants
import com.musicplayer.utils.GeneralUtils
import com.musicplayer.utils.SpringAddItemAnimator
import com.musicplayer.utils.themeColor
import com.musicplayer.viewmodels.MainViewModel
import org.koin.android.ext.android.inject

class AlbumDetailsFragment : Fragment(),
  SongListAdapter.Listener,
  Toolbar.OnMenuItemClickListener {

  private lateinit var binding: FragmentAlbumDetailsBinding
  private val args by navArgs<AlbumDetailsFragmentArgs>()
  private val viewModel by inject<MainViewModel>()
  private val mAlbum by lazy { viewModel.getAlbum(args.albumId) }
  private val mAdapter = SongListAdapter(this)
  private val songs get() = mAdapter.currentList

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sharedElementEnterTransition = MaterialContainerTransform().also {
      it.drawingViewId = R.id.nav_host_fragment
      it.scrimColor = Color.TRANSPARENT
      it.setAllContainerColors(requireContext().themeColor(android.R.attr.colorBackground))
    }
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
      album = mAlbum
      setOnPlayAllClick { onPlayAllClick() }
      setOnPlayRandomlyClick { onPlayRandomlyClick() }
      toolbar.setOnMenuItemClickListener(this@AlbumDetailsFragment)
      songs.adapter = mAdapter.also(::subscribeUi)
      songs.itemAnimator = SpringAddItemAnimator()
    }
  }

  private fun subscribeUi(adapter: SongListAdapter) {
    viewModel.getSongsByAlbum(args.albumId).observe(viewLifecycleOwner) {
      adapter.submitList(it)
    }
  }

  private fun onPlayAllClick() = viewModel.playAlbum(mAlbum)

  private fun onPlayRandomlyClick() {
    val extras = GeneralUtils.getExtraBundle(songs.toIDList(), mAlbum.title)
    viewModel.transportControls()?.sendCustomAction(Constants.PLAY_ALL_SHUFFLED, extras)
  }

  override fun onSongClick(song: Song) {
    val extras = GeneralUtils.getExtraBundle(songs.toIDList(), mAlbum.title)
    viewModel.mediaItemClicked(song.toMediaItem(), extras)
  }

  override fun onSongLongClick(song: Song) {
  }

  override fun onMenuItemClick(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_search -> {
        val directions = SearchFragmentDirections.actionGlobalSearchFragment()
        findNavController().navigate(directions)
      }
      R.id.action_sort -> {
      }
      R.id.action_more_menu -> {
        val directions =
          AlbumMenuDialogFragmentDirections.actionGlobalAlbumMenuDialogFragment(args.albumId)
        findNavController().navigate(directions)
      }
    }
    return true
  }
}