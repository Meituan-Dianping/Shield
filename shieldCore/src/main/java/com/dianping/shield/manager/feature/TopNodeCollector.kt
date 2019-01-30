package com.dianping.shield.manager.feature

import android.util.SparseArray
import com.dianping.shield.manager.ShieldSectionManager
import com.dianping.shield.node.adapter.ShieldDisplayNodeAdapter
import com.dianping.shield.node.cellnode.*
import com.dianping.shield.node.useritem.TopInfo
import com.dianping.shield.node.useritem.TopState
import java.util.*

/**
 * Created by zhi.he on 2018/8/14.
 */
class TopNodeCollector(private val shieldDisplayNodeAdapter: ShieldDisplayNodeAdapter, private val sectionManager: ShieldSectionManager, private val looper: LoopCellGroupsCollector) : CellManagerFeatureInterface {
    private var topNodeSparseArray = SparseArray<ShieldDisplayNode?>()
    private var bottomNodeSparseArray = SparseArray<ShieldDisplayNode?>()

    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {
    }

    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        looper.addBeforeLoopAction {
            topNodeSparseArray.clear()
            bottomNodeSparseArray.clear()
        }
        looper.addIndexedPreloadRowAction { rowIndex, shieldRow ->
            shieldRow.topInfo?.let {
                shieldRow.getDisplayNodeAtPosition(0)?.let { node ->
                    val position = computeTopInfo(node, rowIndex, shieldRow.sectionParent?.cellParent)
                    topNodeSparseArray.put(position, node)
                }
            }
            shieldRow.bottomInfo?.let {
                shieldRow.getDisplayNodeAtPosition(0)?.let { node ->
                    val position = computeBottomInfo(node, rowIndex, shieldRow.sectionParent?.cellParent)
                    if (bottomNodeSparseArray.indexOfKey(position) < 0) {
                        bottomNodeSparseArray.put(position, node)
                    }
                }
            }
        }
        looper.addAfterLoopAction {
            shieldDisplayNodeAdapter.setTopList(topNodeSparseArray)
            shieldDisplayNodeAdapter.setBottomList(bottomNodeSparseArray)
            shieldDisplayNodeAdapter.updateTopBottomViews()
        }
    }

    private fun same(arr1: SparseArray<ShieldDisplayNode?>?, arr2: SparseArray<ShieldDisplayNode?>?, useEquals: Boolean): Boolean {
        if (arr1 == arr2) {
            return true
        }

        if (arr1 == null || arr2 == null) {
            return false
        }

        if (arr1.size() != arr2.size()) {
            return false
        }

        for (i in 0 until arr1.size()) {
            if (arr1.keyAt(i) != arr2.keyAt(i)) {
                return false
            }

            if (useEquals) {
                if (!equals(arr1.valueAt(i), arr2.valueAt(i))) {
                    return false
                }
            } else {
                if (arr1.valueAt(i) != arr2.valueAt(i)) {
                    return false
                }
            }
        }

        return true

    }

    fun equals(a: Any?, b: Any?): Boolean {
        return a == b || (a != null && a.equals(b))
    }

    private fun computeTopInfo(node: ShieldDisplayNode, rIdx: Int, viewcell: ShieldViewCell?): Int {
        var sectionStartPosition: Int = -1
        var rowStartPosition: Int = -1
        val topInfo = node.rowParent?.topInfo
        node.innerTopInfo = InnerTopInfo().apply {
            this.topInfo = topInfo
            sectionStartPosition = node.rowParent?.sectionParent?.let { sectionManager.getSectionStartPosition(it) }
                    ?: -1
            rowStartPosition = node.rowParent?.sectionParent?.rangeDispatcher?.getStartPosition(rIdx)
                    ?: -1
            startPos = when (topInfo?.startType) {
                TopInfo.StartType.SELF -> sectionStartPosition + rowStartPosition
                TopInfo.StartType.ALWAYS -> -1
                null -> -1
            }

            endPos = when (topInfo?.endType) {
                TopInfo.EndType.MODULE -> {
                    val tailSection = viewcell?.shieldSections?.getOrNull(viewcell.shieldSections?.lastIndex
                            ?: -1)
                    tailSection?.let {
                        sectionManager.getSectionStartPosition(it) + it.getRange() - 1
                    } ?: Int.MAX_VALUE

                }
                TopInfo.EndType.SECTION -> {
                    node.rowParent?.sectionParent?.let {
                        sectionManager.getSectionStartPosition(it) + it.getRange() - 1
                    } ?: Int.MAX_VALUE
                }
                TopInfo.EndType.CELL -> sectionStartPosition + rowStartPosition
                else -> Int.MAX_VALUE
            }
            offset = topInfo?.offset ?: 0

            this.needAutoOffset = topInfo?.needAutoOffset == true

            zPosition = topInfo?.zPosition ?: 0

            listener = topInfo?.onTopStateChangeListener?.let {
                InnerTopInfo.TopStateChangeListener { node, state ->
                    it.onTopStageChanged(node.path?.cellType,
                            node.path?.section ?: -1,
                            node.path?.row ?: -1,
                            state ?: TopState.NORMAL)
                }
            }

        }
        return sectionStartPosition + rowStartPosition
    }

    private fun computeBottomInfo(node: ShieldDisplayNode, rIdx: Int, viewcell: ShieldViewCell?): Int {
        var sectionStartPosition = node.rowParent?.sectionParent?.let { sectionManager.getSectionStartPosition(it) }
                ?: -1
        var rowStartPosition = node.rowParent?.sectionParent?.rangeDispatcher?.getStartPosition(rIdx)
                ?: -1
        node.innerBottomInfo = InnerBottomInfo()

        return sectionStartPosition + rowStartPosition
    }
}