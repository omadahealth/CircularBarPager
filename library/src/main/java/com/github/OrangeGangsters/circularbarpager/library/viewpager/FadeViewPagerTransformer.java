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

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by oliviergoutay on 12/9/14.
 */
public class FadeViewPagerTransformer implements ViewPager.PageTransformer {

    /**
     * Used for adding a fadein/fadeout animation in the ViewPager transition.
     * Must be used with {@link android.support.v4.view.ViewPager#setPageTransformer(boolean, android.support.v4.view.ViewPager.PageTransformer)}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void transformPage(View view, float position) {
        //Calculate real position (with padding)
        position -= (float) ((ViewPager) view.getParent()).getPaddingRight() / (float) view.getWidth();
        if (position <= -1.0f || position >= 1.0f) {
            view.setAlpha(0);
            view.setTranslationX(0);
        } else if (position < 0.0001f && position > -0.0001f) {
            view.setAlpha(1);
            view.setTranslationX(1);
        } else if (position <= 0.0f || position <= 1.0f) {
            //Get the page margin to calculate the alpha relatively to it
            float pageMargin = -(float) ((ViewPager) view.getParent()).getPageMargin() / (float) view.getWidth();
            float alpha = position / (1.0f - pageMargin);
            alpha = (alpha <= 0) ? alpha + 1 : 1 - alpha;
            view.setAlpha(alpha);
            //Reduce the translation by factor 2
            view.setTranslationX(-position * ((float) view.getWidth() / 1.5f));
        }
    }

}
