package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/11/23.
 */
abstract class AbstractProcessorHolder {
    private var processorMap: HashMap<Class<*>, Processor> = HashMap()

    open fun addProcessor(key: Class<*>, processor: Processor) {
        processorMap[key] = processor
    }

    open fun getProcessor(key: Class<*>): Processor? {
        return processorMap[key] ?: let {
            val processor = initProcessor(key)
            processor?.let { addProcessor(key, processor) }
            processor
        }
    }

    abstract fun initProcessor(key: Class<*>): Processor?
}