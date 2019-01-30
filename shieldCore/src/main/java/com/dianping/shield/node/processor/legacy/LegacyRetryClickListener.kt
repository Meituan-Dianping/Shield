package com.dianping.shield.node.processor.legacy

import android.view.View
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData

/**
 * Created by zhi.he on 2018/6/28.
 */
class LegacyRetryClickListener(private val sciClickListener: View.OnClickListener) : ViewClickCallbackWithData {
    override fun onViewClicked(view: View, data: Any?, path: NodePath?) {
        sciClickListener.onClick(view)
    }
}