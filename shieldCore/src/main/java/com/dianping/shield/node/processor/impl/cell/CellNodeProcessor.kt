package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by zhi.he on 2018/6/25.
 */
abstract class CellNodeProcessor(protected val context: Context) : Processor() {
    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 3 && obj[0] is ShieldSectionCellItem && obj[1] is ShieldViewCell && obj[2] is ArrayList<*>) {
            return handleShieldViewCell(obj[0] as ShieldSectionCellItem, obj[1] as ShieldViewCell, obj[2] as ArrayList<ShieldSection>)
        }
        return false
    }

    protected abstract fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean
}