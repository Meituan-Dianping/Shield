package com.dianping.shield.node.itemcallbacks

import android.view.View
import com.dianping.agentsdk.framework.CellStatus

/**
 * Created by zhi.he on 2018/6/28.
 */
interface LoadingMoreViewPaintingListener {
    fun onCreateViewCalled(createdView: View, status: CellStatus.LoadingMoreStatus) {}
    fun onBindViewCalled(view: View, data: Any?, status: CellStatus.LoadingMoreStatus)
}