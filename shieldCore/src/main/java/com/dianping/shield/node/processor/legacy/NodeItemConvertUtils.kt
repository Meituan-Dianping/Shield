package com.dianping.shield.node.processor.legacy

import android.content.Context
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class NodeItemConvertUtils {
    companion object {

        @JvmStatic
        fun convertInterfaceToItem(legacyInterface: SectionCellInterface, context: Context, holder: ProcessorHolder): ShieldSectionCellItem {
            val sectionCellItem = ShieldSectionCellItem()
            holder.cellInterfaceProcessorChain.startProcessor(legacyInterface, sectionCellItem)
            return sectionCellItem
        }
    }
}