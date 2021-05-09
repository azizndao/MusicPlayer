package com.musicplayer.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.musicplayer.R
import com.musicplayer.databinding.FragmentAlbumListBinding
import com.musicplayer.extensions.observeOnce
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Album
import com.musicplayer.ui.albumdetails.AlbumDetailsFragmentDirections
import com.musicplayer.ui.home.HomeFragmentDirections
import com.musicplayer.ui.viewmodels.AlbumViewModel
import com.musicplayer.ui.viewmodels.MainViewModel
import com.musicplayer.utils.Constants
import com.musicplayer.utils.GeneralUtils
import org.koin.android.ext.android.inject

class AlbumListFragment : Fragment(), AlbumListAdapter.Listener {

  private val albumViewModel by inject<AlbumViewModel>()
  private val mainViewModel by inject<MainViewModel>()
  private lateinit var binding: FragmentAlbumListBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAlbumListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      val orientation = GeneralUtils.getOrientation(requireContext())
      val spanCount = if (orientation == GeneralUtils.PORTRAIT) 2 else 4
      albums.layoutManager = GridLayoutManager(requireContext(), spanCount)
      albums.adapter = AlbumListAdapter(this@AlbumListFragment).also { adapter ->
        albumViewModel.getAlbums().observe(viewLifecycleOwner) { data -> adapter.submitList(data) }
      }
    }
  }

  override fun onAlbumClick(view: View, album: Album) {
    val extras = FragmentNavigatorExtras(view to getString(R.string.album_detail_transition_name))
    val directions = HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(album.id)
    findNavController().navigate(directions, extras)
  }

  override fun onAlbumPlayClick(album: Album) {
    albumViewModel.getSongsByAlbum(album.id).observeOnce { songs ->
      val extras = GeneralUtils.getExtraBundle(songs.toIDList(), Constants.ALBUM_KEY)
      mainViewModel.mediaItemClicked(songs.first().toMediaItem(), extras)
    }
  }

  override fun onAlbumLongClick(view: View, album: Album) {

  }

  override fun onAlbumMenuClick(album: Album) {
    val directions = AlbumDetailsFragmentDirections.actionGlobalAlbumMenuDialogFragment(album.id)
    findNavController().navigate(directions)
  }
}