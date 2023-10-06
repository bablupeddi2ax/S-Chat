package com.example.simplechat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.simplechat.R
import com.example.simplechat.models.User
import com.example.simplechat.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Get the current user's UID or identifier
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Define a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()

            // Create a reference to where you want to store the token (e.g., under "users" node)
            val userTokenRef = database.getReference("users").child(userId).child("fcmToken")

            // Set the FCM token as the value
            userTokenRef.setValue(token)
                .addOnSuccessListener {
                    Log.i("","")
                }
                .addOnFailureListener { e ->
                    // Handle any errors, such as network issues or database write failures
                    // You can log an error message or take appropriate action
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming message here
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // You can customize the notification's behavior here
        sendNotification(title, body)
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.snapchat)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // NotificationChannel setup (for Android O and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "your_channel_id",
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}