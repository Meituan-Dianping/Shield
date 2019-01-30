package com.dianping.shield.node.cellnode.callback.legacy

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.SectionExtraCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback

/**
 * Created by zhi.he on 2018/6/28.
 */
//加在ViewItem上的callback不需要考虑viewtype的revert
class LegacyFooterPaintingCallback(private val sci: SectionExtraCellInterface) : ViewPaintingCallback {
    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        //加在ViewItem上的callback不需要考虑viewtype的revert
//        但是Interface的转换过程中会加前缀 "footercell${NodeCreator.viewTypeSepreator}${sci.getHeaderViewType(section)}"
//        val originViewType = NodeCreator.revertViewType(viewType)
        return sci.onCreateFooterView(parent, viewType?.toIntOrNull() ?: -1)
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        if (path?.cellType == CellType.FOOTER) {
            sci.updateFooterView(view, path.section, null)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LegacyFooterPaintingCallback

        if (sci != other.sci) return false

        return true
    }

    override fun hashCode(): Int {
        return sci.hashCode()
    }
}