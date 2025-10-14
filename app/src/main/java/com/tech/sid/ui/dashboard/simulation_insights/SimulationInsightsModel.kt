package com.tech.sid.ui.dashboard.simulation_insights
data class SummaryResponse(
    val summaries: List<SimulationInsightsResponse>?
)
data class SimulationInsightsResponse(
    val createdAt: String?,
    val insight: SimulationInsightsModel?,
    val simulationId: String?,
    val scenarioTitle: String?,
    val momentTitle: String?,
    val customMomentText: String?,
    val simulationInsightFound: Boolean
)
data class SimulationInsightsModel(
    val nextSteps: List<String?>?,
    val relationalPattern: String?,
    val whatsReallyGoingOn: String?
)


