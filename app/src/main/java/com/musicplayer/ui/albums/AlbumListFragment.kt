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
import com.musicplayer.models.Album
import com.musicplayer.ui.albumdetails.AlbumDetailsFragmentDirections
import com.musicplayer.ui.home.HomeFragmentDirections
import com.musicplayer.utils.GeneralUtils
import com.musicplayer.utils.SpringAddItemAnimator
import com.musicplayer.viewmodels.MainViewModel
import org.koin.android.ext.android.inject

class AlbumListFragment : Fragment(),
  AlbumListAdapter.Listener {

  private lateinit var binding: FragmentAlbumListBinding
  private val viewModel by inject<MainViewModel>()
  private val albumListAdapter = AlbumListAdapter(this)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
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
      albums.itemAnimator = SpringAddItemAnimator()
      albums.layoutManager = GridLayoutManager(requireContext(), spanCount)
      albums.adapter = albumListAdapter
      subscribeUI()
    }
  }

  private fun FragmentAlbumListBinding.subscribeUI() {
    viewModel.getAllAlbums().observe(viewLifecycleOwner) { data ->
      loaded = true
      isEmpty = data.isEmpty()
      albumListAdapter.submitList(data)
    }
  }

  override fun onAlbumClick(view: View, album: Album) {
    val extras = FragmentNavigatorExtras(view to getString(R.string.album_detail_transition_name))
    val directions = HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(album.id)
    findNavController().navigate(directions, extras)
  }

  override fun onAlbumPlayClick(album: Album) = viewModel.playAlbum(album)

  override fun onAlbumLongClick(view: View, album: Album) {

  }

  override fun onAlbumMenuClick(album: Album) {
    val directions = AlbumDetailsFragmentDirections.actionGlobalAlbumMenuDialogFragment(album.id)
    findNavController().navigate(directions)
  }
}