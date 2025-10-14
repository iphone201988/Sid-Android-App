package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit

data class EditeModel(
    val message: String,
    val success: Boolean,
    val user: User,
    val users: Users
)

data class User(
    val DOB: String,
    val _id: String,
    val countryCode: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImage: String?
)

data class Users(
    val DOB: String,
    val __v: Int,
    val _id: String,
    val activeSubscription: Any,
    val availableSimulations: Int,
    val countryCode: String,
    val createdAt: String,
    val deactivatedAt: Any,
    val deviceToken: String,
    val deviceType: Int,
    val email: String,
    val emotionalResponses: List<EmotionalResponse>,
    val empathyInteractions: List<String>,
    val firstName: String,
    val isDeactivated: Boolean,
    val isEmailVerified: Boolean,
    val isOnboardingCompleted: Boolean,
    val lastName: String,
    val onboardingCompletedAt: Any,
    val otp: Any,
    val otpExpiresAt: Any,
    val otpVerified: Boolean,
    val password: String,
    val personalityAnalysis: PersonalityAnalysis,
    val phoneNumber: String,
    val profileImage: Any,
    val simulationBundles: List<Any>,
    val socialLinkedAccounts: List<Any>,
    val timezone: Any,
    val updatedAt: String
)

data class EmotionalResponse(
    val questionId: Int,
    val responseText: String,
    val responseValue: Int
)

data class PersonalityAnalysis(
    val attachmentStyle: AttachmentStyle,
    val summary: String,
    val topInsights: List<TopInsight>,
    val traits: Traits
)

data class AttachmentStyle(
    val anxious: Int,
    val avoidant: Int,
    val secure: Int
)

data class TopInsight(
    val _id: String,
    val description: String,
    val type: String
)

data class Traits(
    val agreeableness: Int,
    val conscientiousness: Int,
    val neuroticism: Int,
    val openness: Int
)