package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/11/23.
 */
abstract class AbstractAsyncProcessorHolder {
    private var processorMap: HashMap<Any, AsyncProcessor> = HashMap()

    open fun addProcessor(key: Any, processor: AsyncProcessor) {
        processorMap[key] = processor
    }

    open fun getProcessor(key: Any): AsyncProcessor? {
        return processorMap[key] ?: let {
            val processor = initProcessor(key)
            processor?.let { addProcessor(key, processor) }
            processor
        }
    }

    abstract fun initProcessor(key: Any): AsyncProcessor?
}