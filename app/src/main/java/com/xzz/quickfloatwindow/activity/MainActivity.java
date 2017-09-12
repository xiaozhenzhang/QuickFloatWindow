package com.xzz.quickfloatwindow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xzz.quickfloatwindow.service.FloatWindowService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            //在这个方法里结束activity，可以在近期任务里显示应用，不然在一些手机上不会显示
            finish();
        }
    }

}
