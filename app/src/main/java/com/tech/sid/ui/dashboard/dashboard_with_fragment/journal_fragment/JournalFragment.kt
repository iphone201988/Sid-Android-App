package com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentJournalBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class JournalFragment : BaseFragment<FragmentJournalBinding>() {
    private val viewModel: JournalFragmentVm by viewModels()
    override fun onCreateView(view: View) {

    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_journal
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}
