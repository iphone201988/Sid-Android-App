package com.tech.sid.ui.dashboard.result_screen

import com.tech.sid.ui.onboarding_ques.PersonalityAnalysisData

data class GetProfileResponse(
    val DOB: String?,
    val __v: Int?,
    val _id: String?,
    val activeSubscription: Any?,
    val availableSimulations: Int?,
    val countryCode: String?,
    val createdAt: String?,
    val deactivatedAt: Any?,
    val deviceToken: Any?,
    val deviceType: Any?,
    val email: String?,
    val emotionalResponses: List<EmotionalResponse?>?,
    val empathyInteractions: List<String?>?,
    val firstName: String?,
    val isDeactivated: Boolean?,
    val isEmailVerified: Boolean?,
    val isOnboardingCompleted: Boolean?,
    val isSocialLogin: Boolean?,
    val lastName: String?,
    val message: String?,
    val onboardingCompletedAt: Any?,
    val otp: String?,
    val otpExpiresAt: String?,
    val otpVerified: Boolean?,
    val personalityAnalysis: PersonalityAnalysisData?,
    val phoneNumber: String?,
    val profileImage: String?,
    val simulationBundles: List<Any?>?,
    val socialLinkedAccounts: List<Any?>?,
    val success: Boolean?,
    val timezone: Any?,
    val updatedAt: String?,
)

data class EmotionalResponse(
    val _id: String?,
    val options: List<String>?,
    val questionId: Int?,
    val questionType: String?,
    var responseText: String?,
    var responseValue: Int?,
    val text: String?,
)

data class PersonalityAnalysis(
    val attachmentStyle: AttachmentStyle?,
    val heading: String?,
    val summary: String?,
    val topInsights: List<TopInsight?>?,
    val traits: Traits?,
    val traitsSummary: String?,

    )

data class AttachmentStyle(
    val anxious: Double?,
    val avoidant: Double?,
    val secure: Double?,
)

data class TopInsight(
    val _id: String?,
    val description: String?,
    val type: String?,
)

data class Traits(
    val agreeableness: Double?,
    val cognitiveStyle: Double?,
    val conscientiousness: Double?,
    val gratitude: Double?,
    val neuroticism: Double?,
    val openness: Double?,
)


data class PostOnboardingModel(
    val data: PersonalityAnalysisData,
    val message: String,
    val success: Boolean,
)



