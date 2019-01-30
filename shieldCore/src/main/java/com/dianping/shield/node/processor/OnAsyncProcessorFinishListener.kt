package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/11/23.
 */
interface OnAsyncProcessorFinishListener {
    fun onDataHandleComplete(result: Boolean)
}