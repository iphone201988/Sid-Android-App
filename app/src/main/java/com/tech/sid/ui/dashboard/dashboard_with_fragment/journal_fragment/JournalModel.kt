package com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment
//
//data class JournalModel(
//    val message: String,
//    val simulations: List<Simulation>,
//    val success: Boolean
//)
//
//data class Simulation(
//    val __v: Int,
//    val _id: String,
//    val chatId: String,
//    val createdAt: String,
//    val momentId: String,
//    val momentTitle: String,
//    val relation: String,
//    val responseStyle: String,
//    val scenarioId: String,
//    val scenarioTitle: String,
//    val simulationInsight: String,
//    val updatedAt: String,
//    val userId: String
//)
data class JournalModel(
    val `data`: List<Data>,
    val message: String,
    val pagination: Pagination,
    val success: Boolean
)

data class Data(
    val __v: Int,
    val _id: String,
    val content: String,
    val createdAt: String,
    val tags: List<String>,
    val title: String,
    val updatedAt: String,
    val userId: String
)

data class Pagination(
    val limit: Int,
    val page: Int,
    val total: Int,
    val totalPages: Int
)