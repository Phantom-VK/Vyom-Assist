package com.swag.vyom.audioRecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build

class AndroidAudioRecorder(
    private val context: Context
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private fun createRecorder(): MediaRecorder{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }
    }
    override fun startRecording(filepath: String) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filepath)

            try {
                prepare()
                start()

                recorder = this
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun stopRecording() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }


}