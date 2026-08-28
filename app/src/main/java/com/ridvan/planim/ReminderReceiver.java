package com.ridvan.planim;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "PlanimReminder";
    private static final String CHANNEL = "task_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;

        String taskId = intent.getStringExtra("taskId");
        if (taskId == null || taskId.trim().isEmpty()) return;

        try {
            TaskItem task = AppStore.findTask(context, taskId);
            if (task == null || !task.reminderEnabled) return;

            createChannel(context);
            if (Build.VERSION.SDK_INT < 33 ||
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Intent open = new Intent(context, MainActivity.class);
                PendingIntent content = PendingIntent.getActivity(
                        context,
                        task.id.hashCode(),
                        open,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                int current = TimeUtils.currentCount(task);
                String title = task.title == null || task.title.trim().isEmpty() ? "Planım" : task.title;
                android.app.Notification notification = new android.app.Notification.Builder(context, CHANNEL)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(title)
                        .setContentText(TimeUtils.periodLabel(task.period) + " hedef: " + current + "/" + task.requiredCount)
                        .setAutoCancel(true)
                        .setContentIntent(content)
                        .build();

                NotificationManager manager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) manager.notify(task.id.hashCode(), notification);
            }

            ReminderScheduler.schedule(context, task);
        } catch (RuntimeException error) {
            Log.e(TAG, "Hatırlatıcı işlenirken hata oluştu", error);
        }
    }

    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "Yapılacak hatırlatmaları",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }
    }
}
