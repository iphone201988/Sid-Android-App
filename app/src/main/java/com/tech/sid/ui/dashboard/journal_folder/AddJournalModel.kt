package com.tech.sid.ui.dashboard.journal_folder

data class AddJournalModel(
    val `data`: Data,
    val message: String,
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