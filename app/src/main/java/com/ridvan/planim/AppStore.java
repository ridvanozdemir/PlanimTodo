package com.ridvan.planim;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class AppStore {
    private static final String TAG = "PlanimStore";
    private static final String PREFS = "planim_store";
    private static final String TASKS = "tasks";
    private static final String GOALS = "goals";

    private AppStore() {}

    public static List<TaskItem> loadTasks(Context context) {
        List<TaskItem> out = new ArrayList<>();
        String raw = prefs(context).getString(TASKS, "[]");
        if (raw == null || raw.trim().isEmpty()) raw = "[]";

        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    TaskItem task = TaskItem.fromJson(arr.getJSONObject(i));
                    sanitizeTask(task);
                    out.add(task);
                } catch (Exception itemError) {
                    Log.w(TAG, "Bozuk görev kaydı atlandı: " + i, itemError);
                }
            }
        } catch (Exception error) {
            Log.e(TAG, "Görev verileri okunamadı", error);
        }

        out.sort(Comparator.comparingLong(t -> t.createdAt));
        return out;
    }

    public static void saveTasks(Context context, List<TaskItem> tasks) {
        JSONArray arr = new JSONArray();
        if (tasks != null) {
            for (TaskItem task : tasks) {
                if (task == null) continue;
                try {
                    sanitizeTask(task);
                    arr.put(task.toJson());
                } catch (Exception itemError) {
                    Log.w(TAG, "Bir görev kaydedilemedi", itemError);
                }
            }
        }
        prefs(context).edit().putString(TASKS, arr.toString()).apply();
    }

    public static List<GoalItem> loadGoals(Context context) {
        List<GoalItem> out = new ArrayList<>();
        String raw = prefs(context).getString(GOALS, "[]");
        if (raw == null || raw.trim().isEmpty()) raw = "[]";

        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                try {
                    GoalItem goal = GoalItem.fromJson(arr.getJSONObject(i));
                    sanitizeGoal(goal);
                    out.add(goal);
                } catch (Exception itemError) {
                    Log.w(TAG, "Bozuk hedef kaydı atlandı: " + i, itemError);
                }
            }
        } catch (Exception error) {
            Log.e(TAG, "Hedef verileri okunamadı", error);
        }

        out.sort(Comparator.comparingLong(g -> g.targetDate));
        return out;
    }

    public static void saveGoals(Context context, List<GoalItem> goals) {
        JSONArray arr = new JSONArray();
        if (goals != null) {
            for (GoalItem goal : goals) {
                if (goal == null) continue;
                try {
                    sanitizeGoal(goal);
                    arr.put(goal.toJson());
                } catch (Exception itemError) {
                    Log.w(TAG, "Bir hedef kaydedilemedi", itemError);
                }
            }
        }
        prefs(context).edit().putString(GOALS, arr.toString()).apply();
    }

    public static TaskItem findTask(Context context, String id) {
        if (id == null) return null;
        for (TaskItem task : loadTasks(context)) {
            if (id.equals(task.id)) return task;
        }
        return null;
    }

    private static void sanitizeTask(TaskItem task) {
        if (task.id == null || task.id.trim().isEmpty()) task.id = UUID.randomUUID().toString();
        if (task.title == null) task.title = "";
        if (!TaskItem.DAILY.equals(task.period) &&
                !TaskItem.WEEKLY.equals(task.period) &&
                !TaskItem.MONTHLY.equals(task.period)) {
            task.period = TaskItem.DAILY;
        }
        task.requiredCount = Math.max(1, Math.min(10, task.requiredCount));
        task.reminderHour = Math.max(0, Math.min(23, task.reminderHour));
        task.reminderMinute = Math.max(0, Math.min(59, task.reminderMinute));
        if (task.createdAt <= 0L) task.createdAt = System.currentTimeMillis();
        task.completions.removeIf(ts -> ts == null || ts <= 0L);
    }

    private static void sanitizeGoal(GoalItem goal) {
        if (goal.id == null || goal.id.trim().isEmpty()) goal.id = UUID.randomUUID().toString();
        if (goal.title == null) goal.title = "";
        if (goal.targetDate <= 0L) goal.targetDate = System.currentTimeMillis();
        if (goal.createdAt <= 0L) goal.createdAt = System.currentTimeMillis();
        if (goal.completedAt < 0L) goal.completedAt = 0L;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
