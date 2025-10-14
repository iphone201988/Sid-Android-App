package com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment

data class InsightsModel(
    val summaries: List<Summary>?
)

data class Summary(
    val description: String?,
    val simulationId: String?,
    val title: String?
)