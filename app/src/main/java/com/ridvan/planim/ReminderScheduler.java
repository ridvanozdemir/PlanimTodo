package com.ridvan.planim;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public final class ReminderScheduler {
    private static final String TAG = "PlanimReminder";

    private ReminderScheduler() {}

    public static void scheduleAll(Context context) {
        if (context == null) return;
        for (TaskItem task : AppStore.loadTasks(context)) {
            if (!task.reminderEnabled) continue;
            try {
                schedule(context, task);
            } catch (RuntimeException error) {
                Log.e(TAG, "Hatırlatıcı planlanamadı: " + task.id, error);
            }
        }
    }

    public static void schedule(Context context, TaskItem task) {
        if (context == null || task == null || task.id == null) return;

        cancel(context, task.id);
        if (!task.reminderEnabled) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.w(TAG, "AlarmManager kullanılamıyor");
            return;
        }

        final long triggerAt;
        try {
            triggerAt = nextTrigger(task);
        } catch (RuntimeException error) {
            Log.e(TAG, "Geçersiz hatırlatma zamanı: " + task.id, error);
            return;
        }

        PendingIntent pendingIntent = pendingIntent(context, task.id, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent
                );
            } else {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent
                );
            }
        } catch (SecurityException exactAlarmDenied) {
            Log.w(TAG, "Kesin alarm izni yok, normal alarm kullanılacak", exactAlarmDenied);
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            } catch (RuntimeException fallbackError) {
                Log.e(TAG, "Hatırlatıcı fallback ile de planlanamadı", fallbackError);
            }
        } catch (RuntimeException error) {
            Log.e(TAG, "Hatırlatıcı planlanırken hata oluştu", error);
        }
    }

    public static void cancel(Context context, String taskId) {
        if (context == null || taskId == null) return;
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;
            PendingIntent pendingIntent = pendingIntent(context, taskId, PendingIntent.FLAG_NO_CREATE);
            if (pendingIntent != null) alarmManager.cancel(pendingIntent);
        } catch (RuntimeException error) {
            Log.w(TAG, "Hatırlatıcı iptal edilemedi: " + taskId, error);
        }
    }

    private static PendingIntent pendingIntent(Context context, String taskId, int flag) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("taskId", taskId);
        return PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                flag | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static long nextTrigger(TaskItem task) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        int hour = Math.max(0, Math.min(23, task.reminderHour));
        int minute = Math.max(0, Math.min(59, task.reminderMinute));
        LocalTime time = LocalTime.of(hour, minute);
        LocalDateTime now = LocalDateTime.now(zone);
        LocalDateTime next;

        if (TaskItem.WEEKLY.equals(task.period)) {
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            next = LocalDateTime.of(monday, time);
            if (!next.isAfter(now)) next = LocalDateTime.of(monday.plusWeeks(1), time);
        } else if (TaskItem.MONTHLY.equals(task.period)) {
            LocalDate first = today.withDayOfMonth(1);
            next = LocalDateTime.of(first, time);
            if (!next.isAfter(now)) next = LocalDateTime.of(first.plusMonths(1), time);
        } else {
            next = LocalDateTime.of(today, time);
            if (!next.isAfter(now)) next = LocalDateTime.of(today.plusDays(1), time);
        }
        return next.atZone(zone).toInstant().toEpochMilli();
    }
}
