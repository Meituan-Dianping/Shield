package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/7/2.
 * 处理LinkType转换
 */
class LinkTypeSectionNodeProcessor : SectionNodeProcessor() {
    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {
        shieldSection.previousLinkType = sectionItem.previousLinkType
        shieldSection.nextLinkType = sectionItem.nextLinkType
        shieldSection.sectionHeaderHeight = sectionItem.sectionHeaderGapHeight
        shieldSection.sectionFooterHeight = sectionItem.sectionFooterGapHeight
        shieldSection.sectionHeaderDrawable = sectionItem.sectionHeaderGapDrawable
        shieldSection.sectionFooterDrawable = sectionItem.sectionFooterGapDrawable
        return false
    }
}