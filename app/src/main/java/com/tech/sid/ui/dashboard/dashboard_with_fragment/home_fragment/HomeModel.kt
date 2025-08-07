package com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment

data class HomeModel(
    val lastJournal: LastJournal? = null,
    val lastMood: String? = null,
    val lastSimulation: LastSimulation? = null,
    val message: String? = null,
    val moodMeta: MoodMeta? = null,
    val moodTrend: List<MoodTrend>? = null,
    val mostUsedEmotion: String? = null,
    val reflection: Reflection? = null,
    val streak: Int? = null,
    val success: Boolean? = null,
    val todayMood: String? = null
)

data class LastJournal(
    val _id: String? = null,
    val title: String? = null
)

data class LastSimulation(
    val momentTitle: String? = null,
    val scenarioTitle: String? = null
)

data class MoodMeta(
    val overall: String? = null,
    val trend: String? = null
)

data class MoodTrend(
    val date: String? = null,
    val mood: String? = null
)

data class Reflection(
    val message: String? = null,
    val suggestion: String? = null
)

data class HomeGraphModel(
    val data: Data? = null,
    val success: Boolean? = null
)

data class Data(
    val moodCounts: MoodCounts? = null,
    val moodLabel: String? = null,
    val moodTrend: List<MoodTrend>? = null,
    val mostUsedMood: String? = null,
    val streak: Int? = null
)

data class MoodCounts(
    val Drifting: Int? = null,
    val Grateful: Int? = null,
    val Low: Int? = null,
    val Overwhelmed: Int? = null,
    val Thriving: Int? = null
)

