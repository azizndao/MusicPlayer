package com.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.musicplayer.R
import com.musicplayer.databinding.FragmentHomeBinding
import com.musicplayer.ui.adapters.HomeViewAdapter

class HomeFragment : Fragment() {

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
    with(binding) {
      val tabTitles = resources.getStringArray(R.array.home_tabs)
      pages.adapter = HomeViewAdapter(this@HomeFragment)
      pages.currentItem = 1
      TabLayoutMediator(tabs, pages) { tab, position -> tab.text = tabTitles[position] }.attach()
    }
  }
}