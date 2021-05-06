package com.musicplayer.interfaces

import com.musicplayer.enums.State

interface StateListener {
    fun onStateChanged(state: State, seconds: Int)
}