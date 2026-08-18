package com.ridvan.planim;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class GoalItem {
    public String id = UUID.randomUUID().toString();
    public String title = "";
    public long targetDate = System.currentTimeMillis();
    public long createdAt = System.currentTimeMillis();
    public boolean completed = false;
    public long completedAt = 0L;

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("title", title);
        o.put("targetDate", targetDate);
        o.put("createdAt", createdAt);
        o.put("completed", completed);
        o.put("completedAt", completedAt);
        return o;
    }

    public static GoalItem fromJson(JSONObject o) throws JSONException {
        GoalItem g = new GoalItem();
        g.id = o.optString("id", UUID.randomUUID().toString());
        g.title = o.optString("title", "");
        g.targetDate = o.optLong("targetDate", System.currentTimeMillis());
        g.createdAt = o.optLong("createdAt", System.currentTimeMillis());
        g.completed = o.optBoolean("completed", false);
        g.completedAt = o.optLong("completedAt", 0L);
        return g;
    }
}
