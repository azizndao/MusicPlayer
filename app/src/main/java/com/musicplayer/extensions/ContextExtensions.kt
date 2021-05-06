package com.musicplayer.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.AttrRes

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

fun Context.getColorByTheme(
    @AttrRes id: Int
): Int {
    val colorAttr: Int? = id
    val outValue = TypedValue()
    theme.resolveAttribute(colorAttr!!, outValue, true)
    return outValue.data
}

fun Context.shortToast(message: String) = Toast.makeText(this, message, LENGTH_SHORT).show()