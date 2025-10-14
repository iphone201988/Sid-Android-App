package com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChangePasswordBinding
import com.tech.sid.databinding.ActivityForgotEmailPasswordBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password.verify_otp.OtpVerifyForgot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotEmailPassword : BaseActivity<ActivityForgotEmailPasswordBinding>() {
    private val viewModel: ChangePasswordVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_forgot_email_password
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
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
                        Constants.FORGOT_PASSWORD -> {
                            try {
                                val signUpModel: ForgotEmailId? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    startActivity(Intent(this, OtpVerifyForgot::class.java))
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
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if (binding.enterOldPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the email id")
                        return@observe
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(binding.enterOldPassword.text?.trim().toString())
                            .matches()
                    ) {
                        showToast("Please enter valid email id")
                        return@observe
                    }
                    OtpVerifyForgot.email = binding.enterOldPassword.text.toString().trim()
                    val data = HashMap<String, Any>().apply {

                        put("email", binding.enterOldPassword.text.toString().trim())
                    }
                    viewModel.forgotPasswordFunction(data)

                }

                R.id.back_button -> {
                    finish()
                }
            }
        }

    }
}

data class ForgotEmailId(
    val message: String,
    val success: Boolean,
    val user: User
)

data class User(
    val _id: String,
    val email: String,
    val profileImage: Any
)