package com.example.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_set"
const val channelName = "com.example.notification"

class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
      if(remoteMessage.getNotification() != null) {
          if (SDK_INT >= Build.VERSION_CODES.O) {
              createNotification( remoteMessage.notification!!.body!!)
          }
      }
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView( message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.notification", R.layout.notificationtext)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.coronavirus)

        return remoteView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification( message: String) {
        val intent = Intent(this, MainActivity::class.java )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags =
            if (SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,  flags)

        var builder: NotificationCompat.Builder =  NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.coronavirus)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(900,900,900,900))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder = builder.setContent(getRemoteView(message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel( notificationChannel )


        notificationManager.notify(0, builder.build())
    }
}