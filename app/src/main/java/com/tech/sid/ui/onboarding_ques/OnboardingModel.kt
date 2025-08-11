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
    val `data`: Data,
    val message: String,
    val success: Boolean
)

data class Data(
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
    val keyPoints: String,
//    val description: String,
    val type: String
)

data class Traits(
    val agreeableness: Int,
    @SerializedName("cognitive style")
    val cognitiveStyle: Int,
    val conscientiousness: Int,
    val gratitude: Int,
    val neuroticism: Int,
    val openness: Int
)