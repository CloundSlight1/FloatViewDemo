package com.wuyz.floatviewdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class BigFloatView extends LinearLayout {

    private static final String TAG = "BigFloatView";

    public static int sWidth;

    public static int sHeight;

    public BigFloatView(final Context context) {
        super(context);

        LinearLayout view = (LinearLayout) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.float_window_big, this);

        sWidth = getResources().getDimensionPixelSize(R.dimen.big_float_view_width);
        sHeight = getResources().getDimensionPixelSize(R.dimen.big_float_view_height);
        Log2.d(TAG, "BigFloatView sWidth %d sHeight %d", sWidth, sHeight);

        view.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.stopService(new Intent(context, FloatWindowService.class));
            }
        });

        view.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getLayoutParams();
//                FloatWindowService.mSmallFloatView = SmallFloatView.createView(context, layoutParams.x, layoutParams.y);
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeView(BigFloatView.this);
                FloatWindowService.mBigFloatView = null;
            }
        });
    }

    public static BigFloatView createView(Context context, int x, int y) {
        Log2.d(TAG, "createView %d %d", x, y);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        BigFloatView view = new BigFloatView(context);
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
