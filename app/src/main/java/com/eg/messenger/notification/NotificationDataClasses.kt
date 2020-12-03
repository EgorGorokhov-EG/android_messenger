package com.eg.messenger.notification

// Data class used to store data to display in a push notification
data class NotificationData(
    val title: String?,
    val message: String?
)

// Data class used to send NotificationData to recipient via retrofit request to FCM
data class PushNotification (
    val data: NotificationData,
    val to: String?
)