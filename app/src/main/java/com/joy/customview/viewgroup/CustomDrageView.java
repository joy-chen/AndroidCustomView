package com.joy.customview.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.joy.customview.R;

/**
 * Created by joy on 3/29/18.
 */


public class CustomDrageView extends LinearLayout {
    private static final String TAG = "CustomDrageView";
    private Scroller mScroller;
    private LinearLayout tv;
    public CustomDrageView(Context context) {
        super(context);
        init();
    }

    public CustomDrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mvY;
    private int lastY;
    int dis;
    private void init() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.custom_drag_ly, this, true);

        mScroller = new Scroller(getContext());
        tv = (LinearLayout) view.findViewById(R.id.move_ly);
        tv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        moveX = event.getX();
                        moveY = event.getY();
                        mvY = (int) tv.getY();
                        lastY = (int) event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        //setX(getX() + (event.getX() - moveX));
                        /*if (Math.abs(event.getY() - lastY) < 8) {
                            break;
                        }*/
                        if ((event.getY() - lastY) < 2) {
                            break;
                        }
                        lastY = (int) event.getY();
                        dis = (int) (event.getY() - moveY);
                        if (dis > 395) {
                            dis = 395;
                        }
                        tv.setY(mvY+ dis);

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (dis > 390 && mDrageViewCallback != null) {
                            mDrageViewCallback.onTrageBottom();
                            break;
                        }
                        mScroller.startScroll(0, mvY, 0, (int)(tv.getY() - mvY), 600);
                        invalidate();
                        lastY = 0;
                        break;
                    }
                    default:
                        break;
                }

                return true;
            }
        });
    }

    float moveX;
    float moveY;

    float x, y;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        x = tv.getX();
        y = tv.getY();
    }

    private void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int detlaX = destX - scrollX;
        int scrollY = getScrollY();
        int detlaY = destY - scrollY;
        mScroller.startScroll(scrollX, scrollY, detlaX, detlaY, 1000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void setmDrageViewCallback(DrageViewCallback mDrageViewCallback) {
        this.mDrageViewCallback = mDrageViewCallback;
    }

    private DrageViewCallback mDrageViewCallback;

    public interface DrageViewCallback {
        void onTrageBottom();
    }
}