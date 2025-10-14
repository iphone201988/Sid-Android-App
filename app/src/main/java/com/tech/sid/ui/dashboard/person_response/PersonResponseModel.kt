package com.tech.sid.ui.dashboard.person_response

/*
data class PersonResponseModel(
    val `data`: List<String>,
    val message: String,
    val success: Boolean
)
*/


data class PersonResponseModel(
    val data: List<PersonResponseData>?,
    val message: String?,
    val success: Boolean?
)

data class PersonResponseData(
    val description: String?,
    val type: String?
)



data class PostPersonResponseModel(
    val interaction: Interaction,
    val hasSimulation: Boolean,
    val message: String,
    val success: Boolean
)

data class Interaction(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val momentId: String,
    val momentTitle: String,
    val relation: String,
    val responseStyle: String,
    val scenarioId: String,
    val scenarioTitle: String,
    val updatedAt: String,
    val userId: String
)




