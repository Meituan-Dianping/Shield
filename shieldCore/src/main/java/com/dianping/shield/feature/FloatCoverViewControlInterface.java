package com.dianping.shield.feature;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by hai on 2017/5/26.
 */

public interface FloatCoverViewControlInterface {
    void addViewFloatCoverView(View floatingView, RelativeLayout.LayoutParams lp);
    void removeFloatCoverView(View floatingView);
}
