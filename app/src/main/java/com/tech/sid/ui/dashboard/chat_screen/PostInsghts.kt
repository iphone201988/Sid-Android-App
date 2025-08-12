package com.tech.sid.ui.dashboard.chat_screen

data class PostInsghts(
    val insight: Insight
)

data class Insight(
    val __v: Int,
    val _id: String,
    val description: String,
    val hiddenEmotionalPatterns: List<String>,
    val keyInsights: List<String>,
    val scenarioSummary: String,
    val title: String
)