package com.xzz.quickfloatwindow.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.xzz.quickfloatwindow.R;

/**
 * Created by Administrator
 * 通过辅助功能实现返回功能
 */
public class EnvelopeService extends AccessibilityService {

    private static EnvelopeService instance;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, R.string.interrupt_service, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        performGlobalAction(GLOBAL_ACTION_BACK);
        Toast.makeText(this, R.string.link_service, Toast.LENGTH_SHORT).show();
    }

    public static EnvelopeService getInstance() {
        return instance;
    }

    public static void back() {
        if (instance == null) {
            return;
        }
        instance.performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

}