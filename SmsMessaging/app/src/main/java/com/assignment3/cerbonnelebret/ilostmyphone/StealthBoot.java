package com.assignment3.cerbonnelebret.ilostmyphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Start auto class who propose a stealth boot
 * For the moment it's not working
 */
public class StealthBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ServiceLock.class);
        context.startService(i);
        Log.d("StartAuto", "Good !");
    }
}
