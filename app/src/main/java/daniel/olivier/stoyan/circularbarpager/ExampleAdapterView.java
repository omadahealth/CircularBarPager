package daniel.olivier.stoyan.circularbarpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by oliviergoutay on 12/8/14.
 */
public class ExampleAdapterView extends LinearLayout {
    /**
     * TAG for logging
     */
    private static final String TAG = "HomeUserView";

    public ExampleAdapterView(Context context) {
        super(context);

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout mainV = (LinearLayout) inflater.inflate(R.layout.view_user_info, this);

        //TODO init view
    }

}
