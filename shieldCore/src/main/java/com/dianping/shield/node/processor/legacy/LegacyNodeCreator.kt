package com.dianping.shield.node.processor.legacy

import android.content.Context
import android.view.View
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/26.
 */
class LegacyNodeCreator {
    companion object {
        @JvmStatic
        fun createLoadingViewItem(context: Context, view: View): ViewItem {
            return ViewItem().apply {
                viewType = NodeCreator.LOADING_TYPE_CUSTOM

            }
        }
    }
}