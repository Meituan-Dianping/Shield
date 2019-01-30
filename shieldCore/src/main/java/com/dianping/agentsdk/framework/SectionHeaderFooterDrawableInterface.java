package com.dianping.agentsdk.framework;

import android.graphics.drawable.Drawable;

/**
 * Created by runqi.wei on 2017/12/21.
 */

public interface SectionHeaderFooterDrawableInterface {

    Drawable getHeaderDrawable(int section);

    Drawable getFooterDrawable(int section);

}
