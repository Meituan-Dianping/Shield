package com.dianping.shield.node.processor.legacy

import android.view.View
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.agentsdk.framework.CellStatusMoreInterface
import com.dianping.shield.node.itemcallbacks.LoadingMoreViewPaintingListener

/**
 * Created by zhi.he on 2018/6/28.
 */
class LegacyLoadingMoreListener(private val sci: CellStatusMoreInterface) : LoadingMoreViewPaintingListener {
    override fun onBindViewCalled(view: View, data: Any?, status: CellStatus.LoadingMoreStatus) {
        sci.onBindView(status)
    }
}