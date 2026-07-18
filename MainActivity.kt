package com.example.speaktoflowerv5

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import java.util.Locale
import android.content.Context
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {

    private lateinit var voiceManager: VoiceManager
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var tts: TextToSpeech

    private lateinit var output: EditText
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var speakButton: Button
    private lateinit var importButton: Button
    private lateinit var voiceSpinner: Spinner
    private lateinit var loadingBar: ProgressBar

    private val IMPORT_VOICE = 100
    private val PREFS = "VoiceSlots"
    private val SLOT_COUNT = 10
    private val voiceSlots = mutableListOf<VoiceSlot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        output = findViewById(R.id.textOutput)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        speakButton = findViewById(R.id.speakButton)
        importButton = findViewById(R.id.importButton)
        voiceSpinner = findViewById(R.id.voiceSpinner)
        loadingBar = findViewById(R.id.loadingBar)

        carregarSlots()
        if (!PREFS.contains("slot_name_0")) {
            criarSlots()
            salvarSlots()
            return
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")
            }
        }

        voiceManager = VoiceManager(this)
        voiceSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    voiceManager.setVoice(voiceSlots[position])
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                }
        })
        speechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(this)

        recognizerIntent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "pt-BR"
        )
        speechRecognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {}

                override fun onResults(results: Bundle?) {

                    val texto = results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                    if (!texto.isNullOrEmpty()) {
                        output.setText(texto[0])
                    }
                }

                override fun onPartialResults(results: Bundle?) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )

        importButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

            intent.addCategory(Intent.CATEGORY_OPENABLE)

            intent.type = "*/*"

            startActivityForResult(
                intent,
                IMPORT_VOICE
            )
        }

        startButton.setOnClickListener {

            speechRecognizer.startListening(
                recognizerIntent
            )

        }

        stopButton.setOnClickListener {

            speechRecognizer.stopListening()

        }

        speakButton.setOnClickListener {

            val texto = output.text.toString()

            if (texto.isBlank()) return@setOnClickListener

            val slot =
            voiceSlots[
                voiceSpinner.selectedItemPosition
            ]

            loadingBar.visibility = View.VISIBLE

            if (slot.builtIn) {

                tts.speak(
                    texto,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "android"
                )

                loadingBar.visibility = View.GONE

            } else {

                voiceManager.speak(
                    slot,
                    texto
                )

                loadingBar.visibility = View.GONE

            }

        }
    }
    private fun criarSlots() {

        voiceSlots.clear()

        voiceSlots.add(
            VoiceSlot(
                "Android",
                null,
                true
            )
        )

        for (i in 2 .. 10) {

            voiceSlots.add(
                VoiceSlot("Slot $i")
            )

        }

        atualizarSpinner()

    }

    private fun atualizarSpinner() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            voiceSlots.map { it.name }
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        voiceSpinner.adapter = adapter

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == IMPORT_VOICE &&
            resultCode == RESULT_OK
        ) {

            val uri = data?.data ?: return

            val slot =
            voiceSpinner.selectedItemPosition

            val caminho = copiarModelo(uri)

            if (caminho != null) {

                voiceSlots[slot].path = caminho

            }

            voiceSlots[slot].name =
            uri.lastPathSegment ?: "Modelo"

            atualizarSpinner()
            salvarSlots()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                // Permissão concedida

            } else {

                output.setText(
                    "Permissão do microfone negada."
                )

                startButton.isEnabled = false

            }

        }

    }

    private fun salvarSlots() {

        val prefs = getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val editor = prefs.edit()

        for (i in voiceSlots.indices) {

            editor.putString(
                "slot_name_$i",
                voiceSlots[i].name
            )

            editor.putString(
                "slot_path_$i",
                voiceSlots[i].path
            )

            editor.putBoolean(
                "slot_builtin_$i",
                voiceSlots[i].builtIn
            )

        }

        editor.apply()

    }
    private fun carregarSlots() {

        val prefs = getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        if (!prefs.contains("slot_name_0")) {
            criarSlots()
            salvarSlots()
            return
        }

        voiceSlots.clear()

        for (i in 0 until SLOT_COUNT) {

            val builtIn = prefs.getBoolean(
                "slot_builtin_$i",
                i == 0
            )

            val name = prefs.getString(
                "slot_name_$i",
                if (i == 0) "Android" else "Slot ${i + 1}"
            )!!

            val path = prefs.getString(
                "slot_path_$i",
                null
            )

            voiceSlots.add(
                VoiceSlot(name, path, builtIn)
            )
        }

        atualizarSpinner()

    }
    private fun copiarModelo(uri: android.net.Uri): String? {

        return try {

            val input = contentResolver.openInputStream(uri)
            ?: return null

            val pasta = File(filesDir, "voices")

            if (!pasta.exists()) {
                pasta.mkdirs()
            }

            val arquivo = File(
                pasta,
                "voice_${System.currentTimeMillis()}.bin"
            )

            val output = FileOutputStream(arquivo)

            input.copyTo(output)

            input.close()
            output.close()

            arquivo.absolutePath

        } catch (e: Exception) {

            null

        }

    }

    override fun onDestroy() {

        speechRecognizer.destroy()

        tts.stop()
        tts.shutdown()

        voiceManager.release()

        super.onDestroy()

    }
}
