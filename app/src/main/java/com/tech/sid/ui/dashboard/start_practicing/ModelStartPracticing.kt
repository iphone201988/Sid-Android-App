package com.tech.sid.ui.dashboard.start_practicing

data class ModelStartPracticing(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)

data class Data(
    val _id: String?,
    val title: String?
)