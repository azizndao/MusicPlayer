package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.musicplayer.databinding.FragmentSongListBinding
import com.musicplayer.models.Song
import com.musicplayer.ui.adapters.SongListAdapter
import com.musicplayer.ui.adapters.SongListListener
import com.musicplayer.ui.viewmodels.MainViewModel
import com.musicplayer.ui.viewmodels.SongViewModel
import org.koin.android.ext.android.inject

class SongListFragment : Fragment(), SongListListener {

  private lateinit var binding: FragmentSongListBinding
  private val mainViewModel by inject<MainViewModel>()
  private val songViewModel by inject<SongViewModel>()

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
      elements.adapter = SongListAdapter(this@SongListFragment).also(::subscribeUI)
    }
  }

  private fun subscribeUI(adapter: SongListAdapter) {
    songViewModel.getSongList().observe(viewLifecycleOwner) { adapter.submitList(it) }
  }

  override fun onSongClick(song: Song) {
  }

  override fun onSongLongClick(song: Song) {
  }
}
