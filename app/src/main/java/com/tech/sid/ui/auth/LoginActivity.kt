package com.tech.sid.ui.auth

import android.content.Intent
import android.text.InputType
import androidx.activity.viewModels
import com.google.android.gms.common.internal.service.Common
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityLoginBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password.ForgotEmailPassword
import com.tech.sid.ui.dashboard.start_practicing.StartPracticing
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()

    }

    private fun loginFunction() {
        val data = HashMap<String, Any>()
        data["email"] = binding.enterEmail.text.toString().trim()
        data["password"] = binding.enterPassword.text.toString().trim()
        data["deviceToken"] = "swdqwew2eqweqweqwe"
        data["deviceType"] = "1"
        viewModel.loginFunction(data)
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
                        Constants.LOGIN_API -> {
                            try {
                                val loginModel: AuthModelLogin? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (loginModel?.success == true) {
                                    sharedPrefManager.setLoginData(it.data.toString())
                                    startActivity(Intent(this, DashboardActivity::class.java))
                                } else {
                                    loginModel?.message?.let { it1 -> showErrorToast(it1) }
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
                R.id.forgot_password->{
                    startActivity(Intent(this, ForgotEmailPassword::class.java))
                }
                R.id.button -> {
                    if (binding.enterEmail.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the email id")
                        return@observe
                    } else if (binding.enterPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the password")
                        return@observe
                    }
                    loginFunction()

                }

                R.id.back_button -> {
                    finish()
                }

                R.id.hideIcon -> {
                    showOrHidePassword()
                }

                R.id.sign_up -> {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            }
        }

    }

    private fun showOrHidePassword() {
        if (binding.enterPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.hideIcon.setImageResource(R.drawable.unhide_password)
            binding.enterPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.hideIcon.setImageResource(R.drawable.hide_password)
            binding.enterPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.enterPassword.setSelection(binding.enterPassword.length())
    }
}