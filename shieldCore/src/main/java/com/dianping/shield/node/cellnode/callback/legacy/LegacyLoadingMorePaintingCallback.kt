package com.dianping.shield.node.cellnode.callback.legacy

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.agentsdk.framework.CellStatusMoreInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator

/**
 * Created by zhi.he on 2018/6/27.
 */
//加在ViewItem上的callback不需要考虑viewtype的revert
class LegacyLoadingMorePaintingCallback(private val sci: CellStatusMoreInterface, private val creator: LoadingAndLoadingMoreCreator?) : ViewPaintingCallback {

    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        //加在ViewItem上的callback不需要考虑viewtype的revert
//        val originViewType = NodeCreator.revertViewType(viewType)
        return when (viewType) {
            NodeCreator.LOADING_MORE_TYPE_CUSTOM -> sci.loadingMoreView()
                    ?: creator?.loadingMoreView()
                    ?: NodeCreator.createDefaultView(context, "错误的LoadingMoreView")
            NodeCreator.LOADING_MORE_FAILED_TYPE_CUSTOM -> sci.loadingMoreFailedView()
                    ?: creator?.loadingMoreFailedView()
                    ?: NodeCreator.createDefaultView(context, "错误的LoadingMoreView")
            else -> NodeCreator.createDefaultView(context, "错误的LoadingMoreView")
        }
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        //直接读取SDN上的viewType需要revertType
        when (data as? String) {
            NodeCreator.LOADING_MORE_TYPE_CUSTOM -> {
                sci.onBindView(CellStatus.LoadingMoreStatus.LOADING)
            }
            NodeCreator.LOADING_MORE_FAILED_TYPE_CUSTOM -> {
                sci.onBindView(CellStatus.LoadingMoreStatus.FAILED)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LegacyLoadingMorePaintingCallback

        if (sci != other.sci) return false

        return true
    }

    override fun hashCode(): Int {
        return sci.hashCode()
    }

}