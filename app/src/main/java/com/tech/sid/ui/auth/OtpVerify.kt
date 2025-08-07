package com.tech.sid.ui.auth

import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityOtpVerifyBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OtpVerify : BaseActivity<ActivityOtpVerifyBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_otp_verify
    }

    companion object{
        var email:String=""
    }
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        onClick()
        initOnClick()
        initView()
        apiObserver()
        startCountdown()
    }
    private val handler = Handler(Looper.getMainLooper())
    private var secondsLeft = 60
    fun startCountdown() {
        secondsLeft = 60
        handler.post(countdownRunnable)
    }
    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (secondsLeft > 0) {
                secondsLeft--
                binding.countDown.text=secondsLeft.toString()
                handler.postDelayed(this, 1000)
            } else {
                binding.countDown.text="Resend Otp"
                Log.d("Countdown", "Timer finished!")
            }
        }
    }


    private fun onClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_button -> {
                    finish()
                }
                R.id.countDown -> {
                    if(binding.countDown.text.toString().trim().equals("Resend Otp")){

                    }
                }
                R.id.button -> {
                    if (binding.otpET1.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the otp")
                        return@observe
                    } else if (binding.otpET2.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the otp")
                        return@observe
                    } else if (binding.otpET3.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the otp")
                        return@observe
                    } else if (binding.otpET4.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the otp")
                        return@observe
                    }
                    signUpFunction()
                }
            }
        }
    }

    private fun signUpFunction() {
        var fullOtp: String=binding.otpET1.text.toString().trim()+binding.otpET2.text.toString().trim()+binding.otpET3.text.toString().trim()+binding.otpET4.text.toString().trim()

        val data = HashMap<String, Any>().apply {
            put("type", 1)
            put("email", email)
            put("otp", fullOtp)
        }
        viewModel.otpVerificationFunction(data)
    }

    private fun apiObserver() {
        viewModel.observeCommon.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {
                        Constants.VERIFICATION_API -> {
                            try {
                                val signUpModel: AuthModelLogin? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    sharedPrefManager.setLoginData(it.data.toString())
                                    startActivity(Intent(this, ConsentActivity::class.java))
                                } else {
                                    signUpModel?.message?.let { it1 -> showErrorToast(it1) }
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

    private fun initOnClick() {

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                Log.d("Keyboard", "Visible")
            } else {
                binding.apply {
                    otpET1.clearFocus()
                    otpET2.clearFocus()
                    otpET3.clearFocus()
                    otpET4.clearFocus()
                }

            }
        }

        binding.otpET1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.otpET2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.otpET3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isNotEmpty()) {
//                    binding.otpET3.setBackgroundResource(R.drawable.green_btn_bg)
//                } else {
//                    binding.otpET3.setBackgroundResource(R.drawable.track_background)
//                }
            }

        })

        binding.otpET4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private lateinit var otpETs: Array<AppCompatEditText?>
    var isOtpComplete = false
    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1,
            binding.otpET2,
            binding.otpET3,
            binding.otpET4
        )
        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    // Check if all OTP fields are filled
                    isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()  // Clear the previous EditText before focusing
                            requestFocus()
                        }
                    }
                }
                // Check if all OTP fields are filled
                isOtpComplete = otpETs.all { it!!.text?.isNotEmpty() == true }

                false
            }
        }
    }

}