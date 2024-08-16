package com.mildp.familyatschool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager
    companion object {
        private const val TAG: String = "Receiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("SpotActivity","Survey",NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification
            .Builder(context,"SpotActivity")
            .setContentTitle("抵達問答區域囉")
            .setContentText("請您點選通知回答問題")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

        when(intent.action) {
            "gooseSpot" -> {
                val id = System.currentTimeMillis().toInt()
                val notificationIntent = Intent(context, SurveyActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,id,notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notification.setContentIntent(pendingIntent)
                notificationManager.notify(19,notification.build())
                Helper().log(TAG, "抵達鴨子景點")
            }
            "lakeSpot" -> {
                val id = System.currentTimeMillis().toInt()
                val notificationIntent = Intent(context, SurveyActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,id,notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notification.setContentIntent(pendingIntent)
                notificationManager.notify(19,notification.build())
                Helper().log(TAG, "抵達湖心景點")
            }
            "bridgeSpot" -> {
                val id = System.currentTimeMillis().toInt()
                val notificationIntent = Intent(context, SurveyActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,id,notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notification.setContentIntent(pendingIntent)
                notificationManager.notify(19,notification.build())
                Helper().log(TAG, "抵達小橋景點")
            }

        }
    }
}