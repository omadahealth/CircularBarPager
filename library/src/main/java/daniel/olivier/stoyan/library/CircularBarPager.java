package daniel.olivier.stoyan.library;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;

import daniel.olivier.stoyan.library.viewpager.FadeViewPagerTransformer;
import stoyan.olivier.library.R;

/**
 * Created by oliviergoutay on 12/10/14.
 */
public class CircularBarPager extends RelativeLayout {

    private Context mContext;

    private CircularBar mCircularBar;

    private ViewPager mViewPager;

    private CirclePageIndicator mCirclePageIndicator;

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
     * Init the view by getting the {@link daniel.olivier.stoyan.library.CircularBar},
     * the {@link android.support.v4.view.ViewPager} and the {@link com.viewpagerindicator.CirclePageIndicator}.
     * Init also some default values as PageTranformer etc...
     */
    private void initializeView(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
     * on the {@link android.support.v4.view.ViewPager} inside the {@link daniel.olivier.stoyan.library.CircularBar}
     * TODO disable through styleable?
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int paddingForViewPager = this.getMeasuredWidth() / 6;
        mViewPager.setPadding(paddingForViewPager, 0, paddingForViewPager, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mViewPager.setPageMargin(-(mViewPager.getPaddingLeft() + mViewPager.getPaddingRight()));
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
}
