package com.dianping.shield.node.cellnode.callback.legacy

import com.dianping.agentsdk.framework.CellStatusInterface
import com.dianping.agentsdk.framework.CellStatusMoreInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionExtraCellInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator

/**
 * Created by zhi.he on 2018/7/15.
 */
class LegacyCallbackHolder(private val sci: SectionCellInterface, private val creator: LoadingAndLoadingMoreCreator?) {
    val legacyFooterPaintingCallback: LegacyFooterPaintingCallback? by lazy(LazyThreadSafetyMode.NONE) {
        if (sci is SectionExtraCellInterface) {
            LegacyFooterPaintingCallback(sci)
        } else null
    }

    val legacyHeaderPaintingCallback: LegacyHeaderPaintingCallback? by lazy(LazyThreadSafetyMode.NONE) {
        if (sci is SectionExtraCellInterface) {
            LegacyHeaderPaintingCallback(sci)
        } else null
    }
    val legacyLoadingMorePaintingCallback: LegacyLoadingMorePaintingCallback? by lazy(LazyThreadSafetyMode.NONE) {
        if (sci is CellStatusMoreInterface) {
            LegacyLoadingMorePaintingCallback(sci, creator)
        } else null
    }
    val legacyLoadingPaintingCallback: LegacyLoadingPaintingCallback? by lazy(LazyThreadSafetyMode.NONE) {
        if (sci is CellStatusInterface) {
            LegacyLoadingPaintingCallback(sci, creator)
        } else null
    }
    val legacyViewPaintingCallback: LegacyViewPaintingCallback by lazy(LazyThreadSafetyMode.NONE) {
        LegacyViewPaintingCallback(sci)
    }

}