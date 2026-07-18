package com.example.speaktoflowerv5

data class VoiceSlot(
    var name: String,
    var path: String? = null,
    var builtIn: Boolean = false
)