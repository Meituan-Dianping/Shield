package com.dianping.shield.node.cellnode

/**
 * Created by zhi.he on 2018/7/24.
 */
interface RangeChangeObserver {
    fun onChanged(sender: RangeHolder)

    fun onItemRangeChanged(sender: RangeHolder, positionStart: Int, itemCount: Int)

    fun onItemRangeInserted(sender: RangeHolder, positionStart: Int, itemCount: Int)

    fun onItemRangeRemoved(sender: RangeHolder, positionStart: Int, itemCount: Int)

    fun onItemRangeMoved(sender: RangeHolder, fromPosition: Int, toPosition: Int)

    /*
    * 元素发生非等量替换
    * @param fromPosition 起始位置
    * @param oldItemCount 原来被替换的元素数量
    *  @param newItemCount 新的被替换的元素数量
    *  相当于对应一次 remove 和 insert
    * */
    fun onItemRangeReplaced(sender: RangeHolder, fromPosition: Int, newItemCount: Int, oldItemCount: Int)
}