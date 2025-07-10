package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_fragment

import android.content.Context
import android.content.Intent
import android.view.View

import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel

import com.tech.sid.databinding.FragmentProfileBinding
import com.tech.sid.ui.auth.OtpVerify
import com.tech.sid.ui.auth.SignUpActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.OnDataPass
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePassword
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditProfile

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileFragmentVm by viewModels()
    override fun onCreateView(view: View) {

        initOnClick()
    }

    private var dataPassListener: OnDataPass? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPassListener = context as OnDataPass
    }
    override fun getLayoutResource(): Int {
        return R.layout.fragment_profile
    }

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
                R.id.back_button -> {
                    if(dataPassListener!=null){
                        dataPassListener?.onDataPass(1)
                    }

                }
            }
        }
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}
