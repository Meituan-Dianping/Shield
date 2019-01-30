package com.dianping.shield.bridge.feature;

import com.dianping.shield.feature.IFocusChildScrollWhenBack;
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface;

/**
 * Created by xianhe.dong on 2018/12/18.
 * email xianhe.dong@dianping.com
 * PageContainer 常用方法
 * 归集 RecyclerView Adapter LayoutManager的常用方法 以及部分个性方法
 */

public interface PageContainerCommonFunctionInterface extends
        ShieldLayoutManagerInterface,
        LayoutPositionFuctionInterface,
        ItemViewInterface, IFocusChildScrollWhenBack {
}
