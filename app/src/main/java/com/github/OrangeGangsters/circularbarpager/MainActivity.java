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
package com.github.OrangeGangsters.circularbarpager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.OrangeGangsters.circularbarpager.library.CircularBarPager;
import com.nineoldandroids.animation.Animator;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import daniel.olivier.stoyan.circularbarpager.R;

/**
 * Created by stoyan and oliviergoutay on 12/9/14.
 */
public class MainActivity extends Activity {

    private CircularBarPager mCircularBarPager;
    private Button mButton;
    private EditText mEditText;
    private int mHowmanypies=3;

    /**
     * The animation time in milliseconds that we take to display the steps taken
     */
    private static final int BAR_ANIMATION_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeButtonVisible(false);

        mCircularBarPager.getCircularBar().animateProgress(0, 75, 1000);
    }

    private void makeButtonVisible(boolean b){
        if(b) {
            mButton.setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.VISIBLE);
        }else{
            mButton.setVisibility(View.INVISIBLE);
            mEditText.setVisibility(View.INVISIBLE);
        }
    }
    private void initViews(){
        mCircularBarPager = (CircularBarPager) findViewById(R.id.circularBarPager);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.edittext);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHowmanypies = Integer.parseInt(mEditText.getText().toString());
                List<Boolean> partsBoolean = new ArrayList<>();
                for(int i=0; i<mHowmanypies; i++) {
                    partsBoolean.add(true);
                }
                mCircularBarPager.getCircularBar().animateProgress(partsBoolean, BAR_ANIMATION_TIME);
            }
        });
        View[] views = new View[3];
        views[0] = new DemoView(this);
        views[1] = new DemoView(this);
        views[2] = new DemoView(this);


        mCircularBarPager.setViewPagerAdapter(new DemoPagerAdapter(this, views));

        ViewPager viewPager = mCircularBarPager.getViewPager();
        viewPager.setClipToPadding(true);

        CirclePageIndicator circlePageIndicator = mCircularBarPager.getCirclePageIndicator();
        circlePageIndicator.setFillColor(ContextCompat.getColor(this, R.color.light_grey));
        circlePageIndicator.setPageColor(ContextCompat.getColor(this,R.color.very_light_grey));
        circlePageIndicator.setStrokeColor(ContextCompat.getColor(this,R.color.transparent));

        //Do stuff based on animation
        mCircularBarPager.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //TODO do stuff
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //Do stuff based on when pages change
        circlePageIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if(mCircularBarPager!= null && mCircularBarPager.getCircularBar() != null){
                    switch (position){
                        case 0:
                            makeButtonVisible(false);
                            mCircularBarPager.getCircularBar().animateProgress(-25, 100, BAR_ANIMATION_TIME);
                            break;
                        case 1:
                            makeButtonVisible(false);
                            mCircularBarPager.getCircularBar().animateProgress(100, -75, BAR_ANIMATION_TIME);
                            break;
                        case 2:
                            makeButtonVisible(true);
                            List<Boolean> partsBoolean = new ArrayList<>();
                            for(int i=0; i<mHowmanypies; i++) {
                                partsBoolean.add(true);
                            }
                            mCircularBarPager.getCircularBar().animateProgress(partsBoolean, BAR_ANIMATION_TIME);
                            break;
                        default:
                            mCircularBarPager.getCircularBar().animateProgress(0, 75, BAR_ANIMATION_TIME);
                            break;
                    }
                }
            }
        });
    }
}
