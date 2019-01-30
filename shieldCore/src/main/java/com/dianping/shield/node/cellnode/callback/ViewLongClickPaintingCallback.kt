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
class ViewLongClickPaintingCallback(private val originCallback: ViewPaintingCallback, private var viewItem: ViewItem) : ViewPaintingCallback {
    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        var itemView = originCallback.onCreateView(context, parent, viewType)
        val originViewType = NodeCreator.revertViewType(viewType)
        if (originViewType == viewItem.viewType && viewItem.longClickCallback != null) {
            val mListener = ShieldViewClickedListerner()
            itemView.setOnLongClickListener(mListener)
            itemView.setTag(R.id.item_longclick_tag_key_id, mListener)
        }
        return itemView
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        originCallback.updateView(view, data, path)
        if (data is ShieldDisplayNode) {
            val originViewType = NodeCreator.revertViewType(data.viewType)
            if (originViewType == viewItem.viewType && viewItem.longClickCallback != null) {
                view.setTag(R.id.item_longclick_tag_data_id, data)
                val oldListener = view.getTag(R.id.item_longclick_tag_key_id)
                if (oldListener !is ShieldViewClickedListerner) {
                    val mListener = ShieldViewClickedListerner()
                    view.setOnLongClickListener(mListener)
                    view.setTag(R.id.item_longclick_tag_key_id, mListener)
                }
            }
        }
    }

    private class ShieldViewClickedListerner : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            v?.let {
                var data = it.getTag(R.id.item_longclick_tag_data_id)
                if (data is ShieldDisplayNode) {
                    data.longClickCallback?.let {
                        return it.onViewLongClicked(v, data.data, data.path)
                    }
                }
            }
            return false
        }
    }
}