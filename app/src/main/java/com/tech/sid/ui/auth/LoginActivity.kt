package com.tech.sid.ui.auth

import android.content.Intent
import android.graphics.Color
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.BindingUtils.showOrHidePassword
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityLoginBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password.ForgotEmailPassword
import com.tech.sid.ui.onboarding_ques.OnboardingStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var deviceToken:String?=null
    override fun getLayoutResource(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initView()
        initOnClick()
        apiObserver()


    }

    private fun initView() {
        // google login initialisation
        initGoogleLogin()

        // fcm token
        getFCMToken()

        // terms & conditions spannable text
        val textView = binding.tvTerm
        val termsUrl = "https://thryve-os-copy-53bbf16d.base44.app/terms"
        val privacyUrl = "https://thryve-os-copy-53bbf16d.base44.app/privacy-policy"

        val text = "By signing up, you consent to ThryveOS\nTerms of Use and Privacy Policy."
        val spannable = SpannableString(text)
        val highlightColor = ContextCompat.getColor(this, R.color.stepper_color_1)

// Terms of Use clickable
        val startTerms = text.indexOf("Terms of Use")
        val endTerms = startTerms + "Terms of Use".length
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, termsUrl.toUri())
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = highlightColor
            }
        }, startTerms, endTerms, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

// Privacy Policy clickable
        val startPrivacy = text.indexOf("Privacy Policy")
        val endPrivacy = startPrivacy + "Privacy Policy".length
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = highlightColor
            }
        }, startPrivacy, endPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

// Enable clicking
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    private fun loginFunction() {
        val data = HashMap<String, Any>()
        data["email"] = binding.enterEmail.text.toString().trim()
        data["password"] = binding.enterPassword.text.toString().trim()
        data["deviceToken"] = deviceToken.toString()
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
                                    if (loginModel.user.isOnboardingCompleted) {
                                        val intent =Intent(this, DashboardActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                    } else {
                                        startActivity(Intent(this, OnboardingStart::class.java))

                                    }

                                } else {
                                    loginModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.SOCIAL_LOGIN_API -> {
                            try {
                                val loginModel: AuthModelLogin? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (loginModel?.success == true) {
                                    sharedPrefManager.setLoginData(it.data.toString())
                                    if (loginModel.user.isOnboardingCompleted) {
                                        val intent =Intent(this, DashboardActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                    } else {
                                        startActivity(Intent(this, OnboardingStart::class.java))
                                    }

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
                R.id.forgot_password -> {
                    startActivity(Intent(this, ForgotEmailPassword::class.java))
                }

                R.id.button -> {
                    if (binding.enterEmail.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the email id")
                        return@observe
                    }
                    else if (!Patterns.EMAIL_ADDRESS.matcher(binding.enterEmail.text?.trim().toString())
                            .matches()
                    ) {
                        showToast("Please enter valid email id")
                        return@observe
                    }

                    else if (binding.enterPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter the password")
                        return@observe
                    }
                    else if (binding.enterPassword.text.toString().trim().length<6) {
                        showErrorToast("Please enter password of min length 6")
                        return@observe
                    }
                    loginFunction()

                }

                R.id.back_button -> {
                    finish()
                }

                R.id.hideIcon -> {
                    showOrHidePassword(binding.enterPassword,binding.hideIcon)
                }

                R.id.sign_up -> {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }

                R.id.googleLogin -> {
                    signIn()
                }
            }
        }

    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
             deviceToken = it.result
            Log.d("deviceToken", it.result)
        }
    }

    private fun initGoogleLogin() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                Log.e("result_google", "${result.resultCode}: ")
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "signInResult:failed code=${e.statusCode}", e)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.let {
            val id = it.id ?: ""
            val email = it.email?:""
            val displayName = it.displayName ?: ""

            // Split first and last name
            val nameParts = displayName.trim().split(" ", limit = 2)
            val firstName = nameParts.getOrNull(0) ?: ""
            val lastName = nameParts.getOrNull(1) ?: ""
            socialLoginApi(email,firstName,lastName,id)

            Log.d(
                "GoogleSignIn",
                "Signed in as: First Name = $firstName, Last Name = $lastName, Email = $email, ID = $id"
            )
        } ?: run {
            Log.e("GoogleSignIn", "Login failed: account is null")
        }
    }

    private fun socialLoginApi(email:String,firstName:String,lastName: String,socialId:String){
        val request = HashMap<String, Any>()
        request["email"]=email
        request["deviceType"]=1
        request["deviceToken"]=deviceToken.toString()
        request["socialId"]=socialId
        request["provider"]=1
        request["firstName"]=firstName
        if (!lastName.isNullOrEmpty()){
            request["lastName"]=lastName
        }
        viewModel.socialLoginApi(request)
    }
}