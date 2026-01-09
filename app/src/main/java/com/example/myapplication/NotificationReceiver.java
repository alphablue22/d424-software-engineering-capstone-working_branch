package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.media3.common.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ExcursionNotifications"; // Choose a unique ID

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "onReceive called");
        createNotificationChannel(context); // Ensure the channel exists

        // Determine the type of notification
        boolean isVacationNotification = intent.getBooleanExtra("isVacationNotification", false);

        // Build notification based on type
        if (isVacationNotification) {
            handleVacationNotification(context, intent);
        } else {
            handleExcursionNotification(context, intent);
        }
    }

    private void handleExcursionNotification(Context context, Intent intent) {
        String excursionName = intent.getStringExtra("excursionName");
        int excursionID = intent.getIntExtra("excursionID",-1);
        if (excursionName == null) {
            Log.e("NotificationReceiver", "onReceive: Excursion name is null");
            return;
        }

        String notificationText = "Excursion " + excursionName + " is happening today!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
                .setContentTitle("Excursion Reminder")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(excursionID, builder.build());
    }

    private void handleVacationNotification(Context context, Intent intent) {
        String vacationTitle = intent.getStringExtra("vacationTitle");
        if (vacationTitle == null) {
            Log.e("NotificationReceiver", "onReceive: Vacation title is null");
            return;
        }
        boolean isStart = intent.getBooleanExtra("isStart", false);
        int notificationID = intent.getIntExtra("notificationID", -1);

        String alertType = isStart ? "Starting" : "Ending";
        String notificationText = "Vacation " + vacationTitle + " is " + alertType + " today!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
                .setContentTitle("Vacation Alert")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(notificationID, builder.build()); // Use a unique ID if needed
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Excursion Notifications"; // Your channel name
            String description = "Notifications for excursion events"; // Your description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}