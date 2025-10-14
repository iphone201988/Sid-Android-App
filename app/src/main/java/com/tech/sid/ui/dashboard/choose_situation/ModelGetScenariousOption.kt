package com.tech.sid.ui.dashboard.choose_situation

data class ModelGetScenariousOption(
    val data: List<Data>,
    val message: String,
    val success: Boolean
)

data class Data(
    val _id: String,
    val title: String
)