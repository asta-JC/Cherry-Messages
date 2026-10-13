package com.asta.cherrymessages.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.asta.cherrymessages.MainActivity
import com.asta.cherrymessages.R

object NotificationHelper {
    const val CHANNEL_ID = "cherry_sms_channel"
    const val REPLY_ACTION = "com.asta.cherrymessages.ACTION_REPLY"
    const val MARK_READ_ACTION = "com.asta.cherrymessages.ACTION_MARK_READ"
    const val KEY_TEXT_REPLY = "key_text_reply"
    const val EXTRA_THREAD_ID = "extra_thread_id"
    const val EXTRA_ADDRESS = "extra_address"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Cherry Messages"
            val descriptionText = "Notifications for incoming SMS messages and delivery status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.parseColor("#E91E63")
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showIncomingSmsNotification(
        context: Context,
        address: String,
        body: String,
        threadId: Long,
        messageId: Long
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_THREAD_ID, threadId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            threadId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Reply Action
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel("Reply")
            .build()

        val replyIntent = Intent(context, com.asta.cherrymessages.receivers.SmsBroadcastReceiver::class.java).apply {
            action = REPLY_ACTION
            putExtra(EXTRA_THREAD_ID, threadId)
            putExtra(EXTRA_ADDRESS, address)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            threadId.toInt() + 1000,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send,
            "Reply",
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

        // Mark as Read Action
        val markReadIntent = Intent(context, com.asta.cherrymessages.receivers.SmsBroadcastReceiver::class.java).apply {
            action = MARK_READ_ACTION
            putExtra(EXTRA_THREAD_ID, threadId)
        }
        val markReadPendingIntent = PendingIntent.getBroadcast(
            context,
            threadId.toInt() + 2000,
            markReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markReadAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_agenda,
            "Mark as read",
            markReadPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(address)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(Color.parseColor("#E91E63"))
            .addAction(replyAction)
            .addAction(markReadAction)
            .build()

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(threadId.toInt(), notification)
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Failed to show notification due to security exception", e)
        }
    }

    fun showStatusNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setColor(Color.parseColor("#E91E63"))
            .build()

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Failed to show status notification due to security exception", e)
        }
    }
}
