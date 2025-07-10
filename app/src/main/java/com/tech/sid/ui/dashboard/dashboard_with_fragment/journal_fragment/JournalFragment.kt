package com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentHomeBinding
import com.tech.sid.databinding.FragmentJournalBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.AICoachFragmentVm
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
