package com.tech.sid.ui.dashboard.dashboard_with_fragment

import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityDashboardBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.AICoachFragment
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeFragment
import com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment.InsightsFragment
import com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalFragment
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_fragment.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), OnDataPass {
    private val viewModel: DashboardActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
    }

    private fun initOnClick() {
        binding.isSelectedNav = 1
        fragmentTransaction(binding.isSelectedNav ?: 1)
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {

                }

                R.id.nav_home -> {

                    binding.isSelectedNav = 1
                    fragmentTransaction(binding.isSelectedNav ?: 1)
                }

                R.id.aiCoach -> {

                    binding.isSelectedNav = 2
                    fragmentTransaction(binding.isSelectedNav ?: 1)
                }

                R.id.Journal -> {
                    binding.isSelectedNav = 3
                    fragmentTransaction(binding.isSelectedNav ?: 1)
                }

                R.id.Insights -> {
                    binding.isSelectedNav = 4
                    fragmentTransaction(binding.isSelectedNav ?: 1)
                }

                R.id.Profile -> {
                    binding.isSelectedNav = 5
                    fragmentTransaction(binding.isSelectedNav ?: 1)
                }
            }
        }
    }
    private val customBackStack = mutableListOf<Int>()
    private var currentSelectedId = -1
    private fun fragmentTransaction(selected: Int) {
        if (selected == currentSelectedId) return
        runOnUiThread {
            val fragment = getSelectedFragment(selected)
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
            currentSelectedId = selected
            customBackStack.removeAll { it == selected }
            customBackStack.add(selected)
        }
    }
    private fun getSelectedFragment(selected: Int): Fragment {
        return fragmentCreators[selected]?.invoke() ?: HomeFragment()
    }
    private val fragmentCreators = mutableMapOf(
        1 to { HomeFragment() },
        2 to { AICoachFragment() },
        3 to { JournalFragment() },
        4 to { InsightsFragment() },
        5 to { ProfileFragment() }
    )
    override fun onDataPass(data: Int) {
        when (data) {
            1->{
                customBackStack.clear()
                fragmentTransaction(1)
                currentSelectedId = 1
                binding.isSelectedNav = 1
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (currentSelectedId == 1) {
            customBackStack.clear()
        }
        if (customBackStack.size > 1) {
            customBackStack.removeAt(customBackStack.size - 1)
            val previousId = customBackStack.last()
            if (previousId == 1) {
                customBackStack.clear()
            }
            fragmentTransaction(previousId)
            currentSelectedId = previousId
            binding.isSelectedNav = previousId
        } else {
            if (currentSelectedId == 1) {
                finishAffinity()
            } else {
                currentSelectedId = 1
                binding.isSelectedNav = 1
                fragmentTransaction(1)
            }
        }
    }
}