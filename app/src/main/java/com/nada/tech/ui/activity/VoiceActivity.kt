package com.nada.tech.ui.activity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.appcompat.app.AppCompatActivity
import com.nada.tech.databinding.ActivityVoiceBinding
import com.nada.tech.utility.Log
import com.nada.tech.utility.toJson


class VoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoiceBinding

    private var speechRecognizer: SpeechRecognizer? = null
    private var intentRecognizer: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        binding.btnStart.setOnClickListener { speechRecognizer?.startListening(intentRecognizer) }
        binding.btnStop.setOnClickListener { speechRecognizer?.stopListening() }

        intentRecognizer = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentRecognizer?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intentRecognizer?.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000000)
        intentRecognizer?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        intentRecognizer?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, Int.MAX_VALUE)
        intentRecognizer?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) = Unit

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(p0: Float) = Unit

            override fun onBufferReceived(p0: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onError(p0: Int) = Unit

            override fun onResults(bundle: Bundle?) {
//                Log.e("onResults")
//                if (bundle == null) return
//                val matches: ArrayList<String>? =
//                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                if (matches != null) {
//                    binding.textView.text = matches.toJson()
//                }
            }

            override fun onPartialResults(bundle: Bundle?) {
                Log.e("onPartialResults")
                if (bundle == null) return
                val matches: ArrayList<String>? =
                    bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    binding.textView.text = matches.toJson()
                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })
    }
}