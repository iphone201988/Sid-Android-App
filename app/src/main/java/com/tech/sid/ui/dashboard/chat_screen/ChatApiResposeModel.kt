package com.tech.sid.ui.dashboard.chat_screen

data class ChatApiResposeModel(
    val message: String,
    val newMessage: String,
    val success: Boolean,
    val concluding: Boolean
)