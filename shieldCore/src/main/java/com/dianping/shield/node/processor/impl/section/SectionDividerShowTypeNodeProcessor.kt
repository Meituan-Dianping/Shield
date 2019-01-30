package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/7/2.
 * 处理LinkType转换
 */
class SectionDividerShowTypeNodeProcessor : SectionNodeProcessor() {
    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {
        shieldSection.sectionDividerShowType = sectionItem.dividerShowType
        return false
    }
}