package daniel.olivier.stoyan.circularbarpager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;

import daniel.olivier.stoyan.library.CircularBarPager;
import daniel.olivier.stoyan.library.viewpager.HomeFragmentViewPagerAdapter;

/**
 * Created by stoyan and oliviergoutay on 12/9/14.
 */
public class MainActivity extends Activity {

    private CircularBarPager mCircularBarPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircularBarPager = (CircularBarPager) findViewById(R.id.circularBarPager);
//        mCircularBarPager.setProgress(70);

        View[] views = new View[2];
        views[0] = new ExampleAdapterView(this);
        views[1] = new ExampleAdapterView(this);

        mCircularBarPager.setViewPagerAdapter(new HomeFragmentViewPagerAdapter(this, views));

        ViewPager viewPager = mCircularBarPager.getViewPager();
        viewPager.setClipToPadding(true);

        CirclePageIndicator circlePageIndicator = mCircularBarPager.getCirclePageIndicator();
        circlePageIndicator.setSnap(true);
        circlePageIndicator.setFillColor(getResources().getColor(R.color.light_grey));
        circlePageIndicator.setPageColor(getResources().getColor(R.color.very_light_grey));
        circlePageIndicator.setStrokeColor(getResources().getColor(R.color.transparent));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCircularBarPager.getCircularBar().animateProgress(0, 75, 1000);
    }
}
