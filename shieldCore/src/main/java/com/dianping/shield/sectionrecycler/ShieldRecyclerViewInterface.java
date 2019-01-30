package com.dianping.shield.sectionrecycler;

import android.view.View;

/**
 * Created by xianhe.dong on 2018/8/22.
 * email xianhe.dong@dianping.com
 * 适配含有headerview的情况 decoration获取到偏移回的recyclerview方法信息
 */

public interface ShieldRecyclerViewInterface {
    View getShieldChildAt(int index);
    int getShieldChildAdapterPosition(View child);
    int getShieldChildCount();
    int getShieldAdapterItemCount();
    int getHeaderCount();
}
