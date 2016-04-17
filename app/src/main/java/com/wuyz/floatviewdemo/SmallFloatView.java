package com.wuyz.floatviewdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SmallFloatView extends LinearLayout {

    private static final String TAG = "SmallFloatView";

    private WindowManager mWindowManager;

    private int mStatusBarHeight;

    private float mXInView;
    private float mYInView;

    private float mXInScreen;
    private float mYInScreen;

    private TextView mPercentText;

    private Context mContext;

    public static int sWidth;

    public static int sHeight;

    public SmallFloatView(Context context) {
        super(context);
        mContext = context;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LinearLayout view = (LinearLayout) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.float_window_small, this);
        mPercentText = (TextView) view.findViewById(R.id.percent);
        setMemoryText();
        mStatusBarHeight = getStatusBarHeight();
        sWidth = getResources().getDimensionPixelSize(R.dimen.small_float_view_width);
        sHeight = getResources().getDimensionPixelSize(R.dimen.small_float_view_height);
        Log2.d(TAG, "SmallFloatView sWidth %d sHeight %d", sWidth, sHeight);
    }

    public void setMemoryText() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        mPercentText.setText(String.format("%d%%",
                (memoryInfo.totalMem - memoryInfo.availMem) * 100 / memoryInfo.totalMem));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mXInView = event.getX();
                mYInView = event.getY();
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY();
//                Log2.d(TAG, "ACTION_DOWN mXInView %.0f mYInView %.0f", mXInView, mYInView);
                return true;
            case MotionEvent.ACTION_MOVE:
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
//                Log2.d(TAG, "ACTION_MOVE mXInScreen %.0f mYInScreen %.0f layoutParams.x %d, layoutParams.y %d",
//                        mXInScreen, mYInScreen, layoutParams.x, layoutParams.y);
                layoutParams.x = (int) (event.getRawX() - mXInView);
                layoutParams.y = (int) (event.getRawY() - mStatusBarHeight - mYInView);
                mWindowManager.updateViewLayout(this, layoutParams);
                return true;
            case MotionEvent.ACTION_UP:
                if (event.getRawX() == mXInScreen && event.getRawY() == mYInScreen) {
                    layoutParams = (WindowManager.LayoutParams) getLayoutParams();
                    FloatWindowService.mBigFloatView = BigFloatView.createView(mContext,
                            layoutParams.x - BigFloatView.sWidth / 2,
                            layoutParams.y - BigFloatView.sHeight / 2);
//                    mWindowManager.removeView(this);
//                    FloatWindowService.mSmallFloatView = null;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private int getStatusBarHeight() {
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(id);
    }

    public static SmallFloatView createView(Context context, int x, int y) {
        Log2.d(TAG, "createView %d %d", x, y);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        SmallFloatView view = new SmallFloatView(context);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = sWidth;
        layoutParams.height = sHeight;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.format = PixelFormat.RGBA_8888;
        windowManager.addView(view, layoutParams);
        return view;
    }
}
