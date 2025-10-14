package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_fragment

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.tech.sid.ImagePickerHelper
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BaseCustomDialog
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.base.utils.showToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.FragmentProfileBinding
import com.tech.sid.databinding.LogoutDeleteLayoutBinding
import com.tech.sid.databinding.SelectMediaFileBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.auth.LoginActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.FragmentNavRoute
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePassword
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditProfile
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditeModel
import com.tech.sid.ui.dashboard.edit_emotion.EditEmotionalProfileActivity
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.dashboard.subscription_package.SubscriptionActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileFragmentVm by viewModels()
    private lateinit var dialogFile: BaseCustomDialog<SelectMediaFileBinding>
    private lateinit var logOutDelete: BaseCustomDialog<LogoutDeleteLayoutBinding>
    private lateinit var imagePickerHelper: ImagePickerHelper
    private var valueProfile: AuthModelLogin? = null
    override fun onCreateView(view: View) {
        initOnClick()
        launcherFunction()
        apiObserver()
        profileFunction()
    }

    private fun profileFunction() {
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile?.user?.isSocialLogin==true){
            binding.changePasswordCard.visibility=View.GONE
        }
        else{
            binding.changePasswordCard.visibility=View.VISIBLE
        }
        loadProfileImage(requireActivity(), binding.profileIcon, valueProfile?.user?.profileImage)
    }

    private fun loadProfileImage(
        context: Context, imageView: AppCompatImageView, profileImage: String?
    ) {
        val fullImageUrl = when {
            profileImage.isNullOrBlank() -> null
            profileImage.contains(Constants.BASE_URL_PHOTO) -> profileImage
            else -> Constants.BASE_URL_PHOTO + profileImage
        }

        if (fullImageUrl != null) {
            Glide.with(context).load(fullImageUrl).placeholder(R.drawable.progress_animation_small)
                .error(R.drawable.dummy_profile).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.dummy_profile)
        }
    }

    private fun apiObserver() {

        viewModel.observeCommon.observe(requireActivity()) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {
                        Constants.LOGOUT -> {
                            sharedPrefManager.clear()

                            val responseModelModel: EditeModel? =
                                BindingUtils.parseJson(it.data.toString())
                            showToast(responseModelModel?.message ?: "")
                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                requireActivity().finishAffinity()
                            }, 500)
                        }

                        Constants.DELETE_ACCOUNT -> {
                            sharedPrefManager.clear()
                            val responseModelModel: EditeModel? =
                                BindingUtils.parseJson(it.data.toString())
                            showToast(responseModelModel?.message ?: "")
                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                requireActivity().finishAffinity()
                            }, 500)
                        }

                        Constants.PUT_EDIT_PROFILE -> {
                            try {
                                val responseModelModel: EditeModel? =
                                    BindingUtils.parseJson(it.data.toString())

                                valueProfile = sharedPrefManager.getProfileData()
                                valueProfile?.user?.DOB = responseModelModel?.user?.DOB ?: ""
                                valueProfile?.user?._id = responseModelModel?.user?._id ?: ""
                                valueProfile?.user?.countryCode =
                                    responseModelModel?.user?.countryCode ?: ""
                                valueProfile?.user?.email = responseModelModel?.user?.email ?: ""
                                valueProfile?.user?.firstName =
                                    responseModelModel?.user?.firstName ?: ""
                                valueProfile?.user?.lastName =
                                    responseModelModel?.user?.lastName ?: ""
                                valueProfile?.user?.phoneNumber =
                                    responseModelModel?.user?.phoneNumber ?: ""
                                valueProfile?.user?.profileImage =
                                    (Constants.BASE_URL_PHOTO + responseModelModel?.user?.profileImage)
                                        ?: ""
                                sharedPrefManager.setLoginData(Gson().toJson(valueProfile))
                                Glide.with(requireActivity())
                                    .load(Constants.BASE_URL_PHOTO + responseModelModel?.user?.profileImage)
                                    .into(binding.profileIcon)
                                responseModelModel?.message?.let { it1 -> showToast(it1) }
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

    private fun launcherFunction() {
        imagePickerHelper = ImagePickerHelper(this) { multipartPart, imageUri ->
            multipartPart?.let {
                val map = HashMap<String, RequestBody>()
//                map["firstName"] = createPartFromString("John")
                viewModel.postEditFunction(map, multipartPart)
            }
            imageUri?.let {
                binding.profileIcon.setImageURI(it)

            }
        }
    }

    private fun clickFileUpload() {
        dialogFile = BaseCustomDialog(
            R.style.Dialog2, requireActivity(), R.layout.select_media_file
        ) { view ->
            view?.let {
                when (it.id) {
                    R.id.tv_title -> {
                        imagePickerHelper.pickImage(
                            ImagePickerHelper.ImagePickerAction.Camera, BindingUtils.permissions
                        )
                        dialogFile.dismiss()
                    }

                    R.id.tv_title_one -> {
                        imagePickerHelper.pickImage(
                            ImagePickerHelper.ImagePickerAction.Gallery, BindingUtils.permissions
                        )
                        dialogFile.dismiss()
                    }

                    R.id.tv_cancel -> {
                        dialogFile.dismiss()
                    }
                }
            }
        }

        dialogFile.window?.apply {
            setBackgroundDrawableResource(R.color.transparent_white_30)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        dialogFile.show()
    }

    private fun logoutDelete(isLogout: Boolean) {

        logOutDelete = BaseCustomDialog(
            R.style.Dialog2, requireActivity(), R.layout.logout_delete_layout
        ) { view ->
            view?.let {
                when (it.id) {
                    R.id.yesButton -> {
                        if (isLogout) {
                            viewModel.logoutFunction()
                        } else {
                            viewModel.deleteFunction()
                        }

                        logOutDelete.dismiss()

                    }

                    R.id.tvCancel -> {
                        logOutDelete.dismiss()
                    }
                }
            }
        }
        if (isLogout) {
            logOutDelete.binding.tvTitle.text = "Are you sure you want to logout"
        } else {
            logOutDelete.binding.tvTitle.text = "Are you sure you want to delete"
        }
        if (isLogout) {
            logOutDelete.binding.yesButton.text = "Logout"
        } else {
            logOutDelete.binding.yesButton.text = "Delete"
        }
        logOutDelete.window?.apply {
            setBackgroundDrawableResource(R.color.transparent_white_30)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        logOutDelete.show()
    }

    private var fragmentNavRoute: FragmentNavRoute? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentNavRoute = context as FragmentNavRoute
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

    val termsUrl = "https://thryve-os-copy-53bbf16d.base44.app/terms"
    val privacyUrl = "https://thryve-os-copy-53bbf16d.base44.app/privacy-policy"

    private fun initOnClick() {
        viewModel.onClick.observe(requireActivity()) {
            when (it?.id) {
                R.id.editProfileCard -> {
                    startActivity(Intent(requireActivity(), EditProfile::class.java))
                }

                R.id.changePasswordCard -> {
                    startActivity(Intent(requireActivity(), ChangePassword::class.java))
                }

                R.id.notificationCard -> {
                    startActivity(Intent(requireActivity(), NotificationActivity::class.java))
                }

                R.id.subscription -> {
                    startActivity(Intent(requireActivity(), SubscriptionActivity::class.java))
                }

                R.id.profileIcon -> {
                    clickFileUpload()
                }

                R.id.back_button -> {
                    if (fragmentNavRoute != null) {
                        fragmentNavRoute?.fragmentNavRoute(1)
                    }
                }

//                R.id.back_button -> {
//                    if (fragmentNavRoute != null) {
//                        fragmentNavRoute?.fragmentNavRoute(1)
//                    }
//                }

                R.id.logout -> {
                    logoutDelete(true)
                }

                R.id.personalityProfileCard -> {
                    val intent = Intent(requireActivity(), ResultActivity::class.java)
                    intent.putExtra("from", "profile")
                    startActivity(intent)
                }

                R.id.editEmotionalProfileCard -> {
                    val intent = Intent(requireActivity(), EditEmotionalProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.delete -> {
                    logoutDelete(false)
                }

                R.id.tvPolicy -> {
                    val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
                    startActivity(intent)
                }

                R.id.tvTerm -> {
                    val intent = Intent(Intent.ACTION_VIEW, termsUrl.toUri())
                    startActivity(intent)
                }
            }
        }
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}
