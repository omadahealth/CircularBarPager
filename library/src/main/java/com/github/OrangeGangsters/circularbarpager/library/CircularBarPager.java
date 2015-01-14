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
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.OrangeGangsters.circularbarpager.library.viewpager.FadeViewPagerTransformer;
import com.nineoldandroids.animation.Animator;
import com.viewpagerindicator.CirclePageIndicator;

import daniel.olivier.stoyan.pager.R;

/**
 * Created by oliviergoutay and stoyan on 12/10/14.
 */
public class CircularBarPager extends RelativeLayout {

    /**
     * The current {@link android.content.Context} of the app
     */
    private Context mContext;

    /**
     * The inflated {@link CircularBar}
     */
    private CircularBar mCircularBar;

    /**
     * The inflated {@link android.support.v4.view.ViewPager}
     */
    private ViewPager mViewPager;

    /**
     * The inflated {@link com.viewpagerindicator.CirclePageIndicator}
     */
    private CirclePageIndicator mCirclePageIndicator;

    /**
     * The ratio used to set the padding to the {@link android.support.v4.view.ViewPager}
     * relative to the size of this complete view.
     */
    private int mPaddingRatio = 12;

    /**
     * Used for getting the info that the padding has already been set in {@link #onMeasure(int, int)}.
     * Improve performance.
     */
    private boolean isPaddingSet;

    public CircularBarPager(Context context) {
        this(context, null);
    }

    public CircularBarPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularBarPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initializeView(attrs, defStyleAttr);
    }

    /**
     * Init the view by getting the {@link CircularBar},
     * the {@link android.support.v4.view.ViewPager} and the {@link com.viewpagerindicator.CirclePageIndicator}.
     * Init also some default values as PageTranformer etc...
     */
    private void initializeView(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            final TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularViewPager,
                    defStyleAttr, 0);

            boolean enableOnClick = attributes.getBoolean(R.styleable.CircularViewPager_progress_pager_on_click_enabled, false);
            isPaddingSet = false;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.circularbar_view_pager, this);

            mCircularBar = (CircularBar) view.findViewById(R.id.circular_bar);
            mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
            mCirclePageIndicator = (CirclePageIndicator) view.findViewById(R.id.circle_page_indicator);

            //Default init
            if(mCircularBar != null){
                mCircularBar.loadStyledAttributes(attrs, defStyleAttr);
            }
            if(mViewPager != null){
                mViewPager.setPageTransformer(false, new FadeViewPagerTransformer());
            }


            //If we enable onClick, ie. we can switch between pages with both a swipe and a touch
            //Touch just goes to the next page % number of pages
            if (enableOnClick) {
                final GestureDetectorCompat tapGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % mViewPager.getAdapter().getCount());
                        return super.onSingleTapConfirmed(e);
                    }
                });
                if(mViewPager != null){
                    mViewPager.setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            tapGestureDetector.onTouchEvent(event);
                            return false;
                        }
                    });
                }

            }
        }
    }

    /**
     * Apply a {@link android.support.v4.view.ViewPager#setPadding(int, int, int, int)} and
     * {@link android.support.v4.view.ViewPager#setPageMargin(int)} in order to get a nicer animation
     * on the {@link android.support.v4.view.ViewPager} inside the {@link CircularBar}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!isPaddingSet && mViewPager != null) {
            int paddingForViewPager = this.getMeasuredWidth() / mPaddingRatio;
            mViewPager.setPadding(paddingForViewPager, mViewPager.getPaddingTop(), paddingForViewPager, mViewPager.getPaddingBottom());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mViewPager.setPageMargin(-(int) (((float) mViewPager.getPaddingLeft() + (float) mViewPager.getPaddingRight()) * 2.0f));
            }

            isPaddingSet = true;
        }
    }

    public void setViewPagerAdapter(PagerAdapter pagerAdapter) {
        mViewPager.setAdapter(pagerAdapter);
        mCirclePageIndicator.setViewPager(mViewPager);
    }


    /**
     * Method to add a listener to call on animations
     *
     * @param listener The listener to call
     */
    public void addListener(Animator.AnimatorListener listener) {
        mCircularBar.addListener(listener);
    }

    /**
     * Removes the listener provided
     *
     * @param listener The listener to remove
     * @return True if it was in the list and removed, false otherwise
     */
    public boolean removeListener(Animator.AnimatorListener listener) {
        return mCircularBar.removeListener(listener);
    }

    /**
     * Removes all animation listeners
     */
    public void removeAllListeners() {
        mCircularBar.removeAllListeners();
    }

    /**
     * @return The circular bar of this view
     */
    public CircularBar getCircularBar() {
        return mCircularBar;
    }

    /**
     * @return The view pager of this view
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * @return The Page indicator of this view
     */
    public CirclePageIndicator getCirclePageIndicator() {
        return mCirclePageIndicator;
    }

    /**
     * Set the padding ratio between the size of this view and the padding that should have {@link android.support.v4.view.ViewPager}
     * inside of it. Set to 12 by default. If you want to disable, set it to 0.
     *
     * @param paddingRatio the ratio
     */
    public void setPaddingRatio(int paddingRatio) {
        this.mPaddingRatio = paddingRatio;
        isPaddingSet = false;
    }
}
