package com.example.composechatexample.utils.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.composechatexample.R
import com.example.composechatexample.activities.NotificationActivity
import com.example.composechatexample.data.model.NotificationModel
import com.example.composechatexample.data.model.NotificationType
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.data.preferences.PreferencesManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseService() : FirebaseMessagingService() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var intent: Intent
    private var token: String? = null
    private var newDeviceId: String? = null
    private var messageData: NotificationModel? = null
    private var notificationId: Int? = null
    private var gson = Gson()

    private fun sendEvent(event: NotificationEvent) {
        GlobalScope.launch {
            eventChannel.send(event)
        }
    }

    override fun onNewToken(token: String) {
        this.token = token
        newDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        registration()
        super.onNewToken(token)
    }

    private fun registration() {
        Log.d(TAG, token.toString())
        preferencesManager.let {
            it.tokenFcm = token
            it.deviceId = newDeviceId
            it.deviceType = DEVICE_TYPE
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            if (preferencesManager.notificationScreenOpen) {
                sendEvent(
                    NotificationEvent.AddNotification(
                        UserNotification(
                            id = message.data["id"].toString(),
                            type = when (message.data["type"].toString()) {
                                NotificationType.REQUEST_FRIENDSHIP.value -> NotificationType.REQUEST_FRIENDSHIP
                                NotificationType.ACCEPTED_FRIENDSHIP.value -> NotificationType.ACCEPTED_FRIENDSHIP
                                NotificationType.DECLINED_FRIENDSHIP.value -> NotificationType.DECLINED_FRIENDSHIP
                                NotificationType.USER_ACCEPT_FRIENDSHIP.value -> NotificationType.USER_ACCEPT_FRIENDSHIP
                                NotificationType.USER_DECLINED_FRIENDSHIP.value -> NotificationType.USER_DECLINED_FRIENDSHIP
                                else -> NotificationType.REQUEST_FRIENDSHIP
                            },
                            senderId = message.data["senderId"].toString(),
                            senderName = message.data["senderName"].toString(),
                        )
                    )
                )
            } else {
                if (preferencesManager.notification) {
                    messageData = NotificationModel(
                        title = message.data["title"].toString(),
                        text = message.data["body"].toString(),
                    )
                    createNotification(messageData!!)
                }
            }
        }
        super.onMessageReceived(message)
    }

    private fun createNotification(
        messageData: NotificationModel
    ) {
        notificationId = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
        intent = messageData.let {
            Intent(this, NotificationActivity::class.java).apply {
                putExtra(MESSAGE_DATA, messageData)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId!!,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setVibrate(longArrayOf(DURATION, DURATION, DURATION, DURATION))
            .setOnlyAlertOnce(false)
            .setContentTitle(messageData.title)
            .setContentText(messageData.text)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_EVENT)
            .setGroup(TAG)
            .setChannelId(CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        showNotification(notificationManager, notification.build())
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showNotification(
        manager: NotificationManagerCompat,
        notification: Notification,
    ) {

        val summaryNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setGroup(TAG)
            .setGroupSummary(true)
            .build()

        manager.apply {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else
                notify(notificationId!!, notification)
            notify(R.string.app_name, summaryNotification)
        }
    }

    companion object {
        private const val TAG = "FirebaseMessaging"
        private const val CHANNEL_ID = "my_channel"
        private const val CHANNEL_NAME = "notification_channel_id"
        private const val DURATION = 1000L
        private const val MESSAGE_DATA = "message_data"
        const val DEVICE_TYPE = "android_device"

        private val eventChannel = Channel<NotificationEvent>(Channel.BUFFERED)
        val eventsFlow = eventChannel.receiveAsFlow()
        fun subscribeEvent(): Flow<NotificationEvent?> = eventsFlow
    }
}