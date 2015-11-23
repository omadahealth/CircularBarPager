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
package com.github.OrangeGangsters.circularbarpager.library.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by oliviergoutay on 12/9/14.
 */
public class WrapContentViewPager extends ViewPager {

    private static final String TAG = "WrapContentViewPager";

    public WrapContentViewPager(Context context) {
        super(context);
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Fixes for "java.lang.IndexOutOfBoundsException Invalid index 0, size is 0"
     * on "android.support.v4.view.ViewPager.performDrag"
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (event == null || getAdapter() == null || getAdapter().getCount() == 0) {
                return false;
            }
            return super.onInterceptTouchEvent(event);
        } catch (RuntimeException e) {
            Log.e(TAG, "Exception during WrapContentViewPager onTouchEvent: " +
                    "index out of bound, or nullpointer even if we check the adapter before " + e.toString());
            return false;
        }
    }

    /**
     * Fixes for "java.lang.IndexOutOfBoundsException Invalid index 0, size is 0"
     * on "android.support.v4.view.ViewPager.performDrag"
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (ev == null || getAdapter() == null || getAdapter().getCount() == 0) {
                return false;
            }
            return super.onTouchEvent(ev);
        } catch (RuntimeException e) {
            Log.e(TAG, "Exception during WrapContentViewPager onTouchEvent: " +
                    "index out of bound, or nullpointer even if we check the adapter before " + e.toString());
            return false;
        }
    }

    /**
     * Allows to redraw the view size to wrap the content of the bigger child.
     *
     * @param widthMeasureSpec  with measured
     * @param heightMeasureSpec height measured
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int mode = MeasureSpec.getMode(heightMeasureSpec);

            if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int height = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child != null) {
                        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                        int h = child.getMeasuredHeight();
                        if (h > height) {
                            height = h;
                        }
                    }
                }
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (RuntimeException e) {
            Log.e(TAG, "Exception during WrapContentViewPager onMeasure " + e.toString());
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return i;
    }
}
