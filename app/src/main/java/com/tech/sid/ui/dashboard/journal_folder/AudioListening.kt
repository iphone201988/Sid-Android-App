package com.tech.sid.ui.dashboard.journal_folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityAudioListeningBinding
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AudioListening : BaseActivity<ActivityAudioListeningBinding>() {
    private val viewModel: TodayJournalVm by viewModels()

    override fun getLayoutResource(): Int = R.layout.activity_audio_listening
    override fun getViewModel(): BaseViewModel = viewModel
    private var isRecording = false
    private var isRippleRunning = false
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private val rippleHandler = Handler(Looper.getMainLooper())

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()

        // Setup recognizer & intent
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    /**
     * Start Google voice recognition
     */
    private fun startVoiceRecognition(context: Context) {
        isRecording = true
        isRippleRunning = true

        binding.linearLayout7.background =
            AppCompatResources.getDrawable(context, R.drawable.mic_icon_audio_page)

        binding.waveUi.setAmplitude(0f)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.waveUi.initialize(context.resources.displayMetrics)
            }
            override fun onBeginningOfSpeech() {
                binding.waveUi.setAmplitude(0.2f) // start small pulse
            }

            override fun onRmsChanged(rmsdB: Float) {
                val normalized = (rmsdB / 12f).coerceIn(0f, 1f)
                binding.waveUi.setAmplitude(normalized)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                binding.waveUi.setAmplitude(0f) // stop animation
                stopVoiceRecognition(context)
            }

            override fun onError(error: Int) {
                Log.d("VoiceGoogle", "Error: $error")
                stopVoiceRecognition(context)
                binding.waveUi.setAmplitude(0f)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                binding.textTv.text = spokenText
                Log.d("VoiceGoogle", "Recognized: $spokenText")
                stopVoiceRecognition(context)
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        Handler(Looper.getMainLooper()).post {
            // ensure fresh initialization each time you start listening
            binding.waveUi.initialize(context.resources.displayMetrics)
            speechRecognizer.startListening(speechIntent)
            startRippleLoop()
        }


    }

    /**
     * Stop Google voice recognition
     */
    private fun stopVoiceRecognition(context: Context) {
        if (!isRecording) return
        isRecording = false
        isRippleRunning = false
        speechRecognizer.stopListening()

        binding.linearLayout7.background =
            AppCompatResources.getDrawable(context, R.drawable.iv_mute)

       // binding.waveUi.stop()
    }

    /**
     * Ripple animation loop for mic background
     */
    private fun startRippleLoop() {
        val layout4 = findViewById<LinearLayout>(R.id.linearLayout4)
        val layout5 = findViewById<LinearLayout>(R.id.linearLayout5)
        val layout6 = findViewById<LinearLayout>(R.id.linearLayout6)

        isRippleRunning = true

        fun playRippleAnimation(view: View, delay: Long) {
            rippleHandler.postDelayed({
                if (!isRippleRunning) return@postDelayed

                view.clearAnimation()
                view.alpha = 1f

                val anim = AnimationUtils.loadAnimation(this, R.anim.ripple_scale_fade)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        view.alpha = 0f
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                view.startAnimation(anim)
            }, delay)
        }

        val rippleRunnable = object : Runnable {
            override fun run() {
                if (!isRippleRunning) return
                playRippleAnimation(layout4, 0)
                playRippleAnimation(layout5, 200)
                playRippleAnimation(layout6, 400)
                rippleHandler.postDelayed(this, 1400)
            }
        }
        rippleRunnable.run()
    }

    /**
     * Handle button clicks
     */
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, OnboardingQuestion::class.java))
                }
                R.id.back_button -> {
                    finish()
                }
                R.id.linearLayout2 -> {
                    val returnIntent = Intent()
                    returnIntent.putExtra("result_text", binding.textTv.text.toString())
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
                R.id.linearLayout3 -> {
                    binding.textTv.text = ""
                }
                R.id.linearLayout7 -> {
                    if (isRecording) {
                        stopVoiceRecognition(this)
                    } else {
                        startVoiceRecognition(this)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Stop speech recognizer if active
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Stop ripple animation if running
      stopVoiceRecognition(this)
        binding.linearLayout7.background =
            AppCompatResources.getDrawable(this, R.drawable.mic_icon_audio_page)

        isRecording = false
    }

}
