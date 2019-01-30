package com.dianping.shield.node.cellnode

import com.dianping.shield.utils.ObservableArrayList
import com.dianping.shield.utils.ObservableList
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/7/19.
 */
class RangeDispatcher(preCallbacks: ArrayList<ObservableList.OnListChangedCallback<*>>? = null) : ObservableArrayList<RangeHolder>(), RangeHolder {
    override fun getRange(): Int {
        return totalRange
    }

    private val rangeChangedObservers = ArrayList<RangeChangeObserver>()
    override fun registerObserver(observer: RangeChangeObserver) {
        rangeChangedObservers.add(observer)
    }

    override fun unregisterObserver(observer: RangeChangeObserver) {
        rangeChangedObservers.remove(observer)
    }

    private val startPositionList = RangeRemoveableArrayList<Int>()
    private val childRangeObserver = ChildRangeObserver()
    //    private var lastRange = 0
    var totalRange = 0

    init {
        //这个observer用来完成startPositionList的维护和totalRange的计算
        preCallbacks?.let {
            it.forEach { preCallback ->
                addOnListChangedCallback(preCallback)
            }
        }
        addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<RangeHolder>>() {


            override fun onChanged(sender: ObservableArrayList<RangeHolder>) {

            }

            override fun onItemRangeChanged(sender: ObservableArrayList<RangeHolder>, positionStart: Int, itemCount: Int, oldItems: MutableList<Any?>?) {
                //调整 start 和 total
                adjustChildRange(positionStart)
                //分发section级别range更新
                val startGlobalPosition = getStartPosition(positionStart)
                if (startGlobalPosition >= 0) {
                    val positionPair = updateChildObserver(positionStart, itemCount, oldItems, false)

                    rangeChangedObservers.forEach {
                        it.onItemRangeChanged(this@RangeDispatcher, startGlobalPosition, positionPair.first)
                    }
                }
            }

            override fun onItemRangeInserted(sender: ObservableArrayList<RangeHolder>, positionStart: Int, itemCount: Int) {
                //插入section
                adjustChildRange(positionStart)
                /**
                 * 这里执行在startPosition调整之后，所以可以直接取
                 * */
                val startGlobalPosition = getStartPosition(positionStart)
                if (startGlobalPosition >= 0) {
                    val positionPair = updateChildObserver(positionStart, itemCount, null, false)

                    rangeChangedObservers.forEach {
                        it.onItemRangeInserted(this@RangeDispatcher, startGlobalPosition, positionPair.first)
                    }
                }
            }

            override fun onItemRangeMoved(sender: ObservableArrayList<RangeHolder>, fromPosition: Int, toPosition: Int, itemCount: Int) {
                //not support
            }


            override fun onItemRangeRemoved(sender: ObservableArrayList<RangeHolder>, positionStart: Int, itemCount: Int, oldItems: MutableList<Any?>?) {

                //删除涉及到可能内部的section Range 先删除了，导致oldItems取到的range也为0的情况
                //统一通过total range变化来确定 一次删除导致的range count

                /**
                 * 删除先取
                 * */
                val startGlobalPosition = getStartPosition(positionStart)

                val oldTotal = totalRange
                adjustChildRange(positionStart)

                val count = oldTotal - totalRange

                //olditem的observer还是要移除
                updateChildObserver(positionStart, itemCount, oldItems, true)
                if (startGlobalPosition >= 0) {
                    rangeChangedObservers.forEach {
                        it.onItemRangeRemoved(this@RangeDispatcher, startGlobalPosition, count)
                    }
                }
            }

            override fun onItemRangeReplaced(sender: ObservableArrayList<RangeHolder>, fromPosition: Int, newItemCount: Int, oldItemCount: Int, oldItems: MutableList<Any?>?) {
                adjustChildRange(fromPosition)

                val startGlobalPosition = getStartPosition(fromPosition)
                val positionPair = updateChildObserver(fromPosition, newItemCount, oldItems, false)
                if (startGlobalPosition >= 0) {
                    rangeChangedObservers.forEach {
                        it.onItemRangeReplaced(this@RangeDispatcher, startGlobalPosition, positionPair.first, positionPair.second)
                    }
                }

            }

        })
    }

    fun getInnerPosition(position: Int): RangeInfo? {
        if (position < 0 || position >= totalRange) return null

        for (i in 0 until lastIndex) {
            val nextStartPosition = startPositionList[i + 1]
            if (position < nextStartPosition) {
                val start = startPositionList[i]
                val obj = get(i)
                return RangeInfo().apply {
                    this.obj = obj
                    this.index = i
                    this.innerIndex = position - start
                }
            }
        }

        return if (position < totalRange) {
            val lastStart = startPositionList[lastIndex]
            val lastObj = get(lastIndex)

            RangeInfo().apply {
                this.obj = lastObj
                this.index = lastIndex
                this.innerIndex = position - lastStart
            }
        } else null
    }

    fun getStartPosition(index: Int): Int {
        return startPositionList.getOrNull(index) ?: -1
    }

    fun getStartPosition(range: RangeHolder): Int {
        val index = indexOf(range)
        return getStartPosition(index)
    }

    class RangeInfo {
        @JvmField
        var obj: RangeHolder? = null
        @JvmField
        var index: Int = -1
        @JvmField
        var innerIndex: Int = -1
    }

    private fun updateChildObserver(fromPosition: Int, itemCount: Int, oldItems: MutableList<Any?>?, isRemoveAction: Boolean): Pair<Int, Int> {
        var newRangeCount = 0
        var oldRangeCount = 0

        if (!isRemoveAction) {
            for (index in fromPosition until fromPosition + itemCount) {
                val childRange = get(index)
                newRangeCount += childRange?.getRange() ?: 0
                childRange.registerObserver(childRangeObserver)
            }
        }

        oldItems?.forEach {
            if (it is RangeHolder) {
                oldRangeCount += it.getRange()
                it.unregisterObserver(childRangeObserver)
            }
        }
        return Pair(newRangeCount, oldRangeCount)
    }

    //插入，删除，更新的统一逻辑
    private fun adjustChildRange(positionStart: Int) {
        if (positionStart > lastIndex) {
            //从尾部移除
            startPositionList.removeRange(positionStart, startPositionList.size)
            totalRange = if (positionStart > 0) {
                startPositionList[positionStart - 1] + get(positionStart - 1).getRange()
            } else 0
        } else {
            var tempTotalRange = if (positionStart > 0) {
                startPositionList[positionStart - 1] + get(positionStart - 1).getRange()
            } else 0

            for (index in positionStart until size) {
                val positionRange = get(index)
                val range = positionRange?.getRange() ?: 0
                if (index < startPositionList.size) {
                    startPositionList[index] = tempTotalRange
                } else {
                    startPositionList.add(tempTotalRange)
                }
                tempTotalRange += range
            }
            //新数组变小了要同时缩小start list
            if (startPositionList.size > size) {
                startPositionList.removeRange(size, startPositionList.size)
            }
            totalRange = tempTotalRange

        }
    }

    //这个observer用来完成Section内部变化到全局的分发
    inner class ChildRangeObserver : RangeChangeObserver {

        override fun onChanged(sender: RangeHolder) {
            adjustChildRange(indexOf(sender))
            rangeChangedObservers.forEach {
                it.onChanged(this@RangeDispatcher)
            }
        }

        override fun onItemRangeChanged(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            val senderStart = getStartPosition(sender) + positionStart
            adjustChildRange(indexOf(sender))
            if (senderStart >= 0) {
                rangeChangedObservers.forEach {
                    it.onItemRangeChanged(this@RangeDispatcher, senderStart, itemCount)
                }
            }
        }

        override fun onItemRangeInserted(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            val senderStart = getStartPosition(sender) + positionStart
            adjustChildRange(indexOf(sender))
            if (senderStart >= 0) {
                rangeChangedObservers.forEach {
                    it.onItemRangeInserted(this@RangeDispatcher, senderStart, itemCount)
                }
            }
        }

        override fun onItemRangeRemoved(sender: RangeHolder, positionStart: Int, itemCount: Int) {
            val senderStart = getStartPosition(sender) + positionStart
            val dispatcherStart = indexOf(sender)
            //当sender的child都移除了，移除sender空壳
            if (sender.getRange() == 0) {
                this@RangeDispatcher.removeAt(dispatcherStart)
            } else {
                adjustChildRange(dispatcherStart)
                if (senderStart >= 0) {
                    rangeChangedObservers.forEach {
                        it.onItemRangeRemoved(this@RangeDispatcher, senderStart, itemCount)
                    }
                }
            }
        }

        override fun onItemRangeMoved(sender: RangeHolder, fromPosition: Int, toPosition: Int) {
            //not support
        }

        override fun onItemRangeReplaced(sender: RangeHolder, fromPosition: Int, newItemCount: Int, oldItemCount: Int) {
            val senderStart = getStartPosition(sender) + fromPosition
            adjustChildRange(indexOf(sender))
            if (senderStart >= 0) {
                rangeChangedObservers.forEach {
                    it.onItemRangeReplaced(this@RangeDispatcher, senderStart, newItemCount, oldItemCount)
                }
            }
        }

    }

    override fun toString(): String {
        return "RangeDispatcher(totalRange=$totalRange)"
    }
}