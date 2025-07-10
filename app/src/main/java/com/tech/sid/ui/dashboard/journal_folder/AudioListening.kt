package com.tech.sid.ui.dashboard.journal_folder

import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityAudioListeningBinding
import com.tech.sid.databinding.ActivityTodayJournalBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import com.tech.sid.ui.onboarding_ques.SuggestionModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class AudioListening : BaseActivity<ActivityAudioListeningBinding>() {
    private val viewModel: TodayJournalVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_audio_listening
    }

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""
    private var isRecording = false
    private lateinit var handler: Handler

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()

        startRecording()
        startRippleLoop()
    }

    private val rippleHandler = Handler(Looper.getMainLooper())
    private var isRippleRunning = false

    private fun startRippleLoop() {
        val layout4 = findViewById<LinearLayout>(R.id.linearLayout4)
        val layout5 = findViewById<LinearLayout>(R.id.linearLayout5)
        val layout6 = findViewById<LinearLayout>(R.id.linearLayout6)

        isRippleRunning = true

        fun playRippleAnimation(view: View, delay: Long) {
            rippleHandler.postDelayed({
                if (!isRippleRunning) return@postDelayed

                // Reset view before animation
                view.clearAnimation()
                view.alpha = 1f

                val anim = AnimationUtils.loadAnimation(this, R.anim.ripple_scale_fade)

                // Let it stay faded after animation ends
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


    private fun startRecording() {
        Handler(Looper.getMainLooper()).post {
            fileName =
                "${externalCacheDir?.absolutePath}/audiorecordtest_${System.currentTimeMillis()}.3gp"
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                    start()
                    isRecording = true
                    startAmplitudeMonitoring()

                } catch (e: IOException) {
                    Log.i("EXCiPTION  I", e.toString())
                } catch (e: IllegalStateException) {
                    Log.i("EXCiPTION  II", e.toString())
                }
            }
        }
    }

    private fun startAmplitudeMonitoring() {
        binding.waveUi.initialize(resources.displayMetrics)
        handler = Handler(Looper.getMainLooper())
        handler.post(amplitudeRunnable)
    }

    private val amplitudeRunnable = object : Runnable {
        override fun run() {
            if (isRecording) {
                try {
                    val amplitude = recorder?.maxAmplitude?.toFloat()
                    if (amplitude != null) {
                        val normalizedAmplitude = (amplitude / 32767f).coerceIn(0f, 1f)
                        binding.waveUi.setAmplitude(normalizedAmplitude)
                    }
                    handler.postDelayed(this, 200)
                } catch (e: Exception) {
                    Log.i("EXCiPTION  III", e.toString())
                }
            }

        }
    }


    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, OnboardingQuestion::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

                R.id.spinnerSelection -> {

                }
            }
        }
    }
}