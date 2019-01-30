package com.dianping.shield.node.cellnode;

import com.dianping.shield.entity.ScrollDirection;

/**
 * Created by runqi.wei at 2018/7/30
 */
public interface AttachStatusChangeListener<T> {
    void onAttachStatusChanged(int position, T data, AttachStatus oldStatus, AttachStatus attachStatus, ScrollDirection direction);
}
