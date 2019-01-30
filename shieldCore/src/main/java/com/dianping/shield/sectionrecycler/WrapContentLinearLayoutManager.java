package com.dianping.shield.sectionrecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.dianping.shield.layoutmanager.TopLinearLayoutManager;

/**
 * Created by zhi.he on 2017/6/21.
 */

public class WrapContentLinearLayoutManager extends TopLinearLayoutManager {
    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //... constructor
    /**
     * 之前是为了规避recycleView的bug，但会把业务的crash吃掉，所以从2019.1.18不再保护
     */
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        try {
//            super.onLayoutChildren(recycler, state);
//        } catch (IndexOutOfBoundsException e) {
//            Log.e("probe", "meet a IOOBE in RecyclerView");
//        }
//    }
}
