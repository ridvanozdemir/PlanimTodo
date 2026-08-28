package com.ridvan.planim;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AppStore {
    private static final String PREFS = "planim_store";
    private static final String TASKS = "tasks";
    private static final String GOALS = "goals";

    private AppStore() {}

    public static List<TaskItem> loadTasks(Context context) {
        List<TaskItem> out = new ArrayList<>();
        String raw = prefs(context).getString(TASKS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) out.add(TaskItem.fromJson(arr.getJSONObject(i)));
        } catch (Exception ignored) {}
        out.sort(Comparator.comparingLong(t -> t.createdAt));
        return out;
    }

    public static void saveTasks(Context context, List<TaskItem> tasks) {
        JSONArray arr = new JSONArray();
        try {
            for (TaskItem t : tasks) arr.put(t.toJson());
        } catch (Exception ignored) {}
        prefs(context).edit().putString(TASKS, arr.toString()).apply();
    }

    public static List<GoalItem> loadGoals(Context context) {
        List<GoalItem> out = new ArrayList<>();
        String raw = prefs(context).getString(GOALS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) out.add(GoalItem.fromJson(arr.getJSONObject(i)));
        } catch (Exception ignored) {}
        out.sort(Comparator.comparingLong(g -> g.targetDate));
        return out;
    }

    public static void saveGoals(Context context, List<GoalItem> goals) {
        JSONArray arr = new JSONArray();
        try {
            for (GoalItem g : goals) arr.put(g.toJson());
        } catch (Exception ignored) {}
        prefs(context).edit().putString(GOALS, arr.toString()).apply();
    }

    public static TaskItem findTask(Context context, String id) {
        for (TaskItem t : loadTasks(context)) if (t.id.equals(id)) return t;
        return null;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
