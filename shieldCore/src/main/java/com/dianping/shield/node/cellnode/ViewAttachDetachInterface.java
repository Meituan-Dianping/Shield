package com.dianping.shield.node.cellnode;

import android.view.View;

/**
 * Created by runqi.wei at 2018/6/20
 */
public interface ViewAttachDetachInterface {

    void onViewAttachedToWindow(View view, int position, ShieldDisplayNode displayNode);

    void onViewDetachedFromWindow(View view, int position, ShieldDisplayNode displayNode);
}
