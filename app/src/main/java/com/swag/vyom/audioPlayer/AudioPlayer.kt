package com.swag.vyom.audioPlayer

import java.io.File

interface AudioPlayer {

    fun playFile(filePath: String, onCompletion: () -> Unit)
    fun stopPlaying()
    fun deleteAudioFile(filePath: String)
}