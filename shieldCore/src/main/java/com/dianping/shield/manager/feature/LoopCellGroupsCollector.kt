package com.dianping.shield.manager.feature

import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell

/**
 * Created by zhi.he on 2018/8/22.
 */
class LoopCellGroupsCollector : CellManagerFeatureInterface {
    private val beforeActions: ArrayList<() -> Unit> = ArrayList()
    private val afterActions: ArrayList<() -> Unit> = ArrayList()
    private val cellAction: ArrayList<(Int, ShieldViewCell) -> Unit> = ArrayList()
    private val sectionAction: ArrayList<(Int, ShieldSection) -> Unit> = ArrayList()
    private val preloadRowActions: ArrayList<(Int, ShieldRow) -> Unit> = ArrayList()

    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {
    }

    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        beforeActions.forEach { it() }
        cellGroups.forEachIndexedViewCell { vcIndex, shieldViewCell ->
            cellAction.forEach { it(vcIndex, shieldViewCell) }
            shieldViewCell.forEachIndexedSection { sectionIndex, shieldSection ->
                sectionAction.forEach { it(sectionIndex, shieldSection) }
                shieldSection.forEachIndexedPreLoadRow { rowIndex, shieldRow ->
                    preloadRowActions.forEach { action ->
                        action(rowIndex, shieldRow)
                    }
                }
            }
        }
        afterActions.forEach { it() }
    }

    fun addBeforeLoopAction(action: () -> Unit) {
        beforeActions.add(action)
    }

    fun addAfterLoopAction(action: () -> Unit) {
        afterActions.add(action)
    }

    fun addIndexedViewCellAction(action: (Int, ShieldViewCell) -> Unit) {
        cellAction.add(action)
    }

    fun addIndexedSectionAction(action: (Int, ShieldSection) -> Unit) {
        sectionAction.add(action)
    }

    fun addIndexedPreloadRowAction(action: (Int, ShieldRow) -> Unit) {
        preloadRowActions.add(action)
    }

    private inline fun ArrayList<ShieldCellGroup?>.forEachIndexedViewCell(action: (Int, ShieldViewCell) -> Unit) {
        this.forEach { cellGroup ->
            cellGroup?.shieldViewCells?.forEachIndexed { index, viewcell ->
                action(index, viewcell)
            }
        }
    }


    private inline fun ShieldViewCell.forEachIndexedSection(action: (Int, ShieldSection) -> Unit) {
        this.shieldSections?.forEachIndexed { sectionIndex, shieldSection ->
            action(sectionIndex, shieldSection)
        }
    }

    private inline fun ShieldSection.forEachIndexedPreLoadRow(action: (Int, ShieldRow) -> Unit) {
        if (this.isLazyLoad) {
            this.shieldRows?.forEachIndexed { rowIndex, shieldRow ->

                val row: ShieldRow? = shieldRow ?: let {
                    //rowProvider不包含Header和Footer
                    val rowIndexOffset = if (this.hasHeaderCell) rowIndex - 1 else rowIndex
                    if (rowIndexOffset >= 0 && this.rowProvider?.isPreLoad(rowIndexOffset, this) == true) {
                        this.getShieldRow(rowIndex)
                    } else null
                }
                row?.let {
                    action(rowIndex, it)
                }
            }
        } else {
            this.shieldRows?.forEachIndexed { index, shieldRow ->
                shieldRow?.let {
                    action(index, it)
                }
            }
        }
    }


}