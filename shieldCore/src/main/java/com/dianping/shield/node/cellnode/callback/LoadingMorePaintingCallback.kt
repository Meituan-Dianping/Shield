package com.dianping.shield.node.cellnode.callback

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.itemcallbacks.LoadingMoreViewPaintingListener
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator

/**
 * Created by zhi.he on 2018/6/27.
 * 这个是用来生成default loadingmore view的item上的callback
 * 所以不用考虑viewtype revert
 */
class LoadingMorePaintingCallback(var creator: LoadingAndLoadingMoreCreator?, var listener: LoadingMoreViewPaintingListener?) : ViewPaintingCallback {

    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        //默认的loadingmore节点不会添加viewtype前缀，所以不用反解
        return when (viewType) {
            NodeCreator.LOADING_MORE_TYPE -> {
                var view = creator?.let { it.loadingMoreView() }
                        ?: NodeCreator.createDefaultView(context, "未设置默认LoadingMoreView")
                listener?.onCreateViewCalled(view, CellStatus.LoadingMoreStatus.LOADING)
                view
            }
            NodeCreator.LOADING_MORE_FAILED_TYPE -> {
                var view = creator?.let { it.loadingMoreFailedView() }
                        ?: NodeCreator.createDefaultView(context, "未设置默认LoadingMoreFailedView")
                listener?.onCreateViewCalled(view, CellStatus.LoadingMoreStatus.FAILED)
                view
            }
            else -> NodeCreator.createDefaultView(context, "错误的LoadingMoreView")
        }
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        listener?.let {
            if (data is ShieldDisplayNode) {
                //直接读取SDN的viewType需要revert，但是因为默认的loadingmoreView 全局viewType一样，所以不做
                when (data.viewType) {
                    NodeCreator.LOADING_MORE_TYPE -> {
                        it.onBindViewCalled(view, data.data, CellStatus.LoadingMoreStatus.LOADING)
                    }
                    NodeCreator.LOADING_MORE_FAILED_TYPE -> {
                        it.onBindViewCalled(view, data.data, CellStatus.LoadingMoreStatus.FAILED)
                    }
                }
            }
        }
    }

}