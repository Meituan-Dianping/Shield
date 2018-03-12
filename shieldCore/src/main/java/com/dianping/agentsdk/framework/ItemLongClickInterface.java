package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by hezhi on 16/8/3.
 */
public interface ItemLongClickInterface {

    public void setOnItemLongClickListener(OnItemLongClickListener listener);

    public OnItemLongClickListener getOnItemLongClickListener();

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int section, int row);
    }

}



