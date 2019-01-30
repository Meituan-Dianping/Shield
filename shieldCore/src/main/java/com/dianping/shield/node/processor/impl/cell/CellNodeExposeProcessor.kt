package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell
import com.dianping.shield.node.processor.ExposeMoveStatusEventInfoHolder
import com.dianping.shield.node.processor.ExposeMoveStatusEventListener
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by runqi.wei at 2018/8/1
 */
class CellNodeExposeProcessor(mContext: Context, private val infoHolder: ExposeMoveStatusEventInfoHolder) : CellNodeProcessor(mContext) {
    val handler: Handler by lazy(LazyThreadSafetyMode.NONE) {
        Handler(Looper.getMainLooper())
    }

    override fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean {
        shieldViewCell.apply {
            if (this.moveStatusEventListenerList == null) {
                this.moveStatusEventListenerList = ArrayList()
            }
            cellItem.exposeInfo?.forEach { exposeInfo ->
                this.moveStatusEventListenerList?.add(ExposeMoveStatusEventListener(infoHolder, exposeInfo, handler))
            }
        }
        return false
    }

}