package com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password

import android.content.Intent
import android.text.InputType
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm
import androidx.activity.viewModels

import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.BindingUtils.showOrHidePassword
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.ApiHelperImpl
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChangePasswordBinding
import com.tech.sid.databinding.ActivityForgotPasswordBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.auth.LoginActivity


import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPassword : BaseActivity<ActivityForgotPasswordBinding>() {

    private val viewModel: ChangePasswordVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_forgot_password
    }

    companion object {
        var email: String = ""
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
                        Constants.RESET_PASSWORD -> {
                            try {
                                val signUpModel: AuthModelLogin? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    ApiHelperImpl.bearer = null
                                    startActivity(Intent(this, LoginActivity::class.java))
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
                R.id.hide_password -> {
                    showOrHidePassword(binding.enterNewPassword,binding.hidePassword)
                }

                R.id.hide_password2 -> {
                    showOrHidePassword(binding.enterNewConfirmPassword,binding.hidePassword2)
                }

                R.id.button -> {
                    if (binding.enterNewPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the password")
                        return@observe
                    } else if (binding.enterNewConfirmPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the confirm password")
                        return@observe
                    }
                    else if (binding.enterNewPassword.text.toString().trim().length<6) {
                        showErrorToast("Please enter password of min length 6")
                        return@observe
                    }
                    else if (binding.enterNewConfirmPassword.text.toString().trim().length<6) {
                        showErrorToast("Please enter confirm password of min length 6")
                        return@observe
                    }

                    else if (binding.enterNewConfirmPassword.text.toString()
                            .trim() != binding.enterNewPassword.text.toString().trim()
                    ) {
                        showErrorToast("Password and confirm password not match")
                        return@observe
                    }
                    val data = HashMap<String, Any>().apply {
                        put("email", ForgotPassword.email)
                        put("newPassword", binding.enterNewConfirmPassword.text.toString().trim())
                    }
                    viewModel.resetPasswordFunction(data)
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }

    }
}