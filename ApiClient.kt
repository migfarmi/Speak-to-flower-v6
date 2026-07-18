package com.example.speaktoflowerv5

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ApiClient {

    private const val SERVER = "http://SEU_IP:5000"

    fun gerarAudio(texto: String, voicePath: String?): ByteArray? {
    return try {

        val url = URL(
            "$SERVER/tts?text=${
                URLEncoder.encode(texto, "UTF-8")
            }&voice=${
                URLEncoder.encode(voicePath ?: "", "UTF-8")
            }"
        )

        val conexao = url.openConnection() as HttpURLConnection

        conexao.requestMethod = "GET"
        conexao.connectTimeout = 10000
        conexao.readTimeout = 10000

        if (conexao.responseCode == HttpURLConnection.HTTP_OK) {
            conexao.inputStream.readBytes()
        } else {
            null
        }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
}