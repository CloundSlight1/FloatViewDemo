package com.wuyz.floatviewdemo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/23.
 */
public class FloatWindowService extends Service {

    private static final String TAG = "FloatWindowService";

    private Timer mTimer;

    public static SmallFloatView mSmallFloatView;

    public static BigFloatView mBigFloatView;

    private WindowManager mWindowManager;

    private Handler mHandler;

    private ComponentName mLastComponent;

    private boolean mLastValue = false;

    @Override
    public void onCreate() {
        mHandler = new Handler();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isHome()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mSmallFloatView == null && mBigFloatView == null) {
                                Point size = new Point();
                                mWindowManager.getDefaultDisplay().getSize(size);
                                mSmallFloatView = SmallFloatView.createView(FloatWindowService.this,
                                        size.x - SmallFloatView.sWidth, size.y >> 1);
                            } if (mSmallFloatView != null) {
                                mSmallFloatView.setMemoryText();
                            }
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mSmallFloatView != null) {
                                mWindowManager.removeView(mSmallFloatView);
                                mSmallFloatView = null;
                            }
                            if (mBigFloatView != null) {
                                mWindowManager.removeView(mBigFloatView);
                                mBigFloatView = null;
                            }
                        }
                    });
                }
            }
        }, 500, 3000);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mSmallFloatView != null) {
            mWindowManager.removeView(mSmallFloatView);
            mSmallFloatView = null;
        }
        if (mBigFloatView != null) {
            mWindowManager.removeView(mBigFloatView);
            mBigFloatView = null;
        }
    }

    private boolean isHome() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ActivityManager.RunningTaskInfo info = list.get(0);
//            Log2.d(TAG, "isHome RunningTaskInfo %s", info.topActivity);
            if (info.topActivity != null) {
                if (mLastComponent != null && mLastComponent.equals(info.topActivity))
                    return mLastValue;
                mLastComponent = info.topActivity;
                
                String className = info.topActivity.getClassName();
//                Log2.d(TAG, "isHome topActivity %s", className);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
                if (resolveInfos != null) {
                    for (ResolveInfo i : resolveInfos) {
                        Log2.d(TAG, "isHome ResolveInfo %s", i.activityInfo.name);
                        if (className.equals(i.activityInfo.name)) {
                            mLastValue = true;
                            return true;
                        }
                    }
                }
            }
        }
        mLastValue = false;
        return false;
    }
}
