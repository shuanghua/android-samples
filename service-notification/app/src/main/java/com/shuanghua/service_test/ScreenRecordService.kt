package com.shuanghua.service_test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenRecordService : Service() {

    companion object {
        const val CHANNEL_ID = "id_video_record"
        const val CHANNEL_NAME = "video_record"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var notifyManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        startNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    /**
     * 将该 service 提升为前台 service (前台 service 必须和一个通知绑定后才能创建)
     */
    private fun startNotification() {
        notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = createChannelId()
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("视频录制中")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowWhen(false)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notifyManager.createNotificationChannel(channel)
            CHANNEL_ID
        } else {
            ""
        }
    }
}