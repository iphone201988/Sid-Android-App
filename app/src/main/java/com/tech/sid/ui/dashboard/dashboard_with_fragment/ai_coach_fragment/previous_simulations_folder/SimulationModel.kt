package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder

data class SimulationModel(
    val message: String,
    val simulations: List<Simulation>,
    val success: Boolean
)

data class Simulation(
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
    val userId: String?
)