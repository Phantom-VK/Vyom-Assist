package com.swag.vyom.audioPlayer

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File


class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private var player : MediaPlayer ?= null
    override fun playFile(filePath: String, onCompletion: () -> Unit) {
        try {
            MediaPlayer.create(context, filePath.toUri()).apply {
                reset()
                setDataSource(filePath)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun stopPlaying() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun deleteAudioFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

}