package com.ridvan.planim;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

public final class TimeUtils {
    public static final ZoneId ZONE = ZoneId.systemDefault();
    private static final Locale TR = new Locale("tr", "TR");

    private TimeUtils() {}

    public static long atStart(LocalDate date) {
        return date.atStartOfDay(ZONE).toInstant().toEpochMilli();
    }

    public static long atEnd(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZONE).toInstant().toEpochMilli() - 1;
    }

    public static LocalDate toDate(long millis) {
        try {
            return Instant.ofEpochMilli(millis).atZone(ZONE).toLocalDate();
        } catch (RuntimeException error) {
            return LocalDate.now(ZONE);
        }
    }

    public static long[] currentPeriod(TaskItem task) {
        LocalDate today = LocalDate.now(ZONE);
        if (task != null && TaskItem.WEEKLY.equals(task.period)) {
            LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return new long[]{atStart(start), atEnd(start.plusDays(6))};
        }
        if (task != null && TaskItem.MONTHLY.equals(task.period)) {
            LocalDate start = today.withDayOfMonth(1);
            return new long[]{atStart(start), atEnd(start.plusMonths(1).minusDays(1))};
        }
        return new long[]{atStart(today), atEnd(today)};
    }

    public static int count(List<Long> values, long start, long end) {
        if (values == null || values.isEmpty()) return 0;
        int count = 0;
        for (Long timestamp : values) {
            if (timestamp != null && timestamp >= start && timestamp <= end) count++;
        }
        return count;
    }

    public static int currentCount(TaskItem task) {
        if (task == null) return 0;
        long[] range = currentPeriod(task);
        return count(task.completions, range[0], range[1]);
    }

    public static String periodLabel(String period) {
        if (TaskItem.WEEKLY.equals(period)) return "Haftalık";
        if (TaskItem.MONTHLY.equals(period)) return "Aylık";
        return "Günlük";
    }

    public static String formatDate(long millis) {
        return toDate(millis).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", TR));
    }

    public static String formatShort(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM", TR));
    }

    public static long localDateAtTime(LocalDate date, int hour, int minute) {
        int safeHour = Math.max(0, Math.min(23, hour));
        int safeMinute = Math.max(0, Math.min(59, minute));
        return LocalDateTime.of(date, LocalTime.of(safeHour, safeMinute))
                .atZone(ZONE).toInstant().toEpochMilli();
    }

    public static LocalDate mondayForOffset(int offset) {
        return LocalDate.now(ZONE)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(offset);
    }
}
