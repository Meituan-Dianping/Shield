package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by hezhi on 16/8/3.
 */
public interface ItemClickInterface {

    public void setOnItemClickListener(OnItemClickListener listener);

    public OnItemClickListener getOnItemClickListener();

    public interface OnItemClickListener {
        void onItemClick(View view, int section, int row);
    }

}



