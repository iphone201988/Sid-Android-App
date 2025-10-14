package com.tech.sid.ui.dashboard.edit_emotion

import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BaseCustomDialog
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.base.utils.showSuccessToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityEditEmotionalProfileBinding
import com.tech.sid.databinding.InputDialogLayoutBinding
import com.tech.sid.databinding.QuestionDialogLayoutBinding
import com.tech.sid.databinding.StepperOnboardingSubRvItemBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.dashboard.result_screen.EmotionalResponse
import com.tech.sid.ui.dashboard.result_screen.GetProfileResponse
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.onboarding_ques.GetTagsData
import com.tech.sid.ui.onboarding_ques.GetTagsResponse
import com.tech.sid.ui.onboarding_ques.PostOnboardingModel
import com.tech.sid.ui.onboarding_ques.StepperOnboardingModel
import com.tech.sid.ui.onboarding_ques.SuggestionModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditEmotionalProfileActivity : BaseActivity<ActivityEditEmotionalProfileBinding>() {
    private val viewModel: EditEmotionActivityVm by viewModels()
    lateinit var adapter: SimpleRecyclerViewAdapter<EmotionalResponse, SuggestionItemCardBinding>
    private lateinit var questionDilaog: BaseCustomDialog<QuestionDialogLayoutBinding>
    private lateinit var inputDialog: BaseCustomDialog<InputDialogLayoutBinding>
    private lateinit var optionList: ArrayList<StepperOnboardingModel>
    private lateinit var adapterTag: SimpleRecyclerViewAdapter<SuggestionModel, SuggestionItemCardBinding>
    private var tagList: List<GetTagsData> = listOf()
    override fun getLayoutResource(): Int {
        return R.layout.activity_edit_emotional_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
        viewModel.getProfileApi()
        adapter()
        viewModel.getTagsApi()

    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_button -> {
                    finish()
                }

                R.id.buttonContinue -> {
                    getPostOnboarding()
                }


            }
        }
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
                        Constants.GET_PROFILE -> {
                            try {
                                val loginModel: GetProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())

                                // Set directly in your adapter
                                adapter.list = loginModel?.emotionalResponses

                                // now itemListData is ready to use
                                // e.g. pass it to your Stepper Adapter
                                // stepperAdapter.list = itemListData

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }


                        Constants.GET_TAGS -> {
                            try {
                                Log.i("qgqg", "apiObserver: " + Gson().toJson(it.data))
                                val getTagsModel: GetTagsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getTagsModel != null && getTagsModel.success == true) {
                                    tagList = getTagsModel.data!!
                                } else {
                                    showErrorToast("Model parse exception")
                                }

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.POST_ONBOARDING_API -> {
                            try {
                                val postOnboardingModel: PostOnboardingModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (postOnboardingModel?.success == true) {
                                    showSuccessToast(postOnboardingModel.message)
                                 //   ResultActivity.onboardingResultModel = postOnboardingModel.data
                                } else {
                                    postOnboardingModel?.message?.let { it1 -> showErrorToast(it1) }
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


    fun adapter() {
        adapter = SimpleRecyclerViewAdapter(
            R.layout.item_question, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.ivEdit, R.id.tvEdit -> {
                    if (m.questionType == "text") {
                        inputDialog(m, pos)
                    } else {
                        questionDilaog(m, pos)
                    }
                }
            }
        }
        binding.rvEmotional.adapter = adapter
    }


    private fun questionDilaog(data: EmotionalResponse, position: Int) {
        questionDilaog = BaseCustomDialog(
            R.style.Dialog3, this, R.layout.question_dialog_layout
        ) { view ->
            view?.let {
                when (it.id) {
                    R.id.btnDone -> {
                        //  get selected option index
                        val selectedIndex = optionList.indexOfFirst { it.select }
                        if (selectedIndex != -1) {
                            data.responseValue = selectedIndex
                            //  update main adapter list
                            adapter.list[position] = data
                            adapter.notifyItemChanged(position)
                        }

                        questionDilaog.dismiss()
                    }

                    R.id.tvCancel -> {
                        questionDilaog.dismiss()
                    }
                }
            }
        }
        questionDilaog.window?.apply {
            setBackgroundDrawableResource(R.color.transparent_white_30)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        questionDilaog.binding.bean = data

        // prepare options list
        optionList = ArrayList<StepperOnboardingModel>() // 🔹 make it class-level var
        data.options?.forEachIndexed { index, option ->
            optionList.add(
                StepperOnboardingModel(
                    select = index == data.responseValue, text = option
                )
            )
        }
        rvRecyclerviewOnboarding(questionDilaog.binding.rvQuestion, optionList)

        questionDilaog.show()
    }

    private fun inputDialog(data: EmotionalResponse, position: Int) {
        inputDialog = BaseCustomDialog(
            R.style.Dialog3, this, R.layout.input_dialog_layout
        ) { view ->
            view?.let {
                when (it.id) {
                    R.id.btnDone -> {
                        // get text from edittext
                        data.responseText = inputDialog.binding.editNoteSuggestion.text.toString()
                        // update main adapter list
                        adapter.notifyItemChanged(position)
                        inputDialog.dismiss()
                    }

                    R.id.tvCancel -> {
                        inputDialog.dismiss()
                    }
                }
            }
        }

        inputDialog.window?.apply {
            setBackgroundDrawableResource(R.color.transparent_white_30)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        inputDialog.binding.editNoteSuggestion.setText(data.responseText)
        inputDialog.binding.bean = data
        rvRecyclerviewSuggest(inputDialog.binding.rvTag, tagList)

        data.responseText?.let { text ->
            val words = text.split(" ")
            adapterTag.list.forEachIndexed { index, item ->
                if (words.any { it.equals(item.titleValue, ignoreCase = true) }) {
                    item.iselected = true
                    adapterTag.notifyItemChanged(index)
                }
            }
        }


        inputDialog.show()
    }


    private fun rvRecyclerviewOnboarding(
        view: RecyclerView, ignore: List<StepperOnboardingModel>,
    ) {
        val adapter: SimpleRecyclerViewAdapter<StepperOnboardingModel, StepperOnboardingSubRvItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.stepper_onboarding_sub_rv_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {

                    R.id.mainLayout -> {
                        ignore.forEachIndexed { index, item ->
                            item.select = index == pos
                        }
                        (view.adapter as? SimpleRecyclerViewAdapter<*, *>)?.notifyDataSetChanged()
                    }
                }

            }
        view.adapter = adapter
        adapter.list = ignore
    }

    private fun rvRecyclerviewSuggest(view: RecyclerView, isSelected: List<GetTagsData>) {
        val itemListData = ArrayList<SuggestionModel>()
        val colors = listOf("#B5EAEA", "#FFD9DA", "#CAB8FF", "#FFDEB4", "#D0F4DE", "#FFB5E8")
        isSelected.forEachIndexed { index, tagData ->
            val randomColor = colors.random() // pick random color
            itemListData.add(
                SuggestionModel(
                    "#" + tagData.word.toString(), randomColor, tagData.key.toString()
                )
            )
        }

        adapterTag = SimpleRecyclerViewAdapter(
            R.layout.suggestion_item_card, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    val currentText = inputDialog.binding.editNoteSuggestion.text.toString()
                    inputDialog.binding.editNoteSuggestion.setText(currentText + m.titleValue + " ")
                    inputDialog.binding.editNoteSuggestion.text?.let {
                        inputDialog.binding.editNoteSuggestion.setSelection(
                            it.length
                        )
                    }

                    // toggle selection
                    m.iselected = !m.iselected
                    isSelected[pos].iselected = m.iselected
                    adapterTag.notifyItemChanged(pos)
                }
            }


        }
        view.adapter = adapterTag
        adapterTag.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    private fun getPostOnboarding() {
        try {
            val finalAnswers = mutableListOf<Any>()

            adapter.list.forEach { item ->
                when (item.questionType?.lowercase()) {
                    "mcq" -> {
                        val value = item.responseValue ?: 1
                        finalAnswers.add(value)
                    }

                    "text" -> {
                        finalAnswers.add(item.responseText ?: "")
                    }
                }
            }

            // validation
            if (finalAnswers.isNotEmpty() && finalAnswers.last().toString().isEmpty()) {
                showErrorToast("Please enter the usually respond")
                return
            }

            // get selected tags safely
            val selectedTags = try {
                adapterTag.list.filter { it.iselected }
            } catch (e: Exception) {
                emptyList()
            }

            val tagsCategories = selectedTags.groupBy { it.keyName }.mapValues { it.value.size }

            val tags = selectedTags.map { it.titleValue.removePrefix("#") }

            // final map for API
            val data = HashMap<String, Any>().apply {
                put("answers", finalAnswers)
                if (tagsCategories.isNotEmpty()) put("tags_categories", tagsCategories)
                if (tags.isNotEmpty()) put("tags", tags)
            }

            Log.i("ewgwg", "getPostOnboarding: " + Gson().toJson(data))
            viewModel.postOnboardingApi(data)

        } catch (e: Exception) {
            e.printStackTrace()
            showErrorToast("Something went wrong while preparing data")
        }
    }


    data class PostOnboardingRequest(
        val answers: List<Any>,
        val tags_categories: Map<String, Int>?,
        val tags: List<String>?,
    )

    private fun getSelectedTagsFromAdapter(): List<SuggestionModel> {
        return adapterTag.list.filter { it.iselected }
    }
}