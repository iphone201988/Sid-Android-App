package com.tech.sid.ui.onboarding_ques

import android.graphics.Color
import androidx.databinding.ObservableField
import com.tech.sid.base.utils.GlowCircleView
import com.tech.sid.base.utils.GlowCircleView2

data class StepperModel(
    val isComplete: Boolean,
    val fillColor: Int,
    val fillColorShadow: Int,
)

data class StepperOnboardingModel(
    var select: Boolean,
    var text: String
)

data class StepperPageModel(
    var stepperOnboardingModel: List<StepperOnboardingModel>,
    var title: String,
    var textLastText: ObservableField<String> = ObservableField("")
)

data class SuggestionModel(
    var titleValue: String,
    var colorsValue: String,
    )

data class StartPracticingModel(
    var titleValue: String,
    var colorsValue: String,
    var imageValue: Int?=0,
    var exactText: String?="",
    var id: String?="",
    var iselected: Boolean?=false,
    )
data class ChooseSituationModel(
    var titleValue: String,
    var colorsValue: String,
    var id: String?="",
    var iselected: Boolean?=false,
)
data class WantToTalkModel(
    var titleValue: String,
    var colorsValue: String,
    var iselected: Boolean?=false,
)
data class SubscriptionModel(

    var titleValue: String,
    var colorsValue: String,
    var discrition: List<String>,
    var amount: String,
    var period: String,
    var titleSubValue: String,
)
data class FeelingHomeModel(
    var titleValue: String,
    var colorsValue: String,
    var imageValue: Int?,
    var textColor: String,
)



data class JournalModel(
    var colorMainCard: String,
    var colorMessageCard: String,
    var date: String,
    var titleMainHeading: String,
    var titleSubHeading: String,
)
