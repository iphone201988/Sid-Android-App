package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.FragmentAICoachBinding
import com.tech.sid.databinding.StartPracticingItemBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.dashboard.choose_situation.ChooseSituation
import com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder.PreviousSimulations
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.start_practicing.ModelStartPracticing
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import com.tech.sid.ui.onboarding_ques.describe.DescribeYourScenarioActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AICoachFragment : BaseFragment<FragmentAICoachBinding>() {
    private val viewModel: AICoachFragmentVm by viewModels()
    private lateinit var adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItemBinding>
    var valueProfile: AuthModelLogin? = null
    override fun onCreateView(view: View) {
        initOnClick()
        apiObserver()
        viewModel.getAiCoachFunction()
        initRecyclerview(binding.rvgetStarted)
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile != null) {
            binding.apply {
                textView.text =
                    "Hi ,${valueProfile?.user?.firstName} ${valueProfile?.user?.lastName}"
                timeDate.text = getCurrentFormattedDate()
            }
        }
    }

    private fun getCurrentFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }

    private fun apiObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {
                        Constants.GET_EMPATHY_OPTIONS_API -> {
                            try {
                                val getModelStartPracticing: ModelStartPracticing? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelStartPracticing?.success == true) {

                                    val itemListData = ArrayList<StartPracticingModel>()
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Boundaries",
                                            "#E9FFFF",
                                            R.drawable.icon_divide_solid,
                                            "Boundaries",
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Ghosting or \nEmotional Distance",
                                            "#FFFFFF",
                                            R.drawable.ghost_icon,
                                            "Ghosting or Emotional Distance"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Criticism &\n" + "Judgment",
                                            "#FFEEEE",
                                            R.drawable.thumb_down,
                                            "Criticism & Judgment"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Guilt or " + "Emotional Pressure",
                                            "#F0EBFF",
                                            R.drawable.sad_face,
                                            "Guilt or Emotional Pressure"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Miscommunication",
                                            "#E9FFFF",
                                            R.drawable.line_md_heart,
                                            "Miscommunication"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Conflict",
                                            "#FFFFFF",
                                            R.drawable.icon_heartbreak,
                                            "Conflict"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Feeling\n" + "Undervalued",
                                            "#FFEEEE",
                                            R.drawable.line_graph,
                                            "Feeling Undervalued"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Closeness &\n" + "Reassurance",
                                            "#F0EBFF",
                                            R.drawable.smile_facing,
                                            "Closeness & Reassurance"
                                        )
                                    )
                                    val colors = listOf(
                                        "#E9FFFF",
                                        "#FFFFFF",
                                        "#FFEEEE",
                                        "#F0EBFF",
                                    )

                                    for (i in itemListData.indices) {
                                        val colorIndex = i % colors.size
                                        itemListData[i].colorsValue = colors[colorIndex]
                                    }


                                    for (apiItem in getModelStartPracticing.data) {
                                        val matchedItem = itemListData.find { it.exactText == apiItem.title }
                                        matchedItem?.id = apiItem._id ?: "0"
                                    }
                                    itemListData.removeAll { its -> its.id.isNullOrEmpty() }
                                    adapter.list = itemListData
                                    binding.button.visibility=View.VISIBLE
                                    binding.tvNoneOfthese.visibility=View.VISIBLE
                                    binding.startJournalingLL.visibility=View.VISIBLE
                                } else {
                                    getModelStartPracticing?.message?.let { it1 ->
                                        showErrorToast(
                                            it1
                                        )
                                    }
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

    private fun initRecyclerview(view: RecyclerView) {
        adapter =
            SimpleRecyclerViewAdapter(
                R.layout.start_practicing_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        CommonFunctionClass.singleSelectionRV(
                            list = adapter.list,
                            selectedId = m.id,
                            getId = { it.id },
                            isSelectedGetter = { it.iselected == true },
                            isSelectedSetter = { item, isSelected, selectedModel ->
                                item.iselected = isSelected
                                if (isSelected) {

                                    BindingUtils.interactionModelPost?.momentId =
                                        m.id ?: ""
                                }
                            },
                            notifyChanged = { adapter.notifyItemChanged(it) }
                        )
                    }
                }
            }
        view.adapter = adapter
        view.isNestedScrollingEnabled = true
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_a_i_coach
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private fun initOnClick() {
        viewModel.onClick.observe(requireActivity()) {
            when (it?.id) {
                R.id.bellNotification -> {
                    startActivity(Intent(requireActivity(), NotificationActivity::class.java))

                }

                R.id.button -> {
                    startActivity(Intent(requireActivity(), PreviousSimulations::class.java))
                }

                R.id.start_journalingLL -> {
                   /* if (binding.somethingIMGoingThrough.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter notes")
                        return@observe
                    }*/
                    val momentId = BindingUtils.interactionModelPost?.momentId?.trim()
                    if (momentId.isNullOrEmpty()) {
                        showErrorToast("Please select empathy coach")
                        return@observe
                    }

                    startActivity(Intent(requireActivity(), ChooseSituation::class.java))
                }

                R.id.tvNoneOfthese -> {
                    val intent = Intent(requireActivity(), DescribeYourScenarioActivity::class.java)
                    val lastId = adapter.list.last().id
                    intent.putExtra("momentId", lastId)
                    startActivity(intent)

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BindingUtils.interactionModelPost = null
    }
}
