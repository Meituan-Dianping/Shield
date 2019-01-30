package com.dianping.shield.node.processor

/**
 * Created by zhi.he on 2018/7/12.
 */
abstract class Processor {
    @JvmField
    var nextProcessor: Processor? = null

    fun startProcessor(vararg obj: Any?) {
//        val startTime = System.nanoTime()
        val result = handleData(*obj)
//        val endTime = System.nanoTime()
//        ShieldEnvironment.shieldLogger.d("Processor", "${this.javaClass.simpleName} process cost time:${endTime - startTime}ns")
        if (!result) nextProcessor?.startProcessor(*obj)
    }

    protected abstract fun handleData(vararg obj: Any?): Boolean
}