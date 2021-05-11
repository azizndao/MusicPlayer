package com.musicplayer.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.Hold
import com.musicplayer.R
import com.musicplayer.databinding.FragmentHomeBinding
import com.musicplayer.ui.search.SearchFragmentDirections

class HomeFragment : Fragment(R.layout.fragment_home) {

  private lateinit var binding: FragmentHomeBinding
  private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    exitTransition = Hold()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = FragmentHomeBinding.bind(view)
    val tabTitles = resources.getStringArray(R.array.home_tabs)
    postponeEnterTransition()
    view.doOnPreDraw { startPostponedEnterTransition() }
    with(binding) {
      bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
      toolbar.setOnMenuItemClickListener(::onMenuItemClick)
      pages.adapter = HomeViewAdapter(this@HomeFragment)
      TabLayoutMediator(tabs, pages) { tab, position -> tab.text = tabTitles[position] }.attach()
    }
  }

  private fun onMenuItemClick(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_search -> findNavController().navigate(SearchFragmentDirections.actionGlobalSearchFragment())
      R.id.action_sort -> {
      }
      R.id.action_more_menu -> {
      }
    }
    return true
  }
}