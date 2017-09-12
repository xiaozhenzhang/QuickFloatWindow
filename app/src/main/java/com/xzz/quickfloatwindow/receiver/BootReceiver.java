package com.xzz.quickfloatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xzz.quickfloatwindow.service.FloatWindowService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 接收开机广播
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, FloatWindowService.class);
            context.startService(serviceIntent);
        }

    }
}