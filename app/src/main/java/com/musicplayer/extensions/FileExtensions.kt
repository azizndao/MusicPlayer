package com.musicplayer.extensions

import android.content.Context
import com.musicplayer.R
import com.musicplayer.utils.GeneralUtils.getStoragePaths
import timber.log.Timber
import java.io.File

private const val INTERNAL_STORAGE = R.string.internal_storage
private const val EXTERNAL_STORAGE = R.string.external_storage
private const val MEGA_BYTES_SIZE = 1048576

fun File.fixedPath(context: Context): String {
    val storagePaths = getStoragePaths(context)
    val type = path.contains(storagePaths[0])
    Timber.d("fixedPath()")
    return when{
        storagePaths.isNotEmpty() && type -> path.replace(storagePaths[0], "/${context.getString(
            INTERNAL_STORAGE
        )}")
        storagePaths.size > 1 && !type -> path.replace(storagePaths[1], "/${context.getString(
            EXTERNAL_STORAGE
        )}")
        else -> path
    }
}

fun File.fixedName(context: Context): String {
    return File(fixedPath(context)).name
}

fun File.sizeMB(): String? {
    return "${length() / MEGA_BYTES_SIZE} MB"
}