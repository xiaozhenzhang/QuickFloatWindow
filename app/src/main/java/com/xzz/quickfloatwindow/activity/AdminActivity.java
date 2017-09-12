package com.xzz.quickfloatwindow.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xzz.quickfloatwindow.receiver.AdminManageReceiver;

public class AdminActivity extends Activity {
    private ComponentName mAdminName;
    private DevicePolicyManager mDevicePolicyManager;
    private final static int ACTIVATE_ADMIN = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ACTIVATE_ADMIN) {
            mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (mDevicePolicyManager.isAdminActive(this.mAdminName)) {
                mDevicePolicyManager.lockNow();
            }
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.mAdminName = new ComponentName(this, AdminManageReceiver.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
        try {
            startActivityForResult(intent, ACTIVATE_ADMIN);
        } catch (Exception e) {
        }

    }

}
