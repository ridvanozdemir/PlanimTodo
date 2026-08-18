package com.ridvan.planim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskItem {
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";

    public String id = UUID.randomUUID().toString();
    public String title = "";
    public String period = DAILY;
    public int requiredCount = 1;
    public boolean reminderEnabled = false;
    public int reminderHour = 9;
    public int reminderMinute = 0;
    public long createdAt = System.currentTimeMillis();
    public final List<Long> completions = new ArrayList<>();

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("title", title);
        o.put("period", period);
        o.put("requiredCount", requiredCount);
        o.put("reminderEnabled", reminderEnabled);
        o.put("reminderHour", reminderHour);
        o.put("reminderMinute", reminderMinute);
        o.put("createdAt", createdAt);
        JSONArray arr = new JSONArray();
        for (Long ts : completions) arr.put(ts);
        o.put("completions", arr);
        return o;
    }

    public static TaskItem fromJson(JSONObject o) throws JSONException {
        TaskItem t = new TaskItem();
        t.id = o.optString("id", UUID.randomUUID().toString());
        t.title = o.optString("title", "");
        t.period = o.optString("period", DAILY);
        t.requiredCount = o.optInt("requiredCount", 1);
        t.reminderEnabled = o.optBoolean("reminderEnabled", false);
        t.reminderHour = o.optInt("reminderHour", 9);
        t.reminderMinute = o.optInt("reminderMinute", 0);
        t.createdAt = o.optLong("createdAt", System.currentTimeMillis());
        JSONArray arr = o.optJSONArray("completions");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) t.completions.add(arr.optLong(i));
        }
        return t;
    }
}
