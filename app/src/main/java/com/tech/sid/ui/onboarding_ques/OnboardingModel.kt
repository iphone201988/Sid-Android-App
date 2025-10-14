package com.tech.sid.ui.onboarding_ques

import com.google.gson.annotations.SerializedName

data class OnboardingModel(
    val limit: Int,
    val message: String,
    val page: Int,
    val questions: List<Question>,
    val success: Boolean,
    val total: Int,
    val totalPages: Int
)

data class Question(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isActive: Boolean,
    val options: List<String>,
    val questionId: Int,
    val questionType: String,
    val text: String,
    val updatedAt: String
)


data class PostOnboardingModel(
    val data: PersonalityAnalysisData,
    val message: String,
    val success: Boolean
)

data class PersonalityAnalysisData(
    val attachmentStyle: AttachmentStyle?,
    val heading: String?,
    val summary: String?,
    val topInsights: List<TopInsight?>?,
    val traits: Traits?,
    val traitsSummary: String?
)

data class AttachmentStyle(
    val anxious: Int?,
    val avoidant: Int?,
    val secure: Int?
)

data class TopInsight(
    val description: String?,
    val type: String?
)

data class Traits(
    val agreeableness: Double?,
    val cognitiveStyle: Double?,
    val conscientiousness: Double?,
    val gratitude: Double?,
    val neuroticism: Double?,
    val openness: Double?
)