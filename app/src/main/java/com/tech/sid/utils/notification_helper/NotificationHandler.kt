package com.tech.sid.utils.notification_helper

import androidx.lifecycle.LiveData
import com.tech.sid.base.utils.event.SingleActionEvent

//object NotificationHandler {
//    private val _singleEventNotification = SingleActionEvent<NotificationPayLoad>()
//    val singleEventNotification: LiveData<NotificationPayLoad> get() = _singleEventNotification
//
//    private val _dashboardBadges = SingleActionEvent<String>()
//    val dashboardBadges: LiveData<String> get() = _dashboardBadges
//
//
//    /**For Handling Notification**/
//    fun postNotification(notification: NotificationPayLoad) {
//        _singleEventNotification.postValue(notification)
//    }
//
//    /** handle badges in dashboard **/
//    fun handleDashboardBadges(type: String) {
//        _dashboardBadges.postValue(type)
//    }
//}