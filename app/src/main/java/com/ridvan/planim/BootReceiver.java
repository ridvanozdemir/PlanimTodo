package com.ridvan.planim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "PlanimBoot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        try {
            ReminderScheduler.scheduleAll(context);
        } catch (RuntimeException error) {
            Log.e(TAG, "Açılış sonrası hatırlatıcılar yeniden planlanamadı", error);
        }
    }
}
