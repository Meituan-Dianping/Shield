package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/11/23.
 */
abstract class AsyncProcessor {
    @JvmField
    var nextProcessor: AsyncProcessor? = null

    fun startProcessor(inputListener: OnAsyncProcessorFinishListener, vararg obj: Any?) {
        handleData(object : OnAsyncProcessorFinishListener {
            override fun onDataHandleComplete(result: Boolean) {
                if (!result) {
                    nextProcessor?.startProcessor(inputListener, *obj) ?: let {
                        inputListener.onDataHandleComplete(result)
                    }
                } else {
                    inputListener.onDataHandleComplete(result)
                }
            }
        }, *obj)
    }

    protected abstract fun handleData(listener: OnAsyncProcessorFinishListener, vararg obj: Any?)
}