package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.musicplayer.databinding.FragmentAlbumListBinding
import com.musicplayer.ui.adapters.AlbumListAdapter
import com.musicplayer.ui.viewmodels.AlbumViewModel
import org.koin.android.ext.android.inject

class AlbumListFragment : Fragment() {

  private val viewModel by inject<AlbumViewModel>()
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
      albums.layoutManager = GridLayoutManager(requireContext(), 3)
      albums.adapter = AlbumListAdapter().also { adapter ->
        viewModel.getAlbums().observe(viewLifecycleOwner) { data -> adapter.submitList(data) }
      }
    }
  }
}