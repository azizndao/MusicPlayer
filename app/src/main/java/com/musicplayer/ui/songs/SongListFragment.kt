package com.musicplayer.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.musicplayer.R
import com.musicplayer.databinding.FragmentSongListBinding
import com.musicplayer.extensions.toIDList
import com.musicplayer.models.Song
import com.musicplayer.utils.GeneralUtils.getExtraBundle
import com.musicplayer.utils.SpringAddItemAnimator
import com.musicplayer.viewmodels.MainViewModel
import org.koin.android.ext.android.inject

class SongListFragment : Fragment(), SongListAdapter.Listener {

  private lateinit var binding: FragmentSongListBinding
  private val viewModel by inject<MainViewModel>()
  private val songListAdapter = SongListAdapter(this)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentSongListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      elements.itemAnimator = SpringAddItemAnimator()
      binding.elements.adapter = songListAdapter
      subscribeUI()
    }
  }

  private fun subscribeUI() {
    viewModel.getAllSongs().observe(viewLifecycleOwner) { songListAdapter.submitList(it) }
  }

  override fun onSongClick(song: Song) {
    val extras = getExtraBundle(
      songListAdapter.currentList.toIDList(),
      getString(R.string.all_songs)
    )
    viewModel.mediaItemClicked(song.toMediaItem(), extras)
  }

  override fun onSongLongClick(song: Song) {
  }
}
