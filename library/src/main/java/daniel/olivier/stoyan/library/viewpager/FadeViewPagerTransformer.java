package daniel.olivier.stoyan.library.viewpager;

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
        if (position <= -1 || position >= 1) {
            view.setAlpha(0);
            view.setTranslationX(0);
        } else if (position < 0.0001 && position > -0.0001) {
            view.setAlpha(1);
            view.setTranslationX(1);
        } else if (position <= 0 || position <= 1) {
            //Get the page margin to calculate the alpha relatively to it
            float pageMargin = - (float) ((ViewPager) view.getParent()).getPageMargin() / (float) view.getWidth();
            float alpha = position / (1.0f - pageMargin);
            alpha = (alpha <= 0) ? alpha + 1 : 1 - alpha;
            view.setAlpha(alpha);
            //Reduce the translation by factor 2
            view.setTranslationX(-position * ((float) view.getWidth() / 2.0f));
        }
    }

}
