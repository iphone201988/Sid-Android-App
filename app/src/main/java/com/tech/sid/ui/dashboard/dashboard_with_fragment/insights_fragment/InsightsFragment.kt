package com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View

import com.tech.sid.R

import androidx.fragment.app.viewModels

import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentHomeBinding
import com.tech.sid.databinding.FragmentInsightsBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeFragmentVm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsightsFragment : BaseFragment<FragmentInsightsBinding>() {
    private val viewModel: InsightsFragmentVm by viewModels()
    override fun onCreateView(view: View) {
        val data = listOf(0, 5, 10, 10, 6, 8, 0)
        val maxY = 10
        binding.graphView.setGraphData(data, maxY)
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_insights
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}

