package com.eg.messenger.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "channel_id"

class FirebaseMessaging : FirebaseMessagingService() {
    private val TAG = "FirebaseMessaging"

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val authId = auth.currentUser?.uid

        // If new token created just update it's value in the DB document of current user
        db.document("users/$authId")
            .update("messagingToken", token)
            .addOnSuccessListener { Log.d(TAG, "Messaging Token for user $authId updated")}
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document for user $authId", e) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        // Notification channel needed only if android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channel_name"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            enableLights(true)
        }

        notificationManager.createNotificationChannel(channel)
    }
}