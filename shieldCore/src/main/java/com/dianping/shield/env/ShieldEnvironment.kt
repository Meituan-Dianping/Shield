package com.dianping.shield.env

import com.dianping.shield.bridge.ShieldLogger

/**
 * Create By zhi.he at 2018/5/1
 */
object ShieldEnvironment {

    var isDebug: Boolean = false
    var pageWidth: Int = -1
    var pageHeight: Int = -1

    var shieldLogger: ShieldLogger = ShieldLogger()
        set(value) {
            field = value
            if (isDebug) {
                field.level = ShieldLogger.INFO
            } else {
                field.level = Int.MAX_VALUE
            }
        }

    fun init(isDebug: Boolean) {
        ShieldEnvironment.isDebug = isDebug
        if (isDebug) {
            ShieldEnvironment.shieldLogger.level = ShieldLogger.INFO
        } else {
            ShieldEnvironment.shieldLogger.level = Int.MAX_VALUE
        }
    }

    fun initPageFrame(pageWidth: Int, pageHeight: Int) {
        this.pageWidth = pageWidth
        this.pageHeight = pageHeight
    }
}