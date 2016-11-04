package du.martin.library;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * Created by martindu on 16/11/3.
 */

public class GWProgressView extends View {

    private int mStrokeColor;
    private int mCircleBackgroundColor;
    private int mStrokeWidth;
    private int mRadius;
    private boolean mImmediate;
    private int mDuration;
    private Interpolator mInterpolator;
    private boolean mAnimationRunning;
    private float mProgress;
    private Paint mBackgroundPaint;
    private Paint mStrokePaint;
    private ObjectAnimator mAnimation;
    private RectF mOval = new RectF();


    public GWProgressView(Context context) {
        this(context, null);
    }

    public GWProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.GWProgressViewStyle);
    }

    public GWProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GWProgressView, defStyleAttr, R.style.GWProgressViewStyle);
        mStrokeColor = a.getColor(R.styleable.GWProgressView_progress_stroke_color, Color.RED);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.GWProgressView_progress_stroke_width, 4);
        mRadius = a.getDimensionPixelSize(R.styleable.GWProgressView_progress_radius, 20);
        mImmediate = a.getBoolean(R.styleable.GWProgressView_progress_immediate, false);
        mDuration = a.getInt(R.styleable.GWProgressView_progress_duration, 3000);
        mCircleBackgroundColor = a.getColor(R.styleable.GWProgressView_progress_background_color, Color.BLACK);
        final int resId = a.getResourceId(R.styleable.GWProgressView_progress_interpolator, 0);
        a.recycle();

        if (resId > 0) {
            mInterpolator = AnimationUtils.loadInterpolator(context, resId);
        }
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = mRadius * 2 + mStrokeWidth * 2;
        final int height = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mImmediate) {
            startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * set progress
     * @param progress
     */
    public void setProgress(@FloatRange(from = 0.0, to = 1.0) float progress) {
        if (mProgress != progress) {
            if (progress < 0) mProgress = 0;
            else if (progress > 1) mProgress = 1;
            else mProgress = progress;

            invalidate();
        }
    }

    /**
     * get progress
     * @return
     */
    public float getProgress() {
        return mProgress;
    }

    private void initPaint() {
        if (mBackgroundPaint == null) {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(mCircleBackgroundColor);
            mBackgroundPaint.setAntiAlias(true);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }
        if (mStrokePaint == null) {
            mStrokePaint = new Paint();
            mStrokePaint.setAntiAlias(true);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setColor(mStrokeColor);
            mStrokePaint.setStrokeWidth(mStrokeWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         *                |
         *                |
         *                |
         *                |
         * ---------------|------------->  0 startAngle 顺时针
         *                |
         *                |
         *                |
         *                |
         *                |
         *            sweepAngle
         */
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mBackgroundPaint);

        final float left = mStrokeWidth / 2.0f;
        final float top = left;
        final float right = getWidth() - left;
        final float bottom = getHeight() - top;

        mOval.set(left, top, right, bottom);

        final int topStartAngle = -90 + (int)(180 * mProgress);
        final int bottomStartAngle = 90 + (int)(180 * mProgress);
        final int sweepAngle = (int)(180 * mProgress);

        canvas.drawArc(mOval, topStartAngle, sweepAngle, false, mStrokePaint);
        canvas.drawArc(mOval, bottomStartAngle, sweepAngle, false, mStrokePaint);
    }

    public void start() {
        if (mAnimationRunning) return;
        startAnimation();
    }
    
    public void stop() {
        stopAnimation();
    }
    
    void startAnimation() {
        if (getVisibility() != VISIBLE) return;
        mAnimationRunning = true;
        mAnimation = ObjectAnimator.ofFloat(this, "progress", 1);
        mAnimation.setInterpolator(mInterpolator);
        mAnimation.setDuration(mDuration);
        mAnimation.start();
    }

    void stopAnimation() {
        mAnimationRunning = false;
        if (mAnimation != null && mAnimation.isRunning()) {
            mAnimation.end();
        }
    }

    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (mImmediate) {
                if (v == GONE || v == INVISIBLE) {
                    stopAnimation();
                } else {
                    startAnimation();
                }
            }
        }
    }

    interface Callback {
        /**
         * animation end
         */
        void animationEnd();
    }
}
