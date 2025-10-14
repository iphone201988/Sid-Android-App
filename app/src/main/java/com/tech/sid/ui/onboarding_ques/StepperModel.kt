package com.tech.sid.ui.onboarding_ques

import androidx.databinding.ObservableField
import com.android.billingclient.api.ProductDetails

data class StepperModel(
    val isComplete: Boolean,
    val fillColor: Int,
    val fillColorShadow: Int,
)

data class StepperOnboardingModel(
    var select: Boolean, var text: String
)

data class StepperPageModel(
    var stepperOnboardingModel: List<StepperOnboardingModel>,
    var title: String,
    var textLastText: ObservableField<String> = ObservableField(""),
    var tagData: List<GetTagsData>? = null
)

data class SuggestionModel(
    var titleValue: String,
    var colorsValue: String,
    var keyName: String,
    var iselected: Boolean = false
)

data class StartPracticingModel(
    var titleValue: String,
    var colorsValue: String,
    var imageValue: Int? = 0,
    var exactText: String? = "",
    var id: String? = "",
    var iselected: Boolean? = false,
)

data class ChooseSituationModel(
    var titleValue: String,
    var colorsValue: String,
    var id: String? = "",
    var iselected: Boolean? = false,
)

data class WantToTalkModel(
    var titleValue: String,
    var colorsValue: String,
    var iselected: Boolean? = false,
)

data class SubscriptionModel(

    var titleValue: String,
    var colorsValue: String,
    var discrition: List<String>,
    var amount: String,
    var period: String,
    var titleSubValue: String,
    val type: Int?,
    var isSelected: Boolean?=false,
    var productDetails: ProductDetails? = null,
    var productId:String?=null,
)

data class FeelingHomeModel(
    var titleValue: String,
    var colorsValue: String,
    var imageValue: Int?,
    var textColor: String,
)


data class SimulationRv(
    val __v: Int?,
    val _id: String?,
    val chatId: String?,
    val createdAt: String?,
    val momentId: String?,
    val momentTitle: String?,
    val relation: String?,
    val responseStyle: String?,
    val scenarioId: String?,
    val scenarioTitle: String?,
    val simulationInsight: String?,
    val updatedAt: String?,
    val userId: String?,
    var colorMainCard: String?,
    var colorMessageCard: String?
)

data class JournalModel(
    var colorMainCard: String,
    var colorMessageCard: String,
    var date: String,
    var titleMainHeading: String,
    var titleSubHeading: String,
    var id: String? = "",

    var userId: String? = null,
    var momentId: String? = null,
    var scenarioId: String? = null,
    var responseStyle: String? = null,
    var relation: String? = null,
    var chatId: String? = null,
    var simulationInsight: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var v: Int? = null
)


data class JournalModel4(
    val description: String?,
    val simulationId: String?,
    val title: String?,
    var colorMainCard: String?,
    var colorMessageCard: String?,
    var date: String? = null,

    )

data class JournalModel2(
    var colorMainCard: String, var colorMessageCard: String, var date: String,//updatedAt
    var titleMainHeading: String,//title
    var titleSubHeading: String,//content
    var id: String? = "",//_id
    val __v: Int, val createdAt: String, val tags: List<String>, val userId: String
)


data class GetTagsResponse(
    val data: List<GetTagsData>?, val success: Boolean?
)

data class GetTagsData(
    val key: String?, val word: String?, var iselected: Boolean? = false
)


