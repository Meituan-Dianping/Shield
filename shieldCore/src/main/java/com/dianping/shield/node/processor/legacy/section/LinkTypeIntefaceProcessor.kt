package com.dianping.shield.node.processor.legacy.section

import android.content.Context
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionHeaderFooterDrawableInterface
import com.dianping.agentsdk.framework.SectionLinkCellInterface
import com.dianping.agentsdk.framework.ViewUtils
import com.dianping.shield.feature.SectionTitleInterface
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class LinkTypeIntefaceProcessor(private val mContext: Context) : SectionInterfaceProcessor() {
    override fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean {
        if (sci is SectionLinkCellInterface) {
            sectionItem.previousLinkType = sci.linkPrevious(section)
            sectionItem.nextLinkType = sci.linkNext(section)
            var headerHeight = sci.getSectionHeaderHeight(section)
            if (headerHeight >= 0) {
                sectionItem.sectionHeaderGapHeight = ViewUtils.px2dip(mContext, headerHeight)
            }
            var footerHeight = sci.getSectionFooterHeight(section)
            if (footerHeight >= 0) {
                sectionItem.sectionFooterGapHeight = ViewUtils.px2dip(mContext, footerHeight)
            }
        }

        if (sci is SectionHeaderFooterDrawableInterface) {
            sectionItem.sectionHeaderGapDrawable = sci.getHeaderDrawable(section)
            sectionItem.sectionFooterGapDrawable = sci.getFooterDrawable(section)
        }
        if (sci is SectionTitleInterface) {
            sectionItem.sectionTitle = sci.getSectionTitle(section)
        }
        return false
//        nextProcessor?.handleSectionItem(sci, sectionItem, section)
    }
}