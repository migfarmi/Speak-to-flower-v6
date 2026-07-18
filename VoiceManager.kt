package com.example.speaktoflowerv5

import android.content.Context
import android.media.MediaPlayer
import java.io.File

class VoiceManager(
    private val context: Context
) {

    private var player: MediaPlayer? = null
    private var currentVoice: VoiceSlot? = null

    fun setVoice(slot: VoiceSlot) {
        currentVoice = slot
    }

    fun speak(slot: VoiceSlot, text: String) {
        Thread {
val voicePath = currentVoice?.path
val audio = ApiClient.gerarAudio(text, voicePath)
            if (audio != null) {
                val file = File(context.cacheDir, "voice.wav")
                file.writeBytes(audio)
                play(file)
            }

        }.start()
    }

    private fun play(file: File) {
        player?.release()

        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
