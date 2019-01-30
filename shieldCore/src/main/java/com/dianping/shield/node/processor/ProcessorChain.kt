package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/7/12.
 */
class ProcessorChain(private val processorHolder: ProcessorHolder) : Processor() {
    private var processorList = ArrayList<Processor>()
    fun addProcessor(processor: Processor): ProcessorChain {
        processorList.add(processor)
        return this
    }

    fun addProcessor(processorClass: Class<*>): ProcessorChain {
        processorHolder.getProcessor(processorClass)?.let {
            processorList.add(it)
        }
        return this
    }

    override fun handleData(vararg obj: Any?): Boolean {
        if (processorList.isNotEmpty()) {
            var processorChain: Processor = processorList[0]
            for ((index, processor) in processorList.withIndex()) {
                if (index < processorList.size - 1) {
                    processor.nextProcessor = processorList[index + 1]
                }
            }
            processorChain.startProcessor(*obj)
        }
        return true
    }
}