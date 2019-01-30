package com.dianping.shield.node.cellnode.callback

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.shield.core.R
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/27.
 */
class ViewClickPaintingCallback(private val originCallback: ViewPaintingCallback, private var viewItem: ViewItem) : ViewPaintingCallback {
    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        var itemView = originCallback.onCreateView(context, parent, viewType)
        val originViewType = NodeCreator.revertViewType(viewType)
        if (originViewType == viewItem.viewType && viewItem.clickCallback != null && !itemView.hasOnClickListeners()) {
            val mListener = ShieldViewClickedListerner()
            itemView.setOnClickListener(mListener)
            itemView.setTag(R.id.item_click_tag_key_id, mListener)
        }
        return itemView
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        originCallback.updateView(view, data, path)
        if (data is ShieldDisplayNode) {
            val originViewType = NodeCreator.revertViewType(data.viewType)
            if (originViewType == viewItem.viewType && viewItem.clickCallback != null) {
                view.setTag(R.id.item_click_tag_data_id, data)
                val oldListener = view.getTag(R.id.item_click_tag_key_id)
                if (oldListener !is ShieldViewClickedListerner) {
                    val mListener = ShieldViewClickedListerner()
                    view.setOnClickListener(mListener)
                    view.setTag(R.id.item_click_tag_key_id, mListener)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ViewClickPaintingCallback

        if (originCallback != other.originCallback) return false
        if (viewItem != other.viewItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originCallback.hashCode()
        result = 31 * result + viewItem.hashCode()
        return result
    }

    private class ShieldViewClickedListerner : View.OnClickListener {
        override fun onClick(v: View?) {
            v?.let {
                var data = it.getTag(R.id.item_click_tag_data_id)
                if (data is ShieldDisplayNode) {
                    data.clickCallback?.let {
                        it.onViewClicked(v, data.data, data.path)
                    }
                }
            }
        }
    }


}