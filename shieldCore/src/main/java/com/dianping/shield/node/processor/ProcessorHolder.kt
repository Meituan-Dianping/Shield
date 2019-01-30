package com.dianping.shield.node.processor

import android.content.Context
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.manager.ShieldSectionManager
import com.dianping.shield.node.DividerThemePackage
import com.dianping.shield.node.processor.impl.cell.*
import com.dianping.shield.node.processor.impl.displaynode.*
import com.dianping.shield.node.processor.impl.divider.FooterGapProcessor
import com.dianping.shield.node.processor.impl.divider.HeaderGapProcessor
import com.dianping.shield.node.processor.impl.divider.RowDividerProcessor
import com.dianping.shield.node.processor.impl.divider.SectionDividerShowTypeProcessor
import com.dianping.shield.node.processor.impl.row.*
import com.dianping.shield.node.processor.impl.section.LinkTypeSectionNodeProcessor
import com.dianping.shield.node.processor.impl.section.NormalSectionNodeProcessor
import com.dianping.shield.node.processor.impl.section.SectionCellAppearanceProcessor
import com.dianping.shield.node.processor.impl.section.SectionDividerShowTypeNodeProcessor
import com.dianping.shield.node.processor.legacy.cell.*
import com.dianping.shield.node.processor.legacy.row.*
import com.dianping.shield.node.processor.legacy.section.*

/**
 * Created by zhi.he on 2018/7/12.
 */
class ProcessorHolder(val mContext: Context) : AbstractProcessorHolder() {
    //other holder
    var shieldSectionManager: ShieldSectionManager = ShieldSectionManager(this)

    var creator: LoadingAndLoadingMoreCreator? = null

    var dividerThemePackage = DividerThemePackage(mContext)

    var infoHolder: ExposeMoveStatusEventInfoHolder = ExposeMoveStatusEventInfoHolder()

    override fun initProcessor(key: Class<*>): Processor? {
        return when (key) {
        //divider
            RowDividerProcessor::class.java -> RowDividerProcessor(dividerThemePackage)
            HeaderGapProcessor::class.java -> HeaderGapProcessor(mContext, dividerThemePackage)
            FooterGapProcessor::class.java -> FooterGapProcessor(mContext, dividerThemePackage)
            SectionDividerShowTypeProcessor::class.java -> SectionDividerShowTypeProcessor()
        //node
            BaseDisplayNodeProcessor::class.java -> BaseDisplayNodeProcessor(mContext)
            ClickDisplayNodeProcessor::class.java -> ClickDisplayNodeProcessor()
            DisplayNodeHotZoneProcessor::class.java -> DisplayNodeHotZoneProcessor()
            DisplayNodeRowAppearanceProcessor::class.java -> DisplayNodeRowAppearanceProcessor()
            DisplayNodeExposeProcessor::class.java -> DisplayNodeExposeProcessor(infoHolder)
        //row
            BaseRowNodeProcessor::class.java -> BaseRowNodeProcessor(this)
            RowNodeExposeProcessor::class.java -> RowNodeExposeProcessor()
            RowNodeHotZoneProcessor::class.java -> RowNodeHotZoneProcessor()
            RowTopInfoProcessor::class.java -> RowTopInfoProcessor()
            RowBottomInfoProcessor::class.java -> RowBottomInfoProcessor()
            RowSectionAppearanceProcessor::class.java -> RowSectionAppearanceProcessor()
        //section
            LinkTypeSectionNodeProcessor::class.java -> LinkTypeSectionNodeProcessor()
            NormalSectionNodeProcessor::class.java -> NormalSectionNodeProcessor(this)
            SectionDividerShowTypeNodeProcessor::class.java -> SectionDividerShowTypeNodeProcessor()
        //cell
            CellStatusMoreNodeProcessor::class.java -> CellStatusMoreNodeProcessor(mContext, creator, this)
            CellStatusNodeProcessor::class.java -> CellStatusNodeProcessor(mContext, creator, this)
            NormalCellNodeProcessor::class.java -> NormalCellNodeProcessor(mContext, this)
            CellNodeExposeProcessor::class.java -> CellNodeExposeProcessor(mContext, infoHolder)
            CellMoveStatusNodeProcessor::class.java -> CellMoveStatusNodeProcessor(mContext)
        /*
        * legacy interface
        * **/
        //row
            DividerInfoInterfaceProcessor::class.java -> DividerInfoInterfaceProcessor()
            DividerInterfaceProcessor::class.java -> DividerInterfaceProcessor()
            NormalRowInterfaceProcessor::class.java -> NormalRowInterfaceProcessor()
            RowExposeProcessor::class.java -> RowExposeProcessor()
            HeaderRowExposeProcessor::class.java -> HeaderRowExposeProcessor()
            FooterRowExposeProcessor::class.java -> FooterRowExposeProcessor()
            SetTopInterfaceProcessor::class.java -> SetTopInterfaceProcessor()
            SetBottomInterfaceProcessor::class.java -> SetBottomInterfaceProcessor()
            HeaderSetTopInterfaceProcessor::class.java -> HeaderSetTopInterfaceProcessor()
            HeaderSetBottomInterfaceProcessor::class.java -> HeaderSetBottomInterfaceProcessor()
            FooterSetTopInterfaceProcessor::class.java -> FooterSetTopInterfaceProcessor()
            FooterSetBottomInterfaceProcessor::class.java -> FooterSetBottomInterfaceProcessor()

        //section
            DividerShowTypeInterfaceProcessor::class.java -> DividerShowTypeInterfaceProcessor()
            ExtraCellInterfaceProcessor::class.java -> ExtraCellInterfaceProcessor(this)
            LinkTypeIntefaceProcessor::class.java -> LinkTypeIntefaceProcessor(mContext)
            NormalSectionInterfaceProcessor::class.java -> NormalSectionInterfaceProcessor(this)
            SectionDividerInfoInterfaceProcessor::class.java -> SectionDividerInfoInterfaceProcessor()
            SectionCellAppearanceProcessor::class.java -> SectionCellAppearanceProcessor()
        //cell
            CellStatusInterfaceProcessor::class.java -> CellStatusInterfaceProcessor(creator)
            CellStatusMoreInterfaceProcessor::class.java -> CellStatusMoreInterfaceProcessor(creator)
            NormalCellInterfaceProcessor::class.java -> NormalCellInterfaceProcessor(this)
            CellExposeInterfaceProcessor::class.java -> CellExposeInterfaceProcessor()
            CellMoveStatusInterfaceProcessor::class.java -> CellMoveStatusInterfaceProcessor()
            else -> null
        }
    }


