package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import com.dianping.shield.entity.ExposeScope
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.node.cellnode.AppearanceEvent
import com.dianping.shield.node.cellnode.MoveStatusEventListener
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by runqi.wei at 2018/11/14
 */
class CellMoveStatusNodeProcessor(context: Context) : CellNodeProcessor(context) {

    override fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean {
        shieldViewCell.apply {
            this.moveStatusCallback = cellItem?.moveStatusCallback
            if (this.moveStatusEventListenerList == null) {
                this.moveStatusEventListenerList = ArrayList()
            }
            shieldViewCell.moveStatusEventListenerList?.add(object : MoveStatusEventListener<ShieldViewCell> {
                override fun onAppeared(position: Int, data: ShieldViewCell?, appearEvent: AppearanceEvent?, direction: ScrollDirection?) {
                    when (appearEvent) {
                        AppearanceEvent.PARTLY_APPEAR -> data?.moveStatusCallback?.onAppear(ExposeScope.PX, direction
                                ?: ScrollDirection.STATIC, null)
                        AppearanceEvent.FULLY_APPEAR -> data?.moveStatusCallback?.onAppear(ExposeScope.COMPLETE, direction
                                ?: ScrollDirection.STATIC, null)
                    }

                }

                override fun onDisappeared(position: Int, data: ShieldViewCell?, appearEvent: AppearanceEvent?, direction: ScrollDirection?) {
                    when (appearEvent) {
                        AppearanceEvent.PARTLY_DISAPPEAR -> data?.moveStatusCallback?.onDisappear(ExposeScope.PX, direction
                                ?: ScrollDirection.STATIC, null)
                        AppearanceEvent.FULLY_DISAPPEAR -> data?.moveStatusCallback?.onDisappear(ExposeScope.COMPLETE, direction
                                ?: ScrollDirection.STATIC, null)
                    }
                }

                override fun reset(data: ShieldViewCell?) {
                }
            })
        }

        return false
    }
}