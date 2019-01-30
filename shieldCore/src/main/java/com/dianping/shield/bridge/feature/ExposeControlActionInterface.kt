package com.dianping.shield.bridge.feature

import com.dianping.shield.entity.ExposeAction

/**
 * Created by zhi.he on 2018/12/10.
 * ExposeControlParams类只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * ExposeAction.startExpose()
 * ExposeAction.startExpose(1000L)
 * ExposeAction.resetAgentExpose(xxAgent)
 *
 * 请通过getFeature()方法获取该接口的实现
 *
 * 完整调用示例：
 *  getFeature().callExposeAction(ExposeAction.startExpose(1000L));
 */
interface ExposeControlActionInterface {
    fun callExposeAction(action: ExposeAction)
}