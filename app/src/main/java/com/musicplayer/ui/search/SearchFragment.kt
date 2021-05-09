package com.musicplayer.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.musicplayer.databinding.FragmentSearchBinding
import com.musicplayer.utils.BaseFragment

class SearchFragment : BaseFragment() {

  private lateinit var binding: FragmentSearchBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentSearchBinding.inflate(inflater, container, false)
    return binding.root
  }
}