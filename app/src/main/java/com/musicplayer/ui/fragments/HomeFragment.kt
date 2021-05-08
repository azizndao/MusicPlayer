package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.musicplayer.R
import com.musicplayer.databinding.FragmentHomeBinding
import com.musicplayer.ui.adapters.HomeViewAdapter

class HomeFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

  private lateinit var binding: FragmentHomeBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val tabTitles = resources.getStringArray(R.array.home_tabs)
    with(binding) {
      toolbar.setOnMenuItemClickListener(this@HomeFragment)
      pages.adapter = HomeViewAdapter(this@HomeFragment)
      pages.currentItem = 1
      TabLayoutMediator(tabs, pages) { tab, position -> tab.text = tabTitles[position] }.attach()
    }
  }

  override fun onMenuItemClick(item: MenuItem): Boolean {
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