package isnao.acadiaformation

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import isnao.acadiaformation.MainActivity.Actions

class MyReceiver : BroadcastReceiver() {
    @Override
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (context != null) {
            NotificationHelper(context).also { notificationHelper: NotificationHelper ->
                intent?.extras?.getInt("notifId")?.let { notificationHelper.makenoti(it) }
            }
        }
    }
}

class NotificationHelper(
    base: Context,
) : ContextWrapper(base) {
    private var notifManager: NotificationManager =
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private var context: Context = base

    fun createchannel() {
// The user-visible name of the channel.
        val name: CharSequence = getString(R.string.channel_name)
        // The user-visible description of the channel.
        val description = getString(R.string.channel_description)
        val importance =
            NotificationManager.IMPORTANCE_HIGH // which is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
        val mChannel = NotificationChannel(Actions.id, name, importance)
        // Configure the notification channel.
        mChannel.description = description
        mChannel.enableLights(true)
        // Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.setShowBadge(true)
        mChannel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        notifManager.createNotificationChannel(mChannel)
    }

    fun makenoti(notifId: Int) {
        val launchActivity = Intent(this, MainActivity::class.java)
        val launchActivityPendingIntent: PendingIntent =
            TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack.
                addNextIntentWithParentStack(launchActivity)
                // Get the PendingIntent containing the entire back stack.
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            }

        val noti =
            Notification
                .Builder(applicationContext, Actions.id)
                .setSmallIcon(R.drawable.ic_launcher_icon)
                .setChannelId(Actions.id)
                .setContentTitle("Formation Acadia")
                .setContentText("Pense à remplir ensuite le formulaire en cliquant sur la notification")
                .setAutoCancel(true)
                .setNumber(1)
                .setContentIntent(launchActivityPendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setCategory(Notification.CATEGORY_EVENT)
                .build() // finally build and return a Notification.
        // Show the notification
        notifManager.notify(notifId, noti)
    }

    fun setReminder(
        timeInMillis: Long,
        notifId: Int = 0,
    ) {
        Intent(context, MyReceiver::class.java).also { intent: Intent ->
            intent.putExtra("notifId", notifId)
            PendingIntent
                .getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ).also { pendingIntent: PendingIntent ->
                    val alarmManager: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager.set(AlarmManager.RTC, timeInMillis, pendingIntent)
                }
        }
    }
}
