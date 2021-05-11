package com.musicplayer.utils

import android.content.Intent
import android.os.Bundle
import com.musicplayer.R
import org.koin.android.ext.android.inject


open class BaseActivity : RequestPermissionActivity() {

    private var currentTheme: String? = null

    private val settingsUtility by inject<SettingsUtility>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        if (settingsUtility.currentTheme != currentTheme) {
            recreateActivity()
        }
    }

    private fun init() {
        currentTheme = settingsUtility.currentTheme
        setAppTheme(currentTheme!!)
    }

    override fun recreateActivity() {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        startActivity(intent.apply {
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }

/*
    fun search(view: View) {
        addFragment(
            R.id.nav_host_fragment,
            SearchFragment(),
            Constants.SONG_DETAIL,
            true
        )
    }
*/

    private fun setAppTheme(current_theme: String) {
        when (current_theme) {
            Constants.BLACK_THEME -> setTheme(R.style.AppTheme_Black)
            Constants.DARK_THEME -> setTheme(R.style.AppTheme_Dark)
            Constants.LIGHT_THEME -> setTheme(R.style.AppTheme_Light)
            else -> setTheme(R.style.AppTheme_Auto)
        }
    }
}
