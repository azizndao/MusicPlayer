package com.musicplayer.utils

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.musicplayer.R
import com.musicplayer.extensions.optimize
import com.musicplayer.extensions.systemService
import com.musicplayer.extensions.toFileDescriptor
import com.musicplayer.models.Song
import com.musicplayer.utils.BeatConstants.ARTWORK_URI
import com.musicplayer.utils.BeatConstants.SEEK_TO_POS
import com.musicplayer.utils.BeatConstants.SONG_LIST_NAME
import com.musicplayer.utils.BeatConstants.SONG_URI
import com.musicplayer.utils.SettingsUtility.Companion.QUEUE_INFO_KEY
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*


object GeneralUtils {

    const val PORTRAIT = ORIENTATION_PORTRAIT

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun hasNavBar(resources: Resources): Boolean {
        //Emulator
        if (Build.FINGERPRINT.startsWith("generic")) return true
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    fun getStatusBarHeight(resources: Resources): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
            return result
        }
        return 0
    }

    fun getNavigationBarHeight(resources: Resources): Int {
        if (!hasNavBar(resources)) return 0
        val orientation = resources.configuration.orientation

        //Only phone between 0-599 has navigationbar can move
        val isSmartphone = resources.configuration.smallestScreenWidthDp < 600
        if (isSmartphone && Configuration.ORIENTATION_LANDSCAPE == orientation) return 0
        val id = resources
            .getIdentifier(
                if (orientation == ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
                "dimen",
                "android"
            )
        return if (id > 0) resources.getDimensionPixelSize(id) else 0
    }

    fun getOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun formatMilliseconds(duration: Long): String {
        val seconds = (duration / 1000).toInt() % 60
        val minutes = (duration / (1000 * 60) % 60).toInt()
        val hours = (duration / (1000 * 60 * 60) % 24).toInt()
        "${timeAddZeros(hours, false)}:${timeAddZeros(minutes)}:${timeAddZeros(seconds)}".apply {
            return if (startsWith(":")) replaceFirst(":", "") else this
        }
    }

    fun getTotalTime(songList: List<Song>): Long {
        var minutes = 0L
        var hours = 0L
        var seconds = 0L
        for (song in songList) {
            seconds += (song.duration / 1000 % 60).toLong()
            minutes += (song.duration / (1000 * 60) % 60).toLong()
            hours += (song.duration / (1000 * 60 * 60) % 24).toLong()
        }
        return hours * (1000 * 60 * 60) + minutes * (1000 * 60) + seconds * 1000
    }

    fun audio2Raw(context: Context, uri: Uri, didTry: Boolean = false): ByteArray? {
        val parcelFileDescriptor = uri.toFileDescriptor(context) ?: return null
        val fis = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val data = try {
            fis.readBytes().optimize()
        } catch (ex: Exception) {
            Timber.e(ex)
            if (didTry) byteArrayOf() else audio2Raw(context, uri, didTry = true)
        }
        fis.close()
        return data
    }

    fun audio2Raw(context: Context, path: String, tryCountDown: Int = 3): ByteArray? {
        val fis = FileInputStream(File(path))
        val data = try {
            fis.readBytes().optimize()
        } catch (ex: Exception) {
            Timber.e(ex)
            if (tryCountDown == 0) byteArrayOf() else audio2Raw(context, path, tryCountDown - 1)
        }
        fis.close()
        return data
    }

    fun toggleShowKeyBoard(context: Context, editText: EditText, show: Boolean) {
        val imm = context.systemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE)
        if (show) {
            editText.apply {
                requestFocus()
                imm.showSoftInput(this, SHOW_IMPLICIT)
            }
        } else {
            editText.apply {
                clearFocus()
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }

    fun addZeros(number: Int?): String {
        if (number!! < 10) return "00${number}"
        if (number < 100) return "0${number}"
        return number.toString()
    }

    private fun timeAddZeros(number: Int?, showIfIsZero: Boolean = true): String {
        return when (number) {
            0 -> if (showIfIsZero) "0${number}" else ""
            1, 2, 3, 4, 5, 6, 7, 8, 9 -> "0${number}"
            else -> number.toString()
        }
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else Color.BLACK
    }

    fun getStoragePaths(context: Context): List<String> {
        return try {
            val paths: Array<File>? = ContextCompat.getExternalFilesDirs(context, null)
            paths?.map {
                it.path.replace("/Android/data/${context.packageName}/files", "")
            } ?: emptyList()
        } catch (ex: IllegalStateException) {
            emptyList()
        }
    }

    @Suppress("DEPRECATION")
    fun getAlbumArtBitmap(context: Context, albumId: Long): Bitmap {
        try {
            return when {
                SDK_INT >= P -> {
                    val source = ImageDecoder.createSource(
                        context.contentResolver,
                        getAlbumArtUri(albumId)
                    )
                    ImageDecoder.decodeBitmap(source)
                }
                else -> MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    getAlbumArtUri(albumId)
                )
            }
        } catch (e: FileNotFoundException) {
            Timber.e(e)
        } catch (e: UnsupportedOperationException) {
            Timber.e(e)
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_logo)
    }

    fun getExtraBundle(queue: LongArray, title: String): Bundle? {
        return getExtraBundle(queue, title, 0)
    }

    fun getExtraBundle(queue: LongArray, title: String, seekTo: Int?): Bundle? {
        val bundle = Bundle()
        bundle.putLongArray(QUEUE_INFO_KEY, queue)
        bundle.putString(SONG_LIST_NAME, title)
        if (seekTo != null)
            bundle.putInt(SEEK_TO_POS, seekTo)
        else bundle.putInt(SEEK_TO_POS, 0)
        return bundle
    }

    fun addColorOpacity(color: Int, opacity: Float): Int {
        val colorHex = Integer.toHexString(color).toLowerCase(Locale.ROOT)
        val opacityHex = Integer.toHexString((255 * opacity).toInt()).toLowerCase(Locale.ROOT)
        return Color.parseColor("#${colorHex.replace("ff", opacityHex)}")
    }

    fun isOreo() = SDK_INT >= O
    fun getAlbumArtUri(albumId: Long): Uri = withAppendedId(ARTWORK_URI, albumId)
    fun getSongUri(songId: Long): Uri = withAppendedId(SONG_URI, songId)
}
