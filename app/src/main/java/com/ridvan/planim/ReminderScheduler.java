package com.ridvan.planim;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public final class ReminderScheduler {
    private ReminderScheduler() {}

    public static void scheduleAll(Context context) {
        for (TaskItem task : AppStore.loadTasks(context)) {
            if (task.reminderEnabled) schedule(context, task);
        }
    }

    public static void schedule(Context context, TaskItem task) {
        cancel(context, task.id);
        if (!task.reminderEnabled) return;

        long triggerAt = nextTrigger(task);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = pendingIntent(context, task.id, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    public static void cancel(Context context, String taskId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = pendingIntent(context, taskId, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) am.cancel(pi);
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
        LocalTime time = LocalTime.of(task.reminderHour, task.reminderMinute);
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
