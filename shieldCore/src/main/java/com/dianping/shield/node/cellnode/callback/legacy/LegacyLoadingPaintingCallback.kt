package com.dianping.shield.node.cellnode.callback.legacy

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.CellStatusInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator

/**
 * Created by zhi.he on 2018/6/27.
 */
class LegacyLoadingPaintingCallback(private val sci: CellStatusInterface, private val creator: LoadingAndLoadingMoreCreator?) : ViewPaintingCallback {

    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        val originViewType = NodeCreator.revertViewType(viewType)
        return when (originViewType) {
            NodeCreator.LOADING_TYPE_CUSTOM -> sci.loadingView() ?: creator?.loadingView()
            ?: NodeCreator.createDefaultView(context, "错误的LoadingView")
            NodeCreator.FAILED_TYPE_CUSTOM -> sci.loadingFailedView()
                    ?: creator?.loadingFailedView()
                    ?: NodeCreator.createDefaultView(context, "错误的LoadingView")
            NodeCreator.EMPTY_TYPE_CUSTOM -> sci.emptyView() ?: creator?.emptyView()
            ?: NodeCreator.createDefaultView(context, "错误的LoadingView")
            else -> NodeCreator.createDefaultView(context, "错误的LoadingView")
        }
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LegacyLoadingPaintingCallback

        if (sci != other.sci) return false

        return true
    }

    override fun hashCode(): Int {
        return sci.hashCode()
    }


}