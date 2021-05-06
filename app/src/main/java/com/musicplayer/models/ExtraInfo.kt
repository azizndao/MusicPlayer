package com.musicplayer.models

import com.musicplayer.extensions.addIfNotEmpty
import com.musicplayer.extensions.fromJson
import com.musicplayer.extensions.khz
import com.google.gson.Gson
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.tag.FieldKey

data class ExtraInfo(
    val bitRate: String,
    val fileType: String,
    val frequency: String,
    val queuePosition: String
){
    companion object{
        fun fromString(info: String): ExtraInfo{
            return Gson().fromJson(info)
        }

        fun createFromAudioFile(audioFile: AudioFile, queuePosition: String): ExtraInfo {
            return ExtraInfo(
                audioFile.audioHeader.bitRate.addIfNotEmpty("kbps"),
                audioFile.file.extension,
                audioFile.audioHeader.sampleRate.khz().addIfNotEmpty("khz"),
                queuePosition
            )
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
