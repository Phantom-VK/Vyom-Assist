package com.swag.vyom.audioRecorder

import java.io.File

interface AudioRecorder{
    fun startRecording(filePath:String)
    fun stopRecording()


}
