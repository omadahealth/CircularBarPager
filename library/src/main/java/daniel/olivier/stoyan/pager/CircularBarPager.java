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
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;

import daniel.olivier.stoyan.pager.viewpager.FadeViewPagerTransformer;

/**
 * Created by oliviergoutay and stoyan on 12/10/14.
 */
public class CircularBarPager extends RelativeLayout {

    /**
     * The current {@link android.content.Context} of the app
     */
    private Context mContext;

    /**
     * The inflated {@link daniel.olivier.stoyan.pager.CircularBar}
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
        this(context, attrs, R.attr.CircularBarPagerStyle);
    }

    public CircularBarPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initializeView(attrs, defStyleAttr);
    }

    /**
     * Init the view by getting the {@link daniel.olivier.stoyan.pager.CircularBar},
     * the {@link android.support.v4.view.ViewPager} and the {@link com.viewpagerindicator.CirclePageIndicator}.
     * Init also some default values as PageTranformer etc...
     */
    private void initializeView(AttributeSet attrs, int defStyleAttr) {
        isPaddingSet = false;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.circularbar_view_pager, this);

        mCircularBar = (CircularBar) view.findViewById(R.id.circular_bar);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mCirclePageIndicator = (CirclePageIndicator) view.findViewById(R.id.circle_page_indicator);

        //Default init
        mCircularBar.loadStyledAttributes(attrs, defStyleAttr);
        mViewPager.setPageTransformer(false, new FadeViewPagerTransformer());
    }

    /**
     * Apply a {@link android.support.v4.view.ViewPager#setPadding(int, int, int, int)} and
     * {@link android.support.v4.view.ViewPager#setPageMargin(int)} in order to get a nicer animation
     * on the {@link android.support.v4.view.ViewPager} inside the {@link daniel.olivier.stoyan.pager.CircularBar}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!isPaddingSet) {
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

    public CircularBar getCircularBar() {
        return mCircularBar;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

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
