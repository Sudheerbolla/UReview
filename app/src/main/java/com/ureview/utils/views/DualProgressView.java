package com.ureview.utils.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.ureview.R;

public class DualProgressView extends View {

    private static final float INDETERMINANT_MIN_SWEEP = 15f;

    private Paint mOuterCirclePaint;
    private float mThickness;
    private int mAnimDuration;
    private RectF mOuterCircleRect;
    @ColorInt
    private int mOuterCircleColor;
    private int mSteps;
    private int mSize;
    private float mStartAngle;
    private float mIndeterminateSweep;
    private float mIndeterminateRotateOffset;
    private AnimatorSet mIndeterminateAnimator;

    public DualProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public DualProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DualProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    protected void init(AttributeSet attrs, int defStyle) {
        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCircleRect = new RectF();
        Resources resources = getResources();
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DualProgressView, defStyle, 0);
        mThickness = a.getDimensionPixelSize(R.styleable.DualProgressView_dpv_thickness, resources.getDimensionPixelSize(R.dimen.default_thickness));
        mOuterCircleColor = a.getColor(R.styleable.DualProgressView_dpv_outer_color, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mAnimDuration = a.getInteger(R.styleable.DualProgressView_dpv_anim_duration, resources.getInteger(R.integer.default_anim_duration));
        mSteps = resources.getInteger(R.integer.default_anim_step);
        mStartAngle = resources.getInteger(R.integer.default_start_angle);
        a.recycle();
        setPaint();
    }

    private void setPaint() {
        mOuterCirclePaint.setColor(mOuterCircleColor);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setStrokeWidth(mThickness);
        mOuterCirclePaint.setStrokeCap(Paint.Cap.BUTT);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mOuterCircleRect, mStartAngle + mIndeterminateRotateOffset, mIndeterminateSweep, false, mOuterCirclePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();
        int width = getMeasuredWidth() - xPad;
        int height = getMeasuredHeight() - yPad;
        mSize = (width < height) ? width : height;
        setMeasuredDimension(mSize + xPad, mSize + yPad);
        updateRectAngleBounds();
    }

    private void updateRectAngleBounds() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        mOuterCircleRect.set(paddingLeft + mThickness, paddingTop + mThickness, mSize - paddingLeft - mThickness, mSize - paddingTop - mThickness);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSize = (w < h) ? w : h;
        updateRectAngleBounds();
    }

    private AnimatorSet createIndeterminateAnimator(float step) {
        final float maxSweep = 360f * (mSteps - 1) / mSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);
        ValueAnimator frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep);
        frontEndExtend.setDuration(mAnimDuration / mSteps / 2);
        frontEndExtend.setInterpolator(new DecelerateInterpolator(1));
        frontEndExtend.addUpdateListener(animation -> {
            mIndeterminateSweep = (Float) animation.getAnimatedValue();
            invalidate();
        });

        ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat(step * 720f / mSteps, (step + .5f) * 720f / mSteps);
        rotateAnimator1.setDuration(mAnimDuration / mSteps / 2);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(animation -> mIndeterminateRotateOffset = (Float) animation.getAnimatedValue());

        ValueAnimator backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP);
        backEndRetract.setDuration(mAnimDuration / mSteps / 2);
        backEndRetract.setInterpolator(new DecelerateInterpolator(1));
        backEndRetract.addUpdateListener(animation -> {
            mStartAngle = (Float) animation.getAnimatedValue();
            mIndeterminateSweep = maxSweep - mStartAngle + start;
            invalidate();
        });

        ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / mSteps, (step + 1) * 720f / mSteps);
        rotateAnimator2.setDuration(mAnimDuration / mSteps / 2);
        rotateAnimator2.setInterpolator(new LinearInterpolator());
        rotateAnimator2.addUpdateListener(animation -> mIndeterminateRotateOffset = (Float) animation.getAnimatedValue());

        AnimatorSet set = new AnimatorSet();
        set.play(frontEndExtend).with(rotateAnimator1);
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1);
        return set;
    }

    public void resetAnimation() {
        if (mIndeterminateAnimator != null && mIndeterminateAnimator.isRunning())
            mIndeterminateAnimator.cancel();
        mIndeterminateSweep = INDETERMINANT_MIN_SWEEP;
        mIndeterminateAnimator = new AnimatorSet();
        AnimatorSet prevSet = null, nextSet;
        for (int k = 0; k < mSteps; k++) {
            nextSet = createIndeterminateAnimator(k);
            AnimatorSet.Builder builder = mIndeterminateAnimator.play(nextSet);
            if (prevSet != null)
                builder.after(prevSet);
            prevSet = nextSet;
        }

        mIndeterminateAnimator.addListener(new AnimatorListenerAdapter() {
            boolean wasCancelled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!wasCancelled)
                    resetAnimation();
            }
        });
        mIndeterminateAnimator.start();
    }

    public void startAnimation() {
        resetAnimation();
    }

    public void stopAnimation() {
        if (mIndeterminateAnimator != null) {
            mIndeterminateAnimator.cancel();
            mIndeterminateAnimator = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        int currentVisibility = getVisibility();
        super.setVisibility(visibility);
        if (visibility != currentVisibility) {
            if (visibility == View.VISIBLE) {
                resetAnimation();
            } else if (visibility == View.GONE || visibility == View.INVISIBLE) {
                stopAnimation();
            }
        }
    }


}