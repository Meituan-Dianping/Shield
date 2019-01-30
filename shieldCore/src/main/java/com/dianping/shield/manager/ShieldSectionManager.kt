package com.dianping.shield.manager

import android.util.SparseArray
import com.dianping.shield.node.adapter.ListObserver
import com.dianping.shield.node.adapter.NodeList
import com.dianping.shield.node.cellnode.*
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/7/16.
 */
class ShieldSectionManager(private val holder: ProcessorHolder) : NodeList {
    //这个Observer用来完成SectionList变化导致的全局position变化时，对listObservables的更新通知
    private val sectionPositionTypeAdjustor: SectionPositionTypeAdjuster = SectionPositionTypeAdjuster()

    var sectionRangeDispatcher: RangeDispatcher = RangeDispatcher(arrayListOf(sectionPositionTypeAdjustor))

    var displayNodeList = RangeRemoveableArrayList<ShieldDisplayNode?>()

    private var listObservables = ArrayList<ListObserver>()

    private val totalRangeObserver = TotalRangeObserver()

    override fun size(): Int {
        return sectionRangeDispatcher.totalRange
    }

    override fun getShieldDisplayNode(position: Int): ShieldDisplayNode? {
        var node: ShieldDisplayNode? = null
        if (position < displayNodeList.size) {
            node = displayNodeList[position]
        }
        return if (node?.isUpdate == true) {
            node
        } else {
            val shieldSectionPositionPair = sectionRangeDispatcher.getInnerPosition(position)
            val displaySection = shieldSectionPositionPair?.obj as? ShieldSection
            val displayPosition = shieldSectionPositionPair?.innerIndex ?: 0
            val currentNode = displaySection?.getShieldDisplayNode(displayPosition)
            //兼容还没有notify就调用getShieldDisplayNode方法的情况
            if (displayNodeList.size != size()) {
                displayNodeList = RangeRemoveableArrayList(arrayOfNulls<ShieldDisplayNode>(size()).asList())
            }
            displayNodeList[position] = currentNode
            currentNode
        }
    }

    fun getSectionStartPosition(section: ShieldSection): Int {
        return sectionRangeDispatcher.getStartPosition(section)
    }

    fun initAllSections(sections: ArrayList<ShieldSection>) {

//        displayNodeList = ArrayList(arrayOfNulls<ShieldDisplayNode>(size()).asList())
        sectionRangeDispatcher.unregisterObserver(totalRangeObserver)
        sectionRangeDispatcher.clear()

        sectionRangeDispatcher.addAll(sections)
        //全局position变化监听
        sectionRangeDispatcher.registerObserver(totalRangeObserver)

        displayNodeList = RangeRemoveableArrayList(arrayOfNulls<ShieldDisplayNode>(size()).asList())
//        adjustFirstHeaderGapAndLastFooterGap()
        listObservables.forEach {
            it.onChanged()
        }
    }

//    fun updateSections(start: Int, end: Int, sections: ArrayList<ShieldSection>) {
//        //全局position变化监听
//        sectionRangeDispatcher.unregisterObserver(totalRangeObserver)
//        if (start >= 0 && end <= sectionRangeDispatcher.lastIndex) {
//            sectionRangeDispatcher.removeRange(start, end)
//            sectionRangeDispatcher.addAll(start, sections)
//        }
//        sectionRangeDispatcher.registerObserver(totalRangeObserver)
//    }

    override fun registerObserver(observer: ListObserver) {
        listObservables.add(observer)
    }

    override fun unregisterObserver(observer: ListObserver) {
        listObservables.remove(observer)
    }

    override fun getTopNodeList(): SparseArray<ShieldDisplayNode>? {
        return null
    }

    override fun getHotZoneNodeList(): SparseArray<ShieldDisplayNode>? {
        return null
    }


    //这个observer用来完成Section内部变化到全局的分发
    inner class TotalRangeObserver : RangeChangeObserver {
        //非等量替换直接全量notify
        override fun onItemRangeReplaced(sender: RangeHolder, fromPosition: Int, newItemCount: Int, oldItemCount: Int) {
            //删除对应缓存
            displayNodeList.removeRange(fromPosition, fromPosition + oldItemCount)
            //增加缓存容量
            displayNodeList.addAll(fromPosition, arrayOfNulls<ShieldDisplayNode>(newItemCount).asList())

            //全量notify
            listObservables.forEach {
                it.onChanged()
            }
        }

        override fun onChanged(sender: RangeHolder) {
            //清空缓存
            displayNodeList = RangeRemoveableArrayList(arrayOfNulls<ShieldDisplayNode>(size()).asList())
            listObservables.forEach {
                it.onChanged()
            }
        }

        //只是position内容发生变化，整体range没有变化
        override fun onItemRangeChanged(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            //失效缓存
            for (i in positionStart until positionStart + itemCount) {
                displayNodeList[i] = null
            }
            listObservables.forEach {
                it.onItemRangeChanged(positionStart, itemCount)
            }
        }

        override fun onItemRangeInserted(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            //增加缓存容量
            displayNodeList.addAll(positionStart, arrayOfNulls<ShieldDisplayNode>(itemCount).asList())
            listObservables.forEach {
                it.onItemRangeInserted(positionStart, itemCount)
            }

        }

        override fun onItemRangeRemoved(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            //删除对应缓存
            displayNodeList.removeRange(positionStart, positionStart + itemCount)
            listObservables.forEach {
                it.onItemRangeRemoved(positionStart, itemCount)
            }
        }

        override fun onItemRangeMoved(sender: RangeHolder, fromPosition: Int, toPosition: Int) {
            //not support
        }

    }
}