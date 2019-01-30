package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/11/23.
 */
class AsyncProcessorChain(private val processorHolder: AbstractAsyncProcessorHolder) : AsyncProcessor() {
    private var processorList = ArrayList<AsyncProcessor>()

    fun addProcessor(processorKey: Any): AsyncProcessorChain {
        processorHolder.getProcessor(processorKey)?.let {
            processorList.add(it)
        }
        return this
    }

    override fun handleData(listener: OnAsyncProcessorFinishListener, vararg obj: Any?) {
        if (processorList.isNotEmpty()) {
            var processorChain: AsyncProcessor = processorList[0]
            for ((index, processor) in processorList.withIndex()) {
                if (index < processorList.size - 1) {
                    processor.nextProcessor = processorList[index + 1]
                }
            }
            processorChain.startProcessor(listener, *obj)
        }
    }
}