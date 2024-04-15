package ramzi.eljabali.androidcore.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import ramzi.eljabali.androidcore.R

class Notification(private val mContext: Context) {
    // Intent is created to move the user to the MainActivity when they click on the notification
    private val intent = Intent(mContext, MainActivity2::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    //need a notification manager
    private val notificationManager =
        getSystemService(mContext, NotificationManager::class.java) as NotificationManager

    //Pending Intent created using the Intent previously created in order to move
    // the user to the MainActivity when they click on the notification
    private val pendingIntent: PendingIntent =
        PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    private val CHANNEL_ID_1 = "PERSONAL_1_1"
    private val CHANNEL_ID_2 = "WORK_1_1"

    fun createNotificationChannels() {
        val name: String = getString(mContext, R.string.group1_name)
        val name2: String = getString(mContext, R.string.group2_name)
        val mChannel =
            NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description =
                    "This is your personal notification Channel"
            }
        val mChannel2 =
            NotificationChannel(CHANNEL_ID_2, name2, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description =
                    "This is your work notification Channel"
            }
        notificationManager.createNotificationChannels(listOf(mChannel, mChannel2))
    }

    fun notifyUser(personalChannel: Boolean, workChannel: Boolean) {
        if (personalChannel){
            val notification = NotificationCompat.Builder(mContext, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("PERSONAL")
                .setContentText("You got the correct number HOORAY")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(1, notification)
            }
        }

        if (workChannel){
            val notification = NotificationCompat.Builder(mContext, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("WORK")
                .setContentText("You got the correct number HOORAY")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(2, notification)
            }
        }
    }

}