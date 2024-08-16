package com.mildp.familyatschool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CheckStatusReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    companion object {
        private const val TAG: String = "CheckStatusReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("Status","CheckStatus",NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification
            .Builder(context,"Status")
            .setContentTitle("您有尚未打開的設備，請您檢查以下功能是否開啟")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        when(intent.action) {
            "GPS" -> {
                notification.setContentText("您尚未打開GPS")
                notificationManager.notify(25, notification.build())
                Helper().log(TAG, "未打開GPS")
            }
            "BlueTooth" -> {
                notification.setContentText("您尚未打開藍芽")
                notificationManager.notify(24, notification.build())
                Helper().log(TAG, "未打開藍芽")
            }
            "BatteryOptimization" -> {
                notification.setContentText("您尚未關閉省電最佳化")
                notificationManager.notify(23, notification.build())
                Helper().log(TAG, "未關閉最佳化")
            }
        }
    }
}