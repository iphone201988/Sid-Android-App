package com.tech.sid.ui.dashboard.dashboard_with_fragment.notification

data class NotificationModel(
    val success: Boolean?,
    val message: String?,
    val count: Int?,
    val notifications: List<NotificationData>?
)

data class NotificationData(
    val _id: String?,
    val userId: String?,
    var isDeleted: Boolean?,
    val data: NotificationType?,
    val __v: Int?,
    var isRead: Boolean?,
    val title: String?,
    val message: String?,
    val updatedAt: String?,
    val type: String?,
    val createdAt: String?
)

data class NotificationType(
    val notificationType: String?
)

sealed class NotificationsItem {
    data class NotificationDataNoMood(val notification: NotificationData) : NotificationsItem()
    data class NotificationDataMood(val notification: NotificationData) : NotificationsItem()
}