package com.dianping.shield.node.cellnode.callback

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator

/**
 * Created by zhi.he on 2018/6/26.
 * 这个是用来生成default loading view的item上的callback
 * 所以不用考虑viewtype revert
 */
class LoadingPaintingCallback(var creator: LoadingAndLoadingMoreCreator?) : ViewPaintingCallback {

    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        //默认的loading节点不会添加viewtype前缀，所以不用反解
        return when (viewType) {
            NodeCreator.LOADING_TYPE -> creator?.let { it.loadingView() }
                    ?: NodeCreator.createDefaultView(context, "未设置默认LoadingView")
            NodeCreator.FAILED_TYPE -> creator?.let { it.loadingFailedView() }
                    ?: NodeCreator.createDefaultView(context, "未设置默认FailedView")
            NodeCreator.EMPTY_TYPE -> creator?.let { it.emptyView() }
                    ?: NodeCreator.createDefaultView(context, "未设置默认EmptyView")
            else -> NodeCreator.createDefaultView(context, "错误的LoadingView")
        }
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {

    }

}