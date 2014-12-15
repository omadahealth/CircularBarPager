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
package daniel.olivier.stoyan.pager;


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


/**
 * Created by stoyan and olivier on 12/9/14.
 */
public class CircularBar extends View {
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
     * The progress area bar color
     */
    private int mReachedArcColor;

    /**
     * The bar unreached area color.
     */
    private int mUnreachedArcColor;

    /**
     * The width of the reached area
     */
    private float mReachedArcWidth;

    /**
     * The width of the unreached area
     */
    private float mUnreachedArcWidth;

    /**
     * The progress angles of the {@link #mUnReachedArcRectF} and
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
     * The defaults for width and color of the reached and unreached arcs
     */
    private final int default_reached_color = Color.parseColor("#00c853");
    private final int default_unreached_color = Color.parseColor("#00c853");
    private final float default_reached_arc_width;
    private final float default_unreached_arc_width;

    /**
     * For save and restore instance of progressbar
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height";
    private static final String INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";

    /**
     * The Paint of the reached area.
     */
    private Paint mReachedBarPaint;

    /**
     * The Painter of the unreached area.
     */
    private Paint mUnreachedBarPaint;

    /**
     * The reached bar area rect.
     */
    private RectF mReachedArcRectF = new RectF(0,0,0,0);

    /**
     * The unreached bar area
     */
    private RectF mUnReachedArcRectF = new RectF(0,0,0,0);

    /**
     * Determine if need to draw unreached area
     */
    private boolean mDrawUnreachedBar = true;

    /**
     * We should always draw reached area
     */
    private boolean mDrawReachedBar = true;

    public CircularBar(Context context) {
        this(context, null);
    }

    public CircularBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.CircularBarPagerStyle);
    }

    public CircularBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        default_reached_arc_width = dp2px(5f);
        default_unreached_arc_width = dp2px(1.0f);

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

        if(mDrawReachedBar){
            //Draw the bar
            canvas.drawArc(mReachedArcRectF, mProgressSweep.reachedStart, mProgressSweep.reachedSweep, false, mReachedBarPaint);
            //Draw the bar start line
            canvas.drawLine(mReachedArcRectF.centerX(), mReachedArcRectF.top - mReachedArcWidth/2, mReachedArcRectF.centerX() + 1, mReachedArcRectF.top + mReachedArcWidth*1.5f, mUnreachedBarPaint);
        }

        if(mDrawUnreachedBar){
            //Draw the unreached bar
            canvas.drawArc(mUnReachedArcRectF, mProgressSweep.unReachedStart, mProgressSweep.unRreachedSweep, false, mUnreachedBarPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT,getReachedBarHeight());
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight());
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR,getReachedBarColor());
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR,getUnreachedBarColor());
        bundle.putInt(INSTANCE_MAX,getMax());
        bundle.putFloat(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX,getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            final Bundle bundle = (Bundle)state;
            mReachedArcWidth = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
            mUnreachedArcWidth = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT);
            mReachedArcColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
            mUnreachedArcColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR);
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
     * @param attrs The attributes to read from
     * @param defStyleAttr The styles to read from
     */
    public void loadStyledAttributes(AttributeSet attrs, int defStyleAttr) {
        final TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularBar,
                defStyleAttr, 0);

        mReachedArcColor = attributes.getColor(R.styleable.CircularBar_progress_reached_color, default_reached_color);
        mUnreachedArcColor = attributes.getColor(R.styleable.CircularBar_progress_unreached_color, default_unreached_color);

        mReachedArcWidth = attributes.getDimension(R.styleable.CircularBar_progress_reached_arc_width, default_reached_arc_width);
        mUnreachedArcWidth = attributes.getDimension(R.styleable.CircularBar_progress_unreached_arc_width, default_unreached_arc_width);


        setMax(attributes.getInt(R.styleable.CircularBar_max, 100));
        setProgress(attributes.getInt(R.styleable.CircularBar_progress,0));

        attributes.recycle();

        initializePainters();
    }

    /**
     * Measures the space available for our view in {@link #onMeasure(int, int)}
     * @param measureSpec The width or height of the view
     * @param isWidth True if the measureSpec param is the width, false otherwise
     * @return The usable dimension of the spec
     */
    private int measure(int measureSpec, boolean isWidth){
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth?getPaddingLeft()+getPaddingRight():getPaddingTop()+getPaddingBottom();
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if(mode == MeasureSpec.AT_MOST){
                if(isWidth) {
                    result = Math.max(result, size);
                }
                else{
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    /**
     * Calculates the coordinates of {@link #mUnReachedArcRectF} and
     * {@link #mReachedArcRectF}
     */
    private void calculateDrawRectF(){
        mReachedArcRectF = getArcRect(mReachedArcWidth/2);
        mUnReachedArcRectF = getArcRect(mUnreachedArcWidth/2);
    }

    /**
     * Calculates the coordinates of {@link android.graphics.RectF} that
     * are perfectly within the available window
     * @param offset Half the width of the pain stroke
     * @return The rectF
     */
    private RectF getArcRect(float offset){
        RectF workingSurface = new RectF();
        workingSurface.left = getPaddingLeft() + offset;
        workingSurface.top = getPaddingTop() + offset;
        workingSurface.right = getWidth() - getPaddingRight() - offset;
        workingSurface.bottom = getHeight() - getPaddingBottom() - offset;

        float width = workingSurface.right - workingSurface.left;
        float height = workingSurface.bottom - workingSurface.top;
        float radius = Math.min(width, height)/2;
        float centerX = width/2;
        float centerY = height/2;

        //float left, float top, float right, float bottom
        return new RectF(centerX - radius + offset, centerY - radius + offset, centerX + radius + offset, centerY + radius + offset);
    }

    /**
     * Initializes the paints used for the bars
     */
    private void initializePainters(){
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedArcColor);
        mReachedBarPaint.setAntiAlias(true);
        mReachedBarPaint.setStrokeWidth(mReachedArcWidth);
        mReachedBarPaint.setStyle(Paint.Style.STROKE);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedArcColor);
        mUnreachedBarPaint.setAntiAlias(true);
        mUnreachedBarPaint.setStrokeWidth(mUnreachedArcWidth);
        mUnreachedBarPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Animate the change in progress of this view
     *
     * @param start    The value to start from, between 0-100
     * @param end      The value to set it to, between 0-100
     * @param duration The the time to run the animation over
     */
    public void animateProgress(int start,int end, int duration) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(Glider.glide(Skill.QuadEaseInOut, duration, ObjectAnimator.ofFloat(this, "progress", start, end)));
        set.setDuration(duration);
        set = addListenersToSet(set);
        set.start();
    }

    /**
     * Adds the current listeners to the {@link com.nineoldandroids.animation.AnimatorSet}
     * before animation starts
     * @param set The set to add listeners to
     * @return The set with listeners added
     */
    protected AnimatorSet addListenersToSet(AnimatorSet set){
        if(mListeners != null && set != null){
            for(Animator.AnimatorListener listener : mListeners){
                set.addListener(listener);
            }
        }
        return set;
    }

    /**
     * Method to add a listener to call on animations
     * @param listener The listener to call
     */
    public void addListener(Animator.AnimatorListener listener){
        mListeners.add(listener);
    }

    /**
     * Removes the listener provided
     * @param listener The listener to remove
     * @return True if it was in the list and removed, false otherwise
     */
    public boolean removeListener(Animator.AnimatorListener listener){
        return mListeners.remove(listener);
    }

    /**
     * Removes all animation listeners
     */
    public void removeAllListeners(){
        mListeners = new ArrayList<>();
    }

    /**
     * Get the suffix
     * @return
     */
    public String getSuffix(){
        return mSuffix;
    }

    /**
     * Get the prefix
     * @return
     */
    public String getPrefix(){
        return mPrefix;
    }

    /**
     * The unreached arc color
     * @return
     */
    public int getUnreachedBarColor() {
        return mUnreachedArcColor;
    }

    /**
     * The reached arc color
     * @return
     */
    public int getReachedBarColor() {
        return mReachedArcColor;
    }

    /**
     * The current progress
     * @return
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Get the max of the reached arc, defaults to 100
     * @return
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Get the height of the {@link #mReachedArcWidth}
     * @return
     */
    public float getReachedBarHeight(){
        return mReachedArcWidth;
    }

    /**
     * Get the height of the {@link #mUnreachedArcWidth}
     * @return
     */
    public float getUnreachedBarHeight(){
        return mUnreachedArcWidth;
    }

    /**
     * Sets the {@link #mReachedArcColor} and invalidates the view
     * @param color The hex color to set
     */
    public void setReachedBarColor(int color) {
        this.mReachedArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mUnreachedBarPaint} and invalidates the view
     * @param color The hex color to set
     */
    public void setUnreachedBarColor(int color) {
        this.mUnreachedArcColor = color;
        initializePainters();
        invalidate();
    }

    /**
     * Sets the {@link #mReachedArcWidth} and invalidates the view
     * @param height The height in dp to set
     */
    public void setReachedBarHeight(float height){
        mReachedArcWidth = height;
        invalidate();
    }

    /**
     * Sets the {@link #mUnreachedArcWidth} and invalidates the view
     * @param height The height in dp to set
     */
    public void setUnreachedBarHeight(float height){
        mUnreachedArcWidth = height;
        invalidate();
    }

    /**
     * Sets the {@link #mMax} and invalidates the view
     * @param max The height in dp to set
     */
    public void setMax(int max) {
        if(max > 0){
            this.mMax = max;
            invalidate();
        }
    }

    /**
     * Sets the {@link #mSuffix}
     * @param suffix The suffix
     */
    public void setSuffix(String suffix){
        if(suffix == null){
            mSuffix = "";
        }else{
            mSuffix = suffix;
        }
    }

    /**
     * Sets the {@link #mPrefix}
     * @param prefix The prefix
     */
    public void setPrefix(String prefix){
        if(prefix == null)
            mPrefix = "";
        else{
            mPrefix = prefix;
        }
    }

    /**
     *
     * @param Progress
     */
    public void setProgress(float Progress) {
        if(Progress <= getMax()  && Progress >= 0){
            this.progress = Progress;
            if(mProgressSweep == null){
                this.mProgressSweep = new ProgressSweep(progress);
            }else{
                mProgressSweep.enforceBounds();
                mProgressSweep.updateAngles();
            }

            invalidate();
        }
    }

    /**
     * Convert from dp to pixels according to device density
     * @param dp The length in dip to convert
     * @return The pixel equivalent for this device
     */
    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    /**
     * Convert from text sp to dp according to device density
     * @param sp The length in sp to convert
     * @return The pixel equivalent for this device
     */
    public float sp2px(float sp){
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
        private static final float START_12 = 270f;
        /**
         * 3 o'clock
         */
        private static final float START_3 = 0f;
        /**
         * 6 o'clock
         */
        private static final float START_6 = 90f;
        /**
         * 9 o'clock
         */
        private static final float START_9 = 180f;

        /**
         * Starting angle position of the reached arc
         */
        public float reachedStart = START_12;

        /**
         * The sweep angle of the reached arc
         */
        public float reachedSweep = 0f;

        /**
         * Starting angle position of the unreached arc
         */
        public float unReachedStart = reachedStart;

        /**
         * The sweep angle of the unreached arc
         */
        public float unRreachedSweep = 360f;

        public ProgressSweep(float progress){
            CircularBar.this.progress = progress;
            enforceBounds();
            updateAngles();
        }

        /**
         * Enforce the progress boundary at the max value allowed
         */
        public void enforceBounds() {
            if(progress < 0 ){
                progress = 0;
            }
            if(progress > mMax){
                progress = mMax;
            }
        }

        /**
         * Update the angles of the arcs
         */
        public void updateAngles() {
            reachedSweep = progress /mMax * 360f;
            unReachedStart = 270f + progress /mMax * 360f;
            unRreachedSweep = 360f - (progress /mMax * 360f);
        }

    }
}

