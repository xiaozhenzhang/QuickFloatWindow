package com.xzz.quickfloatwindow.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

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
    private static View mFullScreenCheckView;
    private static boolean bIsFullScreen = false;

    /**
     * 创建一个悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createSmallWindow(final Context context) {
        if (smallWindow != null) {
            return;
        }
        final WindowManager windowManager = getWindowManager(context);
        Point screenSize = new Point();
        windowManager.getDefaultDisplay().getSize(screenSize);
        int screenWidth = screenSize.x;
        int screenHeight = screenSize.y;
        smallWindow = new FloatWindowView(context);
        if (smallWindowParams == null) {
            smallWindowParams = new LayoutParams();
            smallWindowParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
            smallWindowParams.format = PixelFormat.RGBA_8888;
            smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
            // 注意这里设置了gravity的属性为Gravity.START | Gravity.BOTTOM，悬浮窗的原点（0,0）在屏幕的左下角
            smallWindowParams.gravity = Gravity.START | Gravity.BOTTOM;
            smallWindowParams.width = FloatWindowView.viewWidth;
            smallWindowParams.height = FloatWindowView.viewHeight;
            smallWindowParams.x = screenWidth;
            smallWindowParams.y = screenHeight / 4 * 3;
        }
        smallWindow.setParams(smallWindowParams);

        windowManager.addView(smallWindow, smallWindowParams);

    }

    /**
     * 创建一个view，用于判断是否全屏
     *
     * @param context
     */
    public static void createFullScreenCheckView(final Context context) {

        if (mFullScreenCheckView != null) {
            return;
        }
        mFullScreenCheckView = new View(context);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.END | Gravity.TOP;

        layoutParams.width = 1;
        layoutParams.height = LayoutParams.MATCH_PARENT;

        mFullScreenCheckView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                DisplayMetrics dm = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(dm);
                int viewHeight = mFullScreenCheckView.getHeight();
                //当view的高度等于竖屏的高度或者横屏的高度时，此时全屏状态
                if (viewHeight == dm.widthPixels || viewHeight == dm.heightPixels) {
                    bIsFullScreen = true;
                    removeSmallWindow(context);
                } else {
                    bIsFullScreen = false;
                    createSmallWindow(context);
                }
            }

        });

        mWindowManager.addView(mFullScreenCheckView, layoutParams);
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
