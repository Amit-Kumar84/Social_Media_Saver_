package com.rajput.socialmediasaver.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.util.Log
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rajput.socialmediasaver.R
import com.rajput.socialmediasaver.activity.MainActivity

@Suppress("DEPRECATION")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val params = remoteMessage.data
        if (params.isNotEmpty()) {
            sendNotification(params["title"], params["message"])
            broadcastNewNotification()
        } else {
            sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("Push Notification", title)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                resources.getString(R.string.app_name),
                resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, resources.getString(R.string.app_name))
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(resources.getColor(R.color.black))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setChannelId(resources.getString(R.string.app_name))
            .setFullScreenIntent(pendingIntent, true)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun broadcastNewNotification() {
        val intent = Intent("new_notification")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}