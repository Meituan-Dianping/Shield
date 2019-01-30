package com.dianping.shield.node.cellnode;

import com.dianping.shield.entity.ScrollDirection;

/**
 * Created by runqi.wei at 2018/7/9
 */
public interface MoveStatusEventListener<T> {

    void onAppeared(int position, T data, AppearanceEvent appearEvent, ScrollDirection direction);

    void onDisappeared(int position, T data, AppearanceEvent appearEvent, ScrollDirection direction);

    void reset(T data);
}
