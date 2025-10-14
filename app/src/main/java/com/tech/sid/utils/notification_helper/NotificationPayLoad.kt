package com.tech.sid.utils.notification_helper

data class NotificationPayload(
    val click_action: String?,
    val notificationType: String?,
    val body: String?,
    val title: String?
)

