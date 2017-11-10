package com.xzz.quickfloatwindow.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.xzz.quickfloatwindow.view.FloatWindowView;

public class MyWindowManager {

    /**
     * 悬浮窗View的实例
     */
    private static FloatWindowView smallWindow;

    /**
     * 悬浮窗View的参数
     */
    private static LayoutParams smallWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 创建悬浮窗时，悬浮窗垂直向上方向距离屏幕中间的距离
     */
    private final static int yOffset = 200;

    /**
     * 创建一个悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        Point screenSize = new Point();
        windowManager.getDefaultDisplay().getSize(screenSize);
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        if (smallWindow == null) {
            smallWindow = new FloatWindowView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                smallWindowParams.type = LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.START | Gravity.BOTTOM;// 注意这里设置了gravity的属性为Gravity.START | Gravity.BOTTOM，悬浮窗的原点（0,0）在屏幕的左下角
                smallWindowParams.width = FloatWindowView.viewWidth;
                smallWindowParams.height = FloatWindowView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2 + yOffset;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 是否有悬浮窗显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

}