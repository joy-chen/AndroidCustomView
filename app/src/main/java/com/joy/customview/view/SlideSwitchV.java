package com.joy.customview.view;

/**
 * Created by joy on 3/30/18.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.joy.customview.R;
import com.joy.customview.utils.CommonUtils;

public class SlideSwitchV extends View {

    public static final int SHAPE_RECT = 1;
    public static final int SHAPE_CIRCLE = 2;
    private static int RIM_SIZE = 4;
    private static final int DEFAULT_COLOR_THEME = Color.parseColor("#ff00ee00");
    // 3 attributes
    private int color_theme;
    private int shape;
    // varials of drawing
    private Paint paint;
    private Rect backRect;
    private Rect frontRect;
    private RectF frontCircleRect;
    private RectF backCircleRect;
    private int alpha;
    private int max_top;
    private int min_top;
    private int frontRect_top;
    private int frontRect_top_begin = RIM_SIZE;
    private int eventStartY;
    private int eventLastY;
    private int diffY = 0;
    private boolean slideable = true;
    private SlideListener listener;

    public interface SlideListener {
        void open();

        void close();
    }

    public SlideSwitchV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RIM_SIZE = CommonUtils.dp2px(context, 4);
        listener = null;
        paint = new Paint();
        paint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.slideswitch);
        color_theme = a.getColor(R.styleable.slideswitch_themeColor,
                DEFAULT_COLOR_THEME);
        shape = a.getInt(R.styleable.slideswitch_shape, SHAPE_RECT);
        a.recycle();
    }

    public SlideSwitchV(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitchV(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(280, widthMeasureSpec);
        int height = measureDimension(140, heightMeasureSpec);
        /*if (shape == SHAPE_CIRCLE) {
            if (width < height)
                width = height * 2;
        }*/
        setMeasuredDimension(width, height);
        initDrawingVal();
    }

    private int width;
    private int height;

    public void initDrawingVal() {
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        backCircleRect = new RectF();
        frontCircleRect = new RectF();
        frontRect = new Rect();
        backRect = new Rect(0, 0, width, height);
        min_top = RIM_SIZE;
        if (shape == SHAPE_RECT)
            max_top = height / 2;
        else
            max_top = height - width - RIM_SIZE;

        frontRect_top = RIM_SIZE;
        alpha = 0;
        frontRect_top_begin = frontRect_top;
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize; // UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shape == SHAPE_RECT) {
            paint.setColor(Color.GRAY);
            canvas.drawRect(backRect, paint);
            paint.setColor(color_theme);
            paint.setAlpha(alpha);
            canvas.drawRect(backRect, paint);
            frontRect.set(0, frontRect_top,
                    getWidth(), frontRect_top + getMeasuredHeight() / 2 - RIM_SIZE);
            paint.setColor(Color.WHITE);
            canvas.drawRect(frontRect, paint);
        } else {
            // draw circle
            int radius;
            radius = backRect.width() / 2 - RIM_SIZE;
            paint.setColor(getResources().getColor(R.color.shut_bg));
            backCircleRect.set(backRect);
            canvas.drawRoundRect(backCircleRect, radius, radius, paint);

            frontRect.set(0, frontRect_top,
                    getWidth(), frontRect_top + getMeasuredHeight() / 2 - RIM_SIZE);
            frontCircleRect.set(frontRect);
            paint.setColor(Color.WHITE);
            //canvas.drawRoundRect(frontCircleRect, radius, radius, paint);
            canvas.drawCircle(width / 2, frontRect_top + radius + RIM_SIZE, radius, paint);

            if (drawDir) {
                paint.setAlpha(85);
                canvas.drawBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.shape),
                        CommonUtils.dp2px(getContext(), 41),
                        CommonUtils.dp2px(getContext(), 159),
                        paint);
                paint.setAlpha(170);
                canvas.drawBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.shape),
                        CommonUtils.dp2px(getContext(), 41),
                        CommonUtils.dp2px(getContext(), 179),
                        paint);
                paint.setAlpha(255);
                canvas.drawBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.shape),
                        CommonUtils.dp2px(getContext(), 41),
                        CommonUtils.dp2px(getContext(), 199),
                        paint);
            }

        }

    }

    private boolean drawDir = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideable == false)
            return super.onTouchEvent(event);
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartY = (int) event.getRawY();
                drawDir = false;
                break;
            case MotionEvent.ACTION_MOVE:
                eventLastY = (int) event.getRawY();
                diffY = eventLastY - eventStartY;
                int tempY = diffY + frontRect_top_begin;
                tempY = (tempY > max_top ? max_top : tempY);
                tempY = (tempY < min_top ? min_top : tempY);
                if (tempY >= min_top && tempY <= max_top) {
                    frontRect_top = tempY;
                    alpha = (int) (255 * (float) tempY / (float) max_top);
                    invalidateView();
                }
                break;
            case MotionEvent.ACTION_UP:
                drawDir = true;
                frontRect_top_begin = frontRect_top;
                boolean toStart;
                toStart = ((max_top - frontRect_top) < 10) ? false : true;
                if (true) {
                    moveToDest();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * draw again
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    public void moveToDest() {
        ValueAnimator toDestAnim = ValueAnimator.ofInt(frontRect_top, min_top);
        toDestAnim.setDuration(500);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frontRect_top = (Integer) animation.getAnimatedValue();
                alpha = (int) (255 * (float) frontRect_top / (float) max_top);
                invalidateView();
            }
        });
        toDestAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.open();
                frontRect_top_begin = min_top;
            }
        });
    }

    public void setState(boolean isOpen) {
        initDrawingVal();
        invalidateView();
        if (listener != null)
            if (isOpen == true) {
                listener.open();
            } else {
                listener.close();
            }
    }

    public void setShapeType(int shapeType) {
        this.shape = shapeType;
    }

    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }

}