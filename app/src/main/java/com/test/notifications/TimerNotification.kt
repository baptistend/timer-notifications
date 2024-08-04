package com.test.notifications


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.CountDownTimer
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
@RequiresApi(Build.VERSION_CODES.TIRAMISU)

object TimerNotification {

    private const val CHANNEL_ID = "timer_channel"
    private const val NOTIFICATION_ID = 2309


    fun createNotificationChannel(context: ComponentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TimerNotification"
            val descriptionText = "timer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: ComponentActivity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            PermissionHandler.checkAndRequestPermissions(context) {
                createNotificationChannel(context)
                displayNotification(context, 10000)
            }
        }
        displayNotification(context, 10000)
    }
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun displayNotification(context: Context, countDownTime: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
            val customView = RemoteViews(context.packageName, R.layout.notification_countdown)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.timer_icon)
                .setContentTitle("Rest Time")
                .setContentText("Your timer is up!")
                .setCustomContentView(customView)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
        object : CountDownTimer(countDownTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update countdown time in the notification
                val timeString = formatTime(millisUntilFinished)
                customView.setTextViewText(R.id.notification_countdown, timeString)
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }

            override fun onFinish() {
                customView.setTextViewText(R.id.notification_countdown, "00:00:00")
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        }.start()
    }

}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun createAdd10SecPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = "ADD_10_SECONDS"
        putExtra("EXTRA_TIME_TO_ADD", 10000) // Ajoute 10 000 ms (10 secondes)
    }
    return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

// Créer un PendingIntent pour le bouton Annuler
@RequiresApi(Build.VERSION_CODES.TIRAMISU)

private fun createCancelPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = "CANCEL_TIMER"
    }
    return PendingIntent.getBroadcast(
        context,
        1,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}