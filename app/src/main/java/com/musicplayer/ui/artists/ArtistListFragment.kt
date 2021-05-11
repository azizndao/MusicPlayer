package com.musicplayer.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.musicplayer.R
import com.musicplayer.databinding.FragmentAlbumListBinding
import com.musicplayer.databinding.FragmentArtistListBinding
import com.musicplayer.models.Artist
import com.musicplayer.ui.home.HomeFragmentDirections
import com.musicplayer.utils.SpringAddItemAnimator
import com.musicplayer.viewmodels.MainViewModel
import org.koin.android.ext.android.inject

class ArtistListFragment : Fragment(),
  ArtistLitAdapter.Listener {

  private lateinit var binding: FragmentArtistListBinding
  private val artistLitAdapter = ArtistLitAdapter(this)
  val viewModel by inject<MainViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentArtistListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.run {
      artistList.itemAnimator = SpringAddItemAnimator()
      artistList.adapter = artistLitAdapter.also { subscribeUI(it) }
    }
  }

  private fun FragmentArtistListBinding.subscribeUI(adapter: ArtistLitAdapter) {
    viewModel.getAllArtists().observe(viewLifecycleOwner) { artists ->
      isEmpty = artists.isEmpty()
      loaded = true
      adapter.submitList(artists)
    }
  }

  override fun onArtistClick(view: View, artist: Artist) {
    val direction = HomeFragmentDirections.actionHomeFragmentToArtistDetailsFragment(artist.id)
    findNavController().navigate(direction)
  }

  override fun onArtistLongClick(view: View, artist: Artist) {
    Toast.makeText(requireActivity(), "Hello Worls", Toast.LENGTH_SHORT).show()
    view.animate()
      .scaleX(0.85f)
      .scaleY(0.85f)
      .setInterpolator(DecelerateInterpolator())
      .start()
  }

  override fun onArtistPlayClick(view: View, artist: Artist) {
  }

  override fun onArtistMenuClick(view: View, artist: Artist) {
  }
}