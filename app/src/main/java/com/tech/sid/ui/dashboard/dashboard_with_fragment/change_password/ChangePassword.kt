package com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.tech.sid.databinding.ActivityEditProfileBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.auth.LoginActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password.ForgotPassword
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditProfileVm
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ChangePassword : BaseActivity<ActivityChangePasswordBinding>() {

    private val viewModel: ChangePasswordVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_change_password
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
                        Constants.CHANGE_PASSWORD_API -> {
                            try {
                                val signUpModel: AuthModelLogin? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    binding.enterNewPassword.text?.clear()
                                    binding.enterNewConfirmPassword.text?.clear()
                                    binding.enterOldPassword.text?.clear()
                                    finish()
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
                R.id.hidePassword -> {
                    showOrHidePassword(binding.enterOldPassword,binding.hidePassword)
                }

                R.id.hidePassword2 -> {
                    showOrHidePassword(binding.enterNewPassword,binding.hidePassword2)
                }
                R.id.hidePassword3 -> {
                    showOrHidePassword(binding.enterNewConfirmPassword,binding.hidePassword3)
                }

                R.id.button -> {
                    if (binding.enterOldPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the password")
                        return@observe
                    }
                    else    if (binding.enterNewPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the password")
                        return@observe
                    } else if (binding.enterNewConfirmPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("pPlease enter the confirm password")
                        return@observe
                    }
                    else if (binding.enterOldPassword.text.toString().trim().length<6) {
                        showErrorToast("Please enter old password of min length 6")
                        return@observe
                    }
                    else if (binding.enterNewPassword.text.toString().trim().length<6) {
                        showErrorToast("Please enter new password of min length 6")
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
                        put("oldPassword", binding.enterOldPassword.text.toString().trim())
                        put("newPassword", binding.enterNewPassword.text.toString().trim())
                    }
                    viewModel.changePasswordFunction(data)
                }
                R.id.back_button -> {
                    finish()
                }
            }
        }

    }
}