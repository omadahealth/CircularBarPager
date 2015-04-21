/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Omada Health, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.OrangeGangsters.circularbarpager.library;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

import daniel.olivier.stoyan.pager.R;


/**
 * Created by stoyan and olivier on 12/9/14.
 */
public class CircularBar extends View implements Animator.AnimatorListener {
    /**
     * TAG for logging
     */
    private static final String TAG = "CircularBar";

    /**
     * The context of this view
     */
    private Context mContext;

    /**
     * The default {@link #mMax} of the circular bar, not the angle.
     * IE. {@link #progress}/{@link #mMax} * 360 = {@link #mProgressSweep}
     */
    public static final int DEFAULT_ARC_MAX = 100;

    /**
     * The max progress, default is 100
     */
    private int mMax = DEFAULT_ARC_MAX;

    /**
     * Current progress, can not exceed the {@link #mMax} progress.
     */
    private float progress = 0;

    /**
     * The clockwise progress area bar color
     */
    private int mClockwiseArcColor;

    /**
     * The counter clockwise progress area bar color
     */
    private int mCounterClockwiseArcColor;

    /**
     * The clockwise bar outline area color.
     */
    private int mClockwiseOutlineArcColor;

    /**
     * The counter clockwise bar outline area color.
     */
    private int mCounterClockwiseOutlineArcColor;

    /**
     * The color to fill the circle center
     */
    private int mCircleFillColor;

    /**
     * The fill mode type for {@link #mCircleFillColor}
     */
    private int mCircleFillMode;

    /**
     * The clockwise width of the reached area
     */
    private float mClockwiseReachedArcWidth;

    /**
     * The counter clockwise width of the reached area
     */
    private float mCounterClockwiseReachedArcWidth;

    /**
     * The clockwise width of the outline area
     */
    private float mClockwiseOutlineArcWidth;

    /**
     * The counter clockwise width of the outline area
     */
    private float mCounterClockwiseOutlineArcWidth;

    /**
     * The Paint of the reached area.
     */
    private Paint mReachedArcPaint;

    /**
     * The Paint of the clockwise reached area.
     */
    private Paint mClockwiseReachedArcPaint;

    /**
     * The Paint of the counter clockwise reached area.
     */
    private Paint mCounterClockwiseReachedArcPaint;

    /**
     * The Painter of the outline area.
     */
    private Paint mOutlineArcPaint;

    /**
     * The Painter of the clockwise outline area.
     */
    private Paint mClockwiseOutlineArcPaint;

    /**
     * The Painter of the counter clockwise outline area.
     */
    private Paint mCounterClockwiseOutlineArcPaint;

    /**
     * The Painter of the fill circle.
     */
    private Paint mCircleFillPaint;

    /**
     * The reached bar area rect.
     */
    private RectF mReachedArcRectF = new RectF(0, 0, 0, 0);

    /**
     * The outline bar area
     */
    private RectF mOutlineArcRectF = new RectF(0, 0, 0, 0);

    /**
     * The fill circle area
     */
    private RectF mFillCircleRectF = new RectF(0, 0, 0, 0);

    /**
     * The diameter of the circle that will be drawn. Computed in {@link #getArcRect(float)}
     */
    private float mDiameter;

    /**
     * Determine if need to draw the start line
     */
    private boolean mStartLineEnabled;

    /**
     * Determine if need to draw outline area
     */
    private boolean mDrawOutlineArc = true;

    /**
     * We should always draw reached area
     */
    private boolean mDrawReachedArc = true;

    /**
     * Indicates if we need to fill the circle
     */
    private boolean mCircleFillEnabled = false;

    /**
     * The progress angles of the {@link #mOutlineArcRectF} and
     * {@link #mReachedArcRectF}
     */
    private ProgressSweep mProgressSweep;

    /**
     * The suffix of the number.
     */
    private String mSuffix = "%";

    /**
     * The prefix.
     */
    private String mPrefix = "";

    /**
     * A list of listeners we call on animations
     */
    private List<Animator.AnimatorListener> mListeners;

    /**
     * The different types of fill color.
     * Default is like a background from 0-360 degrees,
     * Pie is from 0 to the {@link com.github.OrangeGangsters.circularbarpager.library.CircularBar.ProgressSweep}
     * of the {@link #mReachedArcPaint}
     */
    public enum CircleFillMode {
        DEFAULT(0),
        PIE(1);

        private int value;

        CircleFillMode(int val) {
            this.value = val;
        }

        public final int getValue() {
            return this.value;
        }

        public static CircleFillMode getMode(int val) {
            switch (val) {
                case 1:
                    return PIE;
                case 0:
                default:
                    return DEFAULT;
            }
        }
    }

    /**
     * The defaults for width and color of the reached and outline arcs
     */
    private final int default_clockwise_reached_color = Color.parseColor("#00c853");
    private final int default_clockwise_outline_color = Color.parseColor("#00c853");
    private final int default_counter_clockwise_reached_color = Color.parseColor("#ffffff");
    private final int default_counter_clockwise_outline_color = Color.parseColor("#ffffff");
    private final int default_circle_fill_color = Color.parseColor("#00000000");//fully transparent
    private final int default_circle_fill_mode = CircleFillMode.DEFAULT.getValue();//fully transparent
    private final float default_reached_arc_width;
    private final float default_outline_arc_width;

    /**
     * For save and restore instance of progressbar
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_START_LINE_ENABLED = "progress_start_line_enabled";
    private static final String INSTANCE_CLOCKWISE_REACHED_BAR_HEIGHT = "clockwise_reached_bar_height";
    private static final String INSTANCE_CLOCKWISE_REACHED_BAR_COLOR = "clockwise_reached_bar_color";
    private static final String INSTANCE_CLOCKWISE_OUTLINE_BAR_HEIGHT = "clockwise_outline_bar_height";
    private static final String INSTANCE_CLOCKWISE_OUTLINE_BAR_COLOR = "clockwise_outline_bar_color";
    private static final String INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_HEIGHT = "counter_clockwise_reached_bar_height";
    private static final String INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_COLOR = "counter_clockwise_reached_bar_color";
    private static final String INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_HEIGHT = "counter_clockwise_outline_bar_height";
    private static final String INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_COLOR = "counter_clockwise_outline_bar_color";
    private static final String INSTANCE_CIRCLE_FILL_ENABLED = "progress_pager_fill_circle_enabled";
    private static final String INSTANCE_CIRCLE_FILL_COLOR = "progress_pager_fill_circle_color";
    private static final String INSTANCE_CIRCLE_FILL_MODE = "progress_pager_fill_mode";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";

    public CircularBar(Context context) {
        this(context, null);
    }

    public CircularBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        default_reached_arc_width = dp2px(5f);
        default_outline_arc_width = dp2px(1.0f);

        mListeners = new ArrayList<>();
        loadStyledAttributes(attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateDrawRectF();

        //Draw the fill first so that it does not overlap the arcs
        if (mCircleFillEnabled) {
            switch (CircleFillMode.getMode(mCircleFillMode)) {
                case PIE:
                    //Fill the circle to the point of the reached sweep
                    canvas.drawArc(mFillCircleRectF, mProgressSweep.reachedStart, mProgressSweep.reachedSweep, true, mCircleFillPaint);
                    break;
                case DEFAULT:
                default:
                    //Fill the circle as a background
                    canvas.drawArc(mOutlineArcRectF, ProgressSweep.START_12, 360f, true, mCircleFillPaint);
                    break;
            }
        }

        //Draw the outline arc
        if (mDrawOutlineArc) {
            //Draw the outline bar
            canvas.drawArc(mOutlineArcRectF, mProgressSweep.outlineStart, mProgressSweep.outlineSweep, false, mOutlineArcPaint);
        }

        //Draw the reached arc last so its always on top
        if (mDrawReachedArc) {
            //Draw the bar
            canvas.drawArc(mReachedArcRectF, mProgressSweep.reachedStart, mProgressSweep.reachedSweep, false, mReachedArcPaint);
            if (mStartLineEnabled) {
                //Draw the bar start line
                canvas.drawLine(mReachedArcRectF.centerX(), mReachedArcRectF.top - mClockwiseReachedArcWidth / 2, mReachedArcRectF.centerX() + 1, mReachedArcRectF.top + mClockwiseReachedArcWidth * 1.5f, mOutlineArcPaint);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_START_LINE_ENABLED, isStartLineEnabled());
        bundle.putFloat(INSTANCE_CLOCKWISE_REACHED_BAR_HEIGHT, getClockwiseReachedArcWidth());
        bundle.putFloat(INSTANCE_CLOCKWISE_OUTLINE_BAR_HEIGHT, getClockwiseOutlineArcWidth());
        bundle.putInt(INSTANCE_CLOCKWISE_REACHED_BAR_COLOR, getClockwiseReachedArcColor());
        bundle.putInt(INSTANCE_CLOCKWISE_OUTLINE_BAR_COLOR, getClockwiseOutlineArcColor());
        bundle.putFloat(INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_HEIGHT, getCounterClockwiseReachedArcWidth());
        bundle.putFloat(INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_HEIGHT, getCounterClockwiseOutlineArcWidth());
        bundle.putInt(INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_COLOR, getCounterClockwiseReachedArcColor());
        bundle.putInt(INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_COLOR, getCounterClockwiseOutlineArcColor());
        bundle.putBoolean(INSTANCE_CIRCLE_FILL_ENABLED, isCircleFillEnabled());
        bundle.putInt(INSTANCE_CIRCLE_FILL_COLOR, getCircleFillColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putFloat(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mStartLineEnabled = bundle.getBoolean(INSTANCE_START_LINE_ENABLED);
            mClockwiseReachedArcWidth = bundle.getFloat(INSTANCE_CLOCKWISE_REACHED_BAR_HEIGHT);
            mClockwiseOutlineArcWidth = bundle.getFloat(INSTANCE_CLOCKWISE_OUTLINE_BAR_HEIGHT);
            mClockwiseArcColor = bundle.getInt(INSTANCE_CLOCKWISE_REACHED_BAR_COLOR);
            mClockwiseOutlineArcColor = bundle.getInt(INSTANCE_CLOCKWISE_OUTLINE_BAR_COLOR);
            mCounterClockwiseReachedArcWidth = bundle.getFloat(INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_HEIGHT);
            mCounterClockwiseOutlineArcWidth = bundle.getFloat(INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_HEIGHT);
            mCounterClockwiseArcColor = bundle.getInt(INSTANCE_COUNTER_CLOCKWISE_REACHED_BAR_COLOR);
            mCounterClockwiseOutlineArcColor = bundle.getInt(INSTANCE_COUNTER_CLOCKWISE_OUTLINE_BAR_COLOR);
            mCircleFillEnabled = bundle.getBoolean(INSTANCE_CIRCLE_FILL_ENABLED);
            mCircleFillColor = bundle.getInt(INSTANCE_CIRCLE_FILL_COLOR);
            mCircleFillMode = bundle.getInt(INSTANCE_CIRCLE_FILL_MODE);
            initializePainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getFloat(INSTANCE_PROGRESS));
            setPrefix(bundle.getString(INSTANCE_PREFIX));
            setSuffix(bundle.getString(INSTANCE_SUFFIX));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Loads the styles and attributes defined in the xml tag of this class
     *
     * @param attrs        The attributes to read from
     * @param defStyleAttr The styles to read from
     */
    public void loadStyledAttributes(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            final TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularViewPager,
                    defStyleAttr, 0);

            mStartLineEnabled = attributes.getBoolean(R.styleable.CircularViewPager_progress_start_line_enabled, true);

            mClockwiseArcColor = attributes.getColor(R.styleable.CircularViewPager_progress_arc_clockwise_color, default_clockwise_reached_color);
            mCounterClockwiseArcColor = attributes.getColor(R.styleable.CircularViewPager_progress_arc_counter_clockwise_color, default_counter_clockwise_reached_color);
            mClockwiseOutlineArcColor = attributes.getColor(R.styleable.CircularViewPager_progress_arc_clockwise_outline_color, default_clockwise_outline_color);
            mCounterClockwiseOutlineArcColor = attributes.getColor(R.styleable.CircularViewPager_progress_arc_counter_clockwise_outline_color, default_counter_clockwise_outline_color);

            mClockwiseReachedArcWidth = attributes.getDimension(R.styleable.CircularViewPager_progress_arc_clockwise_width, default_reached_arc_width);
            mCounterClockwiseReachedArcWidth = attributes.getDimension(R.styleable.CircularViewPager_progress_arc_counter_clockwise_width, default_reached_arc_width);
            mClockwiseOutlineArcWidth = attributes.getDimension(R.styleable.CircularViewPager_progress_arc_clockwise_outline_width, default_outline_arc_width);
            mCounterClockwiseOutlineArcWidth = attributes.getDimension(R.styleable.CircularViewPager_progress_arc_counter_clockwise_outline_width, default_outline_arc_width);

            mCircleFillColor = attributes.getColor(R.styleable.CircularViewPager_progress_pager_fill_circle_color, default_circle_fill_color);
            mCircleFillMode = attributes.getInt(R.styleable.CircularViewPager_progress_pager_fill_mode, default_circle_fill_mode);
            cicleFillEnable(mCircleFillColor != default_circle_fill_color);

            setMax(attributes.getInt(R.styleable.CircularViewPager_progress_arc_max, 100));
            setProgress(attributes.getInt(R.styleable.CircularViewPager_arc_progress, 0));

            attributes.recycle();

            initializePainters();
        }
    }

    /**
     * Measures the space available for our view in {@link #onMeasure(int, int)}
     *
     * @param measureSpec The width or height of the view
     * @param isWidth     True if the measureSpec param is the width, false otherwise
     * @return The usable dimension of the spec
     */
    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    /**
     * Calculates the coordinates of {@link #mOutlineArcRectF} and
     * {@link #mReachedArcRectF}
     */
    private void calculateDrawRectF() {
        mFillCircleRectF = getArcRect(mClockwiseReachedArcWidth);
        mReachedArcRectF = getArcRect(mClockwiseReachedArcWidth / 2);
        mOutlineArcRectF = getArcRect(mClockwiseOutlineArcWidth / 2);
    }

    /**
     * Calculates the coordinates of {@link android.graphics.RectF} that
     * are perfectly within the available window
     *
     * @param offset Half the width of the pain stroke
     * @return The rectF
     */
    private RectF getArcRect(float offset) {
        RectF workingSurface = new RectF();
        workingSurface.left = getPaddingLeft() + offset;
        workingSurface.top = getPaddingTop() + offset;
        workingSurface.right = getWidth() - getPaddingRight() - offset;
        workingSurface.bottom = getHeight() - getPaddingBottom() - offset;

        float width = workingSurface.right - workingSurface.left;
        float height = workingSurface.bottom - workingSurface.top;

        this.mDiameter = Math.min(width, height);
        float radius = mDiameter / 2;
        float centerX = width / 2;
        float centerY = height / 2;

        //float left, float top, float right, float bottom
        return new RectF(centerX - radius + offset, centerY - radius + offset, centerX + radius + offset, centerY + radius + offset);
    }

    /**
     * Initializes the paints used for the bars
     */
    private void initializePainters() {
        mClockwiseReachedArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClockwiseReachedArcPaint.setColor(mClockwiseArcColor);
        mClockwiseReachedArcPaint.setAntiAlias(true);
        mClockwiseReachedArcPaint.setStrokeWidth(mClockwiseReachedArcWidth);
        mClockwiseReachedArcPaint.setStyle(Paint.Style.STROKE);

        mCounterClockwiseReachedArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCounterClockwiseReachedArcPaint.setColor(mCounterClockwiseArcColor);
        mCounterClockwiseReachedArcPaint.setAntiAlias(true);
        mCounterClockwiseReachedArcPaint.setStrokeWidth(mCounterClockwiseReachedArcWidth);
        mCounterClockwiseReachedArcPaint.setStyle(Paint.Style.STROKE);

        mClockwiseOutlineArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClockwiseOutlineArcPaint.setColor(mClockwiseOutlineArcColor);
        mClockwiseOutlineArcPaint.setAntiAlias(true);
        mClockwiseOutlineArcPaint.setStrokeWidth(mClockwiseOutlineArcWidth);
        mClockwiseOutlineArcPaint.setStyle(Paint.Style.STROKE);

        mCounterClockwiseOutlineArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCounterClockwiseOutlineArcPaint.setColor(mCounterClockwiseOutlineArcColor);
        mCounterClockwiseOutlineArcPaint.setAntiAlias(true);
        mCounterClockwiseOutlineArcPaint.setStrokeWidth(mCounterClockwiseOutlineArcWidth);
        mCounterClockwiseOutlineArcPaint.setStyle(Paint.Style.STROKE);

        mCircleFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleFillPaint.setColor(mCircleFillColor);
        mCircleFillPaint.setAntiAlias(true);
        mCircleFillPaint.setStyle(Paint.Style.FILL);

        //Defaults
        mReachedArcPaint = mClockwiseReachedArcPaint;
        mOutlineArcPaint = mClockwiseOutlineArcPaint;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //Round off the sweep angles that can result from rounding errors at the end
        mProgressSweep.reachedSweep = Math.round(mProgressSweep.reachedSweep);
        mProgressSweep.outlineSweep = Math.round(mProgressSweep.outlineSweep);
        invalidate();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    /**
     * Animate the change in progress of this view
     *
     * @param start    The value to start from, between 0-100
     * @param end      The value to set it to, between 0-100
     * @param duration The the time to run the animation over
     */
    public void animateProgress(int start, int end, int duration) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(Glider.glide(Skill.QuadEaseInOut, duration, ObjectAnimator.ofFloat(this, "progress", start, end)));
        set.setDuration(duration);
        set = addListenersToSet(set);
        set.start();
    }

    /**
     * Adds the current listeners to the {@link com.nineoldandroids.animation.AnimatorSet}
     * before animation starts
     *
     * @param set The set to add listeners to
     * @return The set with listeners added
     */
    protected AnimatorSet addListenersToSet(AnimatorSet set) {
        if (mListeners != null && set != null) {
            set.addListener(this);
            for (Animator.AnimatorListener listener : mListeners) {
                set.addListener(listener);
            }
        }
        return set;
    }

    /**
     * Method to add a listener to call on animations
     *
     * @param listener The listener to call
     */
    public void addListener(Animator.AnimatorListener listener) {
        mListeners.add(listener);
    }

    /**
     * Removes the listener provided
     *
     * @param listener The listener to remove
     * @return True if it was in the list and removed, false otherwise
     */
    public boolean removeListener(Animator.AnimatorListener listener) {
        return mListeners.remove(listener);
    }

    /**
     * Removes all animation listeners
     */
    public void removeAllListeners() {
        mListeners = new ArrayList<>();
    }

    /**
     * Get the suffix
     *
     * @return
     */
    public String getSuffix() {
        return mSuffix;
    }

    /**
     * Get the prefix
     *
     * @return
     */
    public String getPrefix() {
        return mPrefix;
    }

    /**
     * The boolean that indicates if we must draw the start line or not
     *
     * @return
     */
    public boolean isStartLineEnabled() {
        return mStartLineEnabled;
    }

    /**
     * The float computed in {@link #getArcRect(float)} that is the diameter of the drawn circle.
     *
     * @return
     */
    public float getDiameter() {
        return mDiameter;
    }

    /**
     * The clockwise outline arc color
     *
     * @return
     */
    public int getClockwiseOutlineArcColor() {
        return mClockwiseOutlineArcColor;
    }

    /**
     * The clockwise reached arc color
     *
     * @return
     */
    public int getClockwiseReachedArcColor() {
        return mClockwiseArcColor;
    }

    /**
     * The current progress
     *
     * @return
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Get the max of the reached arc, defaults to 100
     *
     * @return
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Get the height of the {@link #mClockwiseReachedArcWidth}
     *
     * @return
     */
    public float getClockwiseReachedArcWidth() {
        return mClockwiseReachedArcWidth;
    }

    /**
     * Get the height of the {@link #mClockwiseOutlineArcWidth}
     *
     * @return
     */
    public float getClockwiseOutlineArcWidth() {
        return mClockwiseOutlineArcWidth;
    }

    /**
     * The counter clockwise outline arc color
     *
     * @return
     */
    public int getCounterClockwiseReachedArcColor() {
        return mCounterClockwiseArcColor;
    }

    /**
     * The counter clockwise outline arc color
     *
     * @return
     */
    public int getCounterClockwiseOutlineArcColor() {
        return mCounterClockwiseOutlineArcColor;
    }

    /**
     * The color to fill the circle center
     *
     * @return
     */
    public int getCircleFillColor() {
        return mCircleFillColor;
    }

    /**
     * Get the height of the {@link #mCounterClockwiseReachedArcWidth}
     *
     * @return
     */
    public float getCounterClockwiseReachedArcWidth() {
        return mCounterClockwiseReachedArcWidth;
    }

    /**
     * Get the height of the {@link #mCounterClockwiseOutlineArcWidth}
     *
     * @return
     */
    public float getCounterClockwiseOutlineArcWidth() {
        return mCounterClockwiseOutlineArcWidth;
    }

    /**
     * Sets whether the circle drawn inside and filled.
     *
     * @return
     */
    public boolean isCircleFillEnabled() {
        return mCircleFillEnabled;
    }

    /**
     * Sets the enabled state of the circle fill
     *
     * @param enable
     */
    public void cicleFillEnable(boolean enable) {
        mCircleFillEnabled = enable;
    }

    /**
     * Sets the {@link #mCounterClockwiseOutlineArcWidth} and invalidates the view
     *
     * @param width The height in dp to set
     */
    public void setCounterClockwiseOutlineArcWidth(float width) {
        this.mCounterClockwiseOutlineArcWidth = width;
        invalidate();
    }

    /**
     * Sets the {@link #mCounterClockwiseReachedArcWidth} and invalidates the view
     *
     * @param width The height in dp to set
     */
    public void setCounterClockwiseReachedArcWidth(float width) {
        this.mCounterClockwiseReachedArcWidth = width;
        invalidate();
    }

    /**
     * Sets the {@link #mCounterClockwiseOutlineArcColor} and invalidates the view
     *
     * @param color The hex color to set
     */
    public void setCounterClockwiseOutlineArcColor(int color) {
        this.mCounterClockwiseOutlineArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mCircleFillColor} and invalidates the view
     *
     * @param color The hex color to set
     */
    public void setCircleFillColor(int color) {
        this.mCircleFillColor = color;
        cicleFillEnable(mCircleFillColor != default_circle_fill_color);
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mStartLineEnabled} and invalidates the view. {@link #mStartLineEnabled}
     * defaults to true
     *
     * @param startLineEnabled True to display the line, false otherwise.
     */
    public void setStartLineEnabled(boolean startLineEnabled) {
        this.mStartLineEnabled = startLineEnabled;
        invalidate();
    }

    /**
     * Sets the {@link #mCounterClockwiseArcColor} and invalidates the view
     *
     * @param color The hex color to set
     */
    public void setCounterClockwiseArcColor(int color) {
        this.mCounterClockwiseArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mClockwiseArcColor} and invalidates the view
     *
     * @param color The hex color to set
     */
    public void setClockwiseReachedArcColor(int color) {
        this.mClockwiseArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mClockwiseOutlineArcColor} and invalidates the view
     *
     * @param color The hex color to set
     */
    public void setClockwiseOutlineArcColor(int color) {
        this.mClockwiseOutlineArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mClockwiseReachedArcWidth} and invalidates the view
     *
     * @param width The height in dp to set
     */
    public void setClockwiseReachedArcWidth(float width) {
        mClockwiseReachedArcWidth = width;
        invalidate();
    }

    /**
     * Sets the {@link #mClockwiseOutlineArcWidth} and invalidates the view
     *
     * @param width The height in dp to set
     */
    public void setClockwiseOutlineArcWidth(float width) {
        mClockwiseOutlineArcWidth = width;
        invalidate();
    }

    /**
     * Sets the {@link #mMax} and invalidates the view
     *
     * @param max The height in dp to set
     */
    public void setMax(int max) {
        if (max > 0) {
            this.mMax = max;
            invalidate();
        }
    }

    /**
     * Sets the {@link #mSuffix}
     *
     * @param suffix The suffix
     */
    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    /**
     * Sets the {@link #mPrefix}
     *
     * @param prefix The prefix
     */
    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    /**
     * @param newProgress
     */
    public void setProgress(float newProgress) {
        if (mProgressSweep == null) {
            this.mProgressSweep = new ProgressSweep(newProgress);
        } else {
            mProgressSweep.enforceBounds(newProgress);
            mProgressSweep.updateAngles();
        }

        invalidate();
    }

    /**
     * Convert from dp to pixels according to device density
     *
     * @param dp The length in dip to convert
     * @return The pixel equivalent for this device
     */
    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * Convert from text sp to dp according to device density
     *
     * @param sp The length in sp to convert
     * @return The pixel equivalent for this device
     */
    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * Private class for calculating and holding the sweep angles of the
     * arcs we are drawing
     */
    private class ProgressSweep {
        /*
         * Possible starting positions at 12, 3, 6, and 9 o'clock positions
         *     12
         * 9        3
         *     6
         */
        /**
         * 12 o'clock
         */
        public static final float START_12 = 270f;
        /**
         * 3 o'clock
         */
        public static final float START_3 = 0f;
        /**
         * 6 o'clock
         */
        public static final float START_6 = 90f;
        /**
         * 9 o'clock
         */
        public static final float START_9 = 180f;

        /**
         * Starting angle position of the reached arc
         */
        public float reachedStart = START_12;

        /**
         * The sweep angle of the reached arc
         */
        public float reachedSweep = 0f;

        /**
         * Starting angle position of the outline arc
         */
        public float outlineStart = reachedStart;

        /**
         * The sweep angle of the outline arc
         */
        public float outlineSweep = 360f;

        public ProgressSweep(float progress) {
            enforceBounds(progress);
            updateAngles();
        }

        /**
         * Enforce the progress boundary at the max value allowed
         */
        public void enforceBounds(float newProgress) {
            if (Math.abs(newProgress) == Math.abs(mMax)) {
                return;
            }
            progress = newProgress % mMax;
        }

        /**
         * Update the angles of the arcs
         */
        public void updateAngles() {
            if (progress >= 0) {
                reachedStart = START_12;
                reachedSweep = progress / mMax * 360f;
                outlineStart = (START_12 + reachedSweep) % 360f;
                outlineSweep = 360f - reachedSweep;

                //paints
                mReachedArcPaint = mClockwiseReachedArcPaint;
                mOutlineArcPaint = mClockwiseOutlineArcPaint;
            } else {
                reachedSweep = Math.abs(progress / mMax * 360f);
                reachedStart = START_12 - reachedSweep;
                outlineStart = START_12;
                outlineSweep = 360f - reachedSweep;

                //paints
                mReachedArcPaint = mCounterClockwiseReachedArcPaint;
                mOutlineArcPaint = mCounterClockwiseOutlineArcPaint;
            }
        }
    }
}

