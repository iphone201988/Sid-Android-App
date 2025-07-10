package com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVm by viewModels()
    override fun onCreateView(view: View) {
        val data = listOf(0, 5, 10, 10, 6, 8, 0)
        val maxY = 10
        binding.graphView.setGraphData(data, maxY)
    }
    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}
