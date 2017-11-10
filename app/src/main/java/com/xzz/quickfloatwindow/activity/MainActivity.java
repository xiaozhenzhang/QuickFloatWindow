package com.xzz.quickfloatwindow.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xzz.quickfloatwindow.service.FloatWindowService;

public class MainActivity extends Activity {

    /**
     * 是否在近期任务里显示本应用。如果为true，就是在近期任务里显示本应用。
     */
    private boolean isShowApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        isShowApp = getIsShowApp();

        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        try {
            mDevicePolicyManager.lockNow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isShowApp) {
            finish();
        }

    }

    private boolean getIsShowApp() {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isShowApp) {
            return;
        }
        if (hasFocus) {
            //在这个方法里结束activity，可以在近期任务里显示应用，不然在一些手机上不会显示
            finish();
        }
    }

}
