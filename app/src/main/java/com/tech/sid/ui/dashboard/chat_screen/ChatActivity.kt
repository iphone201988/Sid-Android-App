package com.tech.sid.ui.dashboard.chat_screen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChatBinding
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsights
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsightsModel
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsightsResponse
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val viewModel: ChatActivityVm by viewModels()
    var chatCount = 0
    private lateinit var adapter: ChatAdapter
    override fun getLayoutResource(): Int {
        return R.layout.activity_chat
    }

    companion object {
        var scenarioId: String? = ""
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        enableEdgeToEdge()
        BindingUtils.screenFillView(this)
        ViewCompat.setOnApplyWindowInsetsListener(binding.constraintLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0,systemBars.top, 0, (systemBars.top*0.2).toInt())

            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val isKeyboardVisible: Boolean = imeHeight > 0
            if (isKeyboardVisible) {
                view.setPadding(0, 0, 0, imeHeight)
            }else{
                view.setPadding(0,0, 0, systemBars.bottom)
            }
            insets
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        initOnClick()
        adapter = ChatAdapter()
        binding.chatRv.adapter = adapter

        binding.nameTitle.text = BindingUtils.interactionModelPost?.relation ?: ""
        binding.subTitleName.text = BindingUtils.interactionModelPost?.responseStyle ?: ""
        apiObserver()
        initView()
//        startRippleLoop()
    }

    private var breathingAnimator: AnimatorSet? = null
    private fun startBreathingAnimation() {
        val view = binding.ViewInsightsLL

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f)
        ).apply {
            duration = 1500
        }

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f, 1f)
        ).apply {
            duration = 1500
        }

        breathingAnimator = AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            interpolator = AccelerateDecelerateInterpolator()

            // loop
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (this@apply == breathingAnimator) { // only if not stopped
                        start()
                    }
                }
            })
            start()
        }
    }

    fun stopBreathingAnimation() {
        breathingAnimator?.cancel()
        breathingAnimator = null
    }


    private fun initView() {
        val side = intent.getStringExtra("side")
        if (side != null) {
            if (side == "Home") {
                scenarioId?.let { viewModel.getChatFunction(it) }
            }
        }
    }

    private fun apiObserver() {
        viewModel.observeCommon.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    CommonFunctionClass.logPrint(response = it.message.toString())
                    if (it.message == Constants.INSIGHTS_ACCOUNT) {
                        showLoading("Loading")
                    }

                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.INSIGHTS_ACCOUNT -> {
                            val chatApiResposeModelModel: SimulationInsightsResponse? =
                                BindingUtils.parseJson(it.data.toString())
                            if (chatApiResposeModelModel != null) {
                                SimulationInsights.isChatRoute = true
                                SimulationInsights.simulationInsightsId = ""
                                SimulationInsights.simulationInsightsModel =
                                    SimulationInsightsModel(
                                        nextSteps = chatApiResposeModelModel.insight?.nextSteps,
                                        relationalPattern = chatApiResposeModelModel.insight?.relationalPattern,
                                        whatsReallyGoingOn = chatApiResposeModelModel.insight?.whatsReallyGoingOn
                                    )
                                startActivity(Intent(this, SimulationInsights::class.java))
                            }
                        }

                        Constants.POST_CHAT_API -> {
                            try {
                                val chatApiResposeModelModel: ChatApiResposeModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (chatApiResposeModelModel?.success == true) {
                                    if (adapter.getList()?.isNotEmpty() == true) {
                                        adapter.getList()?.removeAt(adapter.getList()!!.size - 1)
                                        adapter.notifyItemRemoved(adapter.getList()!!.size)
                                    }
                                    binding.sendButtom.isEnabled = true
                                    val dadasd = GetChatApiResponse.Data(
                                        chat = listOf(
                                            GetChatApiResponse.Data.Chat(
                                                __v = 1,
                                                _id = "chat123",
                                                createdAt = "2025-08-22T12:28:40.784Z",
                                                updatedAt = "2025-08-22T12:32:07.161Z",
                                                simulationId = "simulation123",
                                                userId = "user123",
                                                messages = listOf(
                                                    GetChatApiResponse.Data.Chat.Message(
                                                        from = "ai",
                                                        message = chatApiResposeModelModel.newMessage
                                                    )
                                                )
                                            )
                                        ), simulationData = null
                                    )


                                    if(!dadasd.chat.isNullOrEmpty()) {
                                        adapter.addToListSendMessage(dadasd.chat?.get(0)?.messages)
                                    }

                                    if(adapter.itemCount > 0) {
                                        binding.chatRv.smoothScrollToPosition(adapter.itemCount - 1)
                                    }

                                    if (chatApiResposeModelModel.concluding) {
//                                        binding.aminLottie.playAnimation()
                                        startRippleLoop()
                                        startBreathingAnimation()
                                        binding.aminLottie.visibility = View.VISIBLE
                                        binding.buttonContinue.visibility = View.VISIBLE
                                        binding.linearLayout.visibility = View.GONE
                                        binding.sendButtom.visibility = View.GONE
                                    } else {
                                        binding.aminLottie.visibility = View.GONE
                                        binding.buttonContinue.visibility = View.GONE
                                        binding.linearLayout.visibility = View.VISIBLE
                                        binding.sendButtom.visibility = View.VISIBLE
                                        stopBreathingAnimation()
                                    }


//                                    Handler(Looper.getMainLooper()).postDelayed({
//                                        SimulationInsights.isChatRoute = true
//                                        SimulationInsights.simulationInsightsId = scenarioId?:""
//                                        startActivity(Intent(this, SimulationInsights::class.java))
//                                    }, 100)


                                } else {
                                    chatApiResposeModelModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        "empathy" -> {
                            try {
                                val myDataModel: GetChatApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    if(myDataModel.data != null) {
                                        binding.nameTitle.text = myDataModel.data?.simulationData?.relation
                                        binding.subTitleName.text = myDataModel.data?.simulationData?.responseStyle
                                        if(myDataModel.data!!.chat?.isNotEmpty() == true){
                                            adapter.setList(myDataModel.data!!.chat?.get(0)?.messages)
                                        }

                                        binding.chatRv.post {
                                            if (adapter.itemCount > 0) {
                                                binding.chatRv.scrollToPosition(adapter.itemCount - 1)
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                Status.UN_AUTHORIZE -> {
                    hideLoading()
                    showUnauthorised()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }
    private val rippleHandler = Handler(Looper.getMainLooper())
    private var isRippleRunning = false
    private fun startRippleLoop() {
        val layout4 = findViewById<LinearLayout>(R.id.linearLayout4)
        val layout5 = findViewById<LinearLayout>(R.id.linearLayout5)
        val layout6 = findViewById<LinearLayout>(R.id.linearLayout6)
        val layout7 = findViewById<LinearLayout>(R.id.linearLayout7)

        val speedOffset = 0.8f  // 1.0 = normal speed, <1 = faster, >1 = slower

        isRippleRunning = true

        fun playRippleAnimation(view: View, delay: Long) {
            rippleHandler.postDelayed({
                if (!isRippleRunning) return@postDelayed

                view.clearAnimation()
                view.alpha = 1f

                val anim = AnimationUtils.loadAnimation(this, R.anim.ripple_scale_fade)
                anim.duration = (600 * speedOffset).toLong() // faster ripple
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

                playRippleAnimation(layout7, 0)
                playRippleAnimation(layout4, (150 * speedOffset).toLong())
                playRippleAnimation(layout5, (300 * speedOffset).toLong())
                playRippleAnimation(layout6, (450 * speedOffset).toLong())

                rippleHandler.postDelayed(this, (1000 * speedOffset).toLong())
            }
        }

        rippleRunnable.run()
    }

    /*    private fun startRippleLoop() {
        var longDuration:Long=10
        val layout4 = findViewById<LinearLayout>(R.id.linearLayout4)
        val layout5 = findViewById<LinearLayout>(R.id.linearLayout5)
        val layout6 = findViewById<LinearLayout>(R.id.linearLayout6)
        val layout7 = findViewById<LinearLayout>(R.id.linearLayout7)

        isRippleRunning = true

        fun playRippleAnimation(view: View, delay: Long) {

            rippleHandler.postDelayed({
                if (!isRippleRunning) return@postDelayed

                view.clearAnimation()
                view.alpha = 1f
                val anim = AnimationUtils.loadAnimation(this, R.anim.ripple_scale_fade)
                anim.duration = 600-longDuration // ⏩ faster than before (was probably 800–1000)
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

                playRippleAnimation(layout7, 0)
                playRippleAnimation(layout4, 150-longDuration) // faster stagger
                playRippleAnimation(layout5, 300-longDuration)
                playRippleAnimation(layout6, 450-longDuration)

                rippleHandler.postDelayed(this, 1000) // restart sooner
            }
        }
        rippleRunnable.run()
    }*/
//    private fun startRippleLoop() {
//        val layout4 = findViewById<LinearLayout>(R.id.linearLayout4)
//        val layout5 = findViewById<LinearLayout>(R.id.linearLayout5)
//        val layout6 = findViewById<LinearLayout>(R.id.linearLayout6)
//        val layout7 = findViewById<LinearLayout>(R.id.linearLayout7)
//        isRippleRunning = true
//        fun playRippleAnimation(view: View, delay: Long) {
//            rippleHandler.postDelayed({
//                if (!isRippleRunning) return@postDelayed
//
//                view.clearAnimation()
//                view.alpha = 1f
//
//                val anim = AnimationUtils.loadAnimation(this, R.anim.ripple_scale_fade)
//                anim.setAnimationListener(object : Animation.AnimationListener {
//                    override fun onAnimationStart(animation: Animation?) {}
//                    override fun onAnimationEnd(animation: Animation?) {
//                        view.alpha = 0f
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation?) {}
//                })
//                view.startAnimation(anim)
//            }, delay)
//        }
//
//        val rippleRunnable = object : Runnable {
//            override fun run() {
//                if (!isRippleRunning) return
//                playRippleAnimation(layout7, 0)
//                playRippleAnimation(layout4, 200)
//                playRippleAnimation(layout5, 400)
//                playRippleAnimation(layout6, 600)
//                rippleHandler.postDelayed(this, 1400)
//            }
//        }
//        rippleRunnable.run()
//    }
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.headingTitle -> {
                    TodayJournal.isEdited = false
                    TodayJournal.data = null
                    startActivity(Intent(this, TodayJournal::class.java))
                }

                R.id.ViewInsightsLL -> {
                    val data: HashMap<String, Any> = hashMapOf(
                        "simulationId" to scenarioId!!,
                    )
                    viewModel.simulationFunction(data)
                }

                R.id.buttonContinue -> {
                    stopBreathingAnimation()
                    binding.aminLottie.visibility = View.GONE
                    binding.buttonContinue.visibility = View.GONE
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.sendButtom.visibility = View.VISIBLE
                }

                R.id.sendButtom -> {
                    if (binding.enterEmail.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the message")
                        return@observe
                    }

                    if (binding.enterEmail.text.toString().trim()
                            .equals("Stop", ignoreCase = true)
                    ) {
                        val data: HashMap<String, Any> = hashMapOf(
                            "simulationId" to scenarioId!!,
                        )
                        viewModel.simulationFunction(data)
                        return@observe
                    }

                    val data: HashMap<String, Any> = hashMapOf(
                        "message" to binding.enterEmail.text.toString().trim(),
                        "simulationId" to scenarioId!!,
                    )
                    binding.sendButtom.isEnabled = false
                    viewModel.postChatFunction(data)
                    val dadasd = GetChatApiResponse.Data(
                        chat = listOf(
                            GetChatApiResponse.Data.Chat(
                                __v = 1,
                                _id = "chat123",
                                createdAt = "2025-08-22T12:28:40.784Z",
                                updatedAt = "2025-08-22T12:32:07.161Z",
                                simulationId = "simulation123",
                                userId = "user123",
                                messages = listOf(
                                    GetChatApiResponse.Data.Chat.Message(
                                        from = "user",
                                        message = binding.enterEmail.text.toString().trim()
                                    )
                                )
                            )
                        ), simulationData = null
                    )
                    adapter.addToListSendMessage(dadasd.chat?.get(0)?.messages)

                    val dadasd1 = GetChatApiResponse.Data(
                        chat = listOf(
                            GetChatApiResponse.Data.Chat(
                                __v = 1,
                                _id = "chat123",
                                createdAt = "2025-08-22T12:28:40.784Z",
                                updatedAt = "2025-08-22T12:32:07.161Z",
                                simulationId = "simulation123",
                                userId = "user123",
                                messages = listOf(
                                    GetChatApiResponse.Data.Chat.Message(
                                        from = "ai", message = ""
                                    )
                                )
                            )
                        ), simulationData = null
                    )

                    adapter.addToListSendMessage(dadasd1.chat?.get(0)?.messages)
                    binding.enterEmail.text?.clear()
                    chatCount++
                    binding.chatRv.post {
                        binding.chatRv.smoothScrollToPosition(adapter.itemCount - 1)
                    }

//                    startActivity(Intent(this, SimulationInsights::class.java))

                    // Check size condition
                    if (chatCount in 3..6) {
                        // 50% chance ya random condition
                        val showAnimation = (0..1).random() == 1
                        if (showAnimation) {
//                            binding.aminLottie.playAnimation()
                            startRippleLoop()
                            startBreathingAnimation()
                            binding.aminLottie.visibility = View.VISIBLE
                            binding.buttonContinue.visibility = View.VISIBLE
                            binding.linearLayout.visibility = View.GONE
                            binding.sendButtom.visibility = View.GONE
                        } else {
                            binding.aminLottie.visibility = View.GONE
                            binding.buttonContinue.visibility = View.GONE
                            binding.linearLayout.visibility = View.VISIBLE
                            binding.sendButtom.visibility = View.VISIBLE
                            stopBreathingAnimation()
                        }
                    } else {
                        binding.aminLottie.visibility = View.GONE
                        binding.buttonContinue.visibility = View.GONE
                        binding.linearLayout.visibility = View.VISIBLE
                        binding.sendButtom.visibility = View.VISIBLE
                        stopBreathingAnimation()
                    }
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }


}
