package daniel.olivier.stoyan.circularbarpager;

import android.app.Activity;
import android.os.Bundle;

import daniel.olivier.stoyan.library.CircularBarPager;

/**
 * Created by stoyan on 12/9/14.
 */
public class MainActivity extends Activity {
    private CircularBarPager circularBarPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circularBarPager = (CircularBarPager)findViewById(R.id.circularBar1);
//        circularBarPager.setProgress(70);

    }

    @Override
    protected void onResume() {
        super.onResume();
        circularBarPager.animateProgress(0, 75, this);
    }
}
