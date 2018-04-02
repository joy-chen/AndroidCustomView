package com.joy.customview.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.joy.customview.R;
import com.joy.customview.viewgroup.ShutDownLayout;

public class MainActivity extends Activity {
    WindowManager mwindowManager;
    ShutDownLayout mFloatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mwindowManager = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        //createFloatWindow(this);
    }

    public void createFloatWindow(Context context) {
        int stretch = ViewGroup.LayoutParams.MATCH_PARENT;
        int type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(stretch, stretch, type, flags, PixelFormat.TRANSLUCENT);

        mFloatLayout = new ShutDownLayout(context);
        mFloatLayout.setShutDownUICallback(new ShutDownLayout.ShutDownUICallback() {
            @Override
            public void onCancle() {
                mwindowManager.removeView(mFloatLayout);
            }

            @Override
            public void onShutDown() {
                mwindowManager.removeView(mFloatLayout);
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mwindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mwindowManager.addView(mFloatLayout, wmParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
