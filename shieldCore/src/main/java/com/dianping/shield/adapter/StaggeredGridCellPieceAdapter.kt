package com.dianping.shield.adapter

import android.content.Context
import android.support.v7.widget.StaggeredGridLayoutManager
import com.dianping.agentsdk.adapter.WrapperPieceAdapter
import com.dianping.agentsdk.framework.DividerInfo
import com.dianping.agentsdk.framework.LinkType
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.StaggeredGridCellInfoInterface

class StaggeredGridCellPieceAdapter(
        context: Context,
        pieceAdapter: PieceAdapter,
        extraInterface: StaggeredGridCellInfoInterface?
) : WrapperPieceAdapter<StaggeredGridCellInfoInterface>(context, pieceAdapter, extraInterface) {

    var staggerGridLayoutManager: StaggeredGridLayoutManager? = null

    override fun onBindViewHolder(
            holder: MergeSectionDividerAdapter.BasicHolder?,
            section: Int,
            row: Int
    ) {
        super.onBindViewHolder(holder, section, row)

        if (extraInterface != null
                && (getCellType(section, row) == CellType.LOADING_MORE ||
                        (extraInterface.spanCount(section) > 1 && getCellType(section, row) == CellType.NORMAL))) {
            val layoutParams: StaggeredGridLayoutManager.LayoutParams =
                    StaggeredGridLayoutManager.LayoutParams(holder?.itemView?.layoutParams)
            layoutParams.isFullSpan = false
//            if (layoutParams.spanIndex > 0) {
//                layoutParams.leftMargin = extraInterface.yStaggeredGridGap(section)
//            }
//            layoutParams.bottomMargin = extraInterface.xStaggeredGridGap(section)
            holder?.itemView?.layoutParams = layoutParams
        } else {
            val layoutParams: StaggeredGridLayoutManager.LayoutParams =
                    StaggeredGridLayoutManager.LayoutParams(holder?.itemView?.layoutParams)
            layoutParams.isFullSpan = true
            holder?.itemView?.layoutParams = layoutParams
        }

    }

    override fun getPreviousLinkType(section: Int): LinkType.Previous? {
        if (extraInterface != null && extraInterface.spanCount(section) > 1) {
            return LinkType.Previous.LINK_TO_PREVIOUS
        }
        return super.getPreviousLinkType(section)
    }

    override fun getNextLinkType(section: Int): LinkType.Next? {
        if (extraInterface != null && extraInterface.spanCount(section) > 1) {
            return LinkType.Next.LINK_TO_NEXT
        }
        return super.getNextLinkType(section)
    }

    override fun showTopDivider(section: Int, row: Int): Boolean {
        if (extraInterface != null && extraInterface.spanCount(section) > 1) {
            return false
        }
        return super.showTopDivider(section, row)
    }

    override fun showBottomDivider(section: Int, row: Int): Boolean {
        if (extraInterface != null && extraInterface.spanCount(section) > 1) {
            return false
        }
        return super.showBottomDivider(section, row)
    }
}