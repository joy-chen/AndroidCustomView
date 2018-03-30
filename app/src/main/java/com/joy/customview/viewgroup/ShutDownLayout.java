package com.joy.customview.viewgroup;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.joy.customview.R;

/**
 * Created by joy on 3/29/18.
 */

public class ShutDownLayout extends LinearLayout implements View.OnClickListener {
    public ShutDownLayout(Context context) {
        super(context);
        init();
    }

    public ShutDownLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShutDownLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.shutdown_ly, this, true);
        findViewById(R.id.cancel_action).setOnClickListener(this);
        ((CustomDrageView)findViewById(R.id.drage_view)).setmDrageViewCallback(new CustomDrageView.DrageViewCallback() {
            @Override
            public void onTrageBottom() {
                shutDownUICallback.onShutDown();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_action:
                if (shutDownUICallback != null) {
                    shutDownUICallback.onCancle();
                }
                break;

            default:
                break;
        }
    }

    private ShutDownUICallback shutDownUICallback;

    public void setShutDownUICallback(ShutDownUICallback shutDownUICallback) {
        this.shutDownUICallback = shutDownUICallback;
    }

    public interface ShutDownUICallback {
        void onCancle();

        void onShutDown();
    }
}