    //node processor chain
    val cellProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(CellStatusNodeProcessor::class.java)
                .addProcessor(NormalCellNodeProcessor::class.java)
                .addProcessor(CellStatusMoreNodeProcessor::class.java)
                .addProcessor(CellNodeExposeProcessor::class.java)
                .addProcessor(CellMoveStatusNodeProcessor::class.java)
    }

    val sectionProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(LinkTypeSectionNodeProcessor::class.java)    //处理LinkType
                .addProcessor(SectionDividerShowTypeNodeProcessor::class.java)    //处理DividerShowType
//            .addProcessor(HeaderSectionNodeProcessor::class.java)    //处理Header
                .addProcessor(NormalSectionNodeProcessor::class.java)    //处理Normal
//            .addProcessor(FooterSectionNodeProcessor::class.java)
                .addProcessor(SectionCellAppearanceProcessor::class.java)
    }

    val rowProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(RowTopInfoProcessor::class.java)
                .addProcessor(RowBottomInfoProcessor::class.java)
                .addProcessor(RowNodeHotZoneProcessor::class.java)
                .addProcessor(RowNodeExposeProcessor::class.java)
                .addProcessor(RowSectionAppearanceProcessor::class.java)
                .addProcessor(BaseRowNodeProcessor::class.java)
    }

    val nodeProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(BaseDisplayNodeProcessor::class.java)
                .addProcessor(ClickDisplayNodeProcessor::class.java)
                .addProcessor(DisplayNodeExposeProcessor::class.java)
                .addProcessor(DisplayNodeHotZoneProcessor::class.java)
                .addProcessor(DisplayNodeRowAppearanceProcessor::class.java)
    }

    val dividerProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(HeaderGapProcessor::class.java)
                .addProcessor(FooterGapProcessor::class.java)
                .addProcessor(SectionDividerShowTypeProcessor::class.java)
                .addProcessor(RowDividerProcessor::class.java)
    }


    //legacy interface processor chain

    val cellInterfaceProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(CellStatusInterfaceProcessor::class.java)
                .addProcessor(NormalCellInterfaceProcessor::class.java)
                .addProcessor(CellStatusMoreInterfaceProcessor::class.java)
                .addProcessor(CellExposeInterfaceProcessor::class.java)
                .addProcessor(CellMoveStatusInterfaceProcessor::class.java)
    }

    val sectionInterfaceProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(NormalSectionInterfaceProcessor::class.java)
                .addProcessor(ExtraCellInterfaceProcessor::class.java)
                .addProcessor(LinkTypeIntefaceProcessor::class.java)
                .addProcessor(DividerShowTypeInterfaceProcessor::class.java)
                .addProcessor(SectionDividerInfoInterfaceProcessor::class.java)
    }

    val rowInterfaceProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(NormalRowInterfaceProcessor::class.java)
                .addProcessor(DividerInterfaceProcessor::class.java)
                .addProcessor(DividerInfoInterfaceProcessor::class.java)
                .addProcessor(RowExposeProcessor::class.java)
                .addProcessor(SetTopInterfaceProcessor::class.java)
                .addProcessor(SetBottomInterfaceProcessor::class.java)
    }

    val headerInterfaceProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(DividerInfoInterfaceProcessor::class.java)
                .addProcessor(HeaderRowExposeProcessor::class.java)
                .addProcessor(HeaderSetTopInterfaceProcessor::class.java)
                .addProcessor(HeaderSetBottomInterfaceProcessor::class.java)

    }

    val footerInterfaceProcessorChain: ProcessorChain by lazy(LazyThreadSafetyMode.NONE) {
        ProcessorChain(this)
                .addProcessor(DividerInfoInterfaceProcessor::class.java)
                .addProcessor(FooterRowExposeProcessor::class.java)
                .addProcessor(FooterSetTopInterfaceProcessor::class.java)
                .addProcessor(FooterSetBottomInterfaceProcessor::class.java)
    }

}