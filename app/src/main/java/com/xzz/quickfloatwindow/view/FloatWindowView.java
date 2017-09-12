package com.xzz.quickfloatwindow.view;


import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xzz.quickfloatwindow.R;
import com.xzz.quickfloatwindow.activity.AdminActivity;
import com.xzz.quickfloatwindow.receiver.AdminManageReceiver;
import com.xzz.quickfloatwindow.service.EnvelopeService;

public class FloatWindowView extends LinearLayout {

    private static final String TAG = "float";
    /**
     * 记录悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;
//    private final TextView tvFloat;

    /**
     * 用于更新悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    /**
     * 移动的阈值
     */
    private static final int TOUCH_SLOP = 50;

    private boolean isMove = false;

    /**
     * 是否向上移动锁屏。默认是false，即向下移动锁屏
     */
    private boolean isMoveUpLock = false;

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window, this);
        View view = findViewById(R.id.ll_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
//        tvFloat = (TextView) findViewById(R.id.tv_float);

    }

    /**
     * 执行shell命令，但是有延迟
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x;
        int y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();

                x = (int) (xInScreen - xDownInScreen);
                y = (int) (yInScreen - yDownInScreen);
                if (Math.abs(x) < TOUCH_SLOP && Math.abs(y) < TOUCH_SLOP && isMove == false) {
                    return true;
                }

                if ((Math.abs(x) > TOUCH_SLOP)) {
                    isMove = true;
                }

                if (((yInScreen < yDownInScreen) ^ isMoveUpLock) || isMove) {
                    if (!isMove) {
                        isMove = true;
                    }
                    // 手指移动的时候更新小悬浮窗的位置
                    updateViewPosition();
                } else {
                    //如果设备管理器尚未激活，这里会启动一个激活设备管理器的Intent,具体的表现就是第一次打开程序时，手机会弹出激活设备管理器的提示，激活即可。
                    DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
                    if (!mDevicePolicyManager.isAdminActive(new ComponentName(getContext(), AdminManageReceiver.class))) {
                        showAdminManagement();
                    } else {
                        mDevicePolicyManager.lockNow();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    isMove = false;
                    break;
                }
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                x = (int) (xInScreen - xDownInScreen);
                y = (int) (yInScreen - yDownInScreen);
                if (Math.abs(x) < TOUCH_SLOP && Math.abs(y) < TOUCH_SLOP) {

//                    execShellCmd("input keyevent 4");//返回键的键值是4

                    if (isAccessibilitySettingsOn(getContext())) {
                        EnvelopeService.back();
                    } else {
                        // 打开系统设置中辅助功能
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                        Toast.makeText(getContext(), String.format(getResources().getString(R.string.tip_open_accessibility), getResources().getString(R.string.app_name)),
                                Toast.LENGTH_LONG).show();
                    }

                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断辅助服务是否开启。
     *
     * @param mContext
     * @return true为开启服务，false为未开启
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/"
                + EnvelopeService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext
                            .getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG,
                    "Error finding setting, default accessibility to not found: "
                            + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
                ':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext
                            .getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: "
                            + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG,
                                "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        windowManager.getDefaultDisplay().getSize(screenSize);
        int screenHeight = screenSize.y;
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = screenHeight - getStatusBarHeight() - (int) (yInScreen - yInView) - mParams.height;
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 激活设备管理器。通过跳转到一个activity实现，直接在服务里面跳转到激活设备管理器的页面在一些机型不能成功
     */
    private void showAdminManagement() {
        Intent intent = new Intent(getContext(), AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

}
