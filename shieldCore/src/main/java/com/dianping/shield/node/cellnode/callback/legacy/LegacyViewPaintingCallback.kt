package com.dianping.shield.node.cellnode.callback.legacy

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback

/**
 * Created by zhi.he on 2018/6/28.
 * 加在ViewItem上的callback不需要考虑viewtype的revert
 */
class LegacyViewPaintingCallback(private var legacyInterface: SectionCellInterface) : ViewPaintingCallback {
    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        return legacyInterface.onCreateView(parent, viewType?.toIntOrNull() ?: -1)
    }

    override fun updateView(view: View, data: Any?, path: NodePath?) {
        if (path?.cellType == CellType.NORMAL) {
            legacyInterface.updateView(view, path.section, path.row, null)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LegacyViewPaintingCallback

        if (legacyInterface != other.legacyInterface) return false

        return true
    }

    override fun hashCode(): Int {
        return legacyInterface.hashCode()
    }


}