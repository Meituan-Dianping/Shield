package com.dianping.shield.sectionrecycler.itemdecoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.dianping.shield.node.StaggeredGridThemePackage

/**
 * Created by zhi.he on 2018/5/17.
 */
class StaggeredGridSpaceDecoration : RecyclerView.ItemDecoration() {
    var staggeredGridThemePackage: StaggeredGridThemePackage? = StaggeredGridThemePackage()
    val NOT_DEFINED = -1

    var xGap: Int = NOT_DEFINED
    var yGap: Int = NOT_DEFINED
    var gapProvider: GapProvider? = null
    var leftMargin: Int = NOT_DEFINED
    var rightMargin: Int = NOT_DEFINED

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (parent?.layoutManager !is StaggeredGridLayoutManager) {
            return
        }

        var position: Int = parent?.getChildAdapterPosition(view) ?: 0
        var lp: StaggeredGridLayoutManager.LayoutParams = view?.layoutParams as StaggeredGridLayoutManager.LayoutParams
        if (position < 0 || lp.isFullSpan) {
            return
        }

        outRect?.apply {
            var spanIndex: Int = lp.spanIndex
            var layoutManager = parent?.layoutManager as StaggeredGridLayoutManager
            var spanCount = layoutManager.spanCount

            updateOffset(this, getGapParams(position), spanIndex, spanCount)
        }
    }

    private fun getGapParams(position: Int): GapParams {

        return if (gapProvider != null) {
            GapParams(
                    getValue(gapProvider?.getXGap(position), staggeredGridThemePackage?.defaultXStaggeredGridGap),
                    getValue(gapProvider?.getYGap(position), staggeredGridThemePackage?.defaultYStaggeredGridGap),
                    getValue(gapProvider?.getLeftMargin(position), staggeredGridThemePackage?.defaultStaggeredLeftMargin),
                    getValue(gapProvider?.getRightMargin(position), staggeredGridThemePackage?.defaultStaggeredRightMargin)
            )
        } else {
            GapParams(
                    getValue(xGap, staggeredGridThemePackage?.defaultYStaggeredGridGap),
                    getValue(yGap, staggeredGridThemePackage?.defaultYStaggeredGridGap),
                    getValue(leftMargin, staggeredGridThemePackage?.defaultStaggeredLeftMargin),
                    getValue(rightMargin, staggeredGridThemePackage?.defaultStaggeredRightMargin)
            )
        }
    }

    private fun getValue(value: Int?, defaultValue: Int?): Int {
        if (value == null || value < 0) {
            return defaultValue ?: 0
        }
        return value
    }

    private fun updateOffset(outRect: Rect, gapParams: GapParams, spanIndex: Int, spanCount: Int) {

        val halfXGap = gapParams.xGap / 2

        if (spanIndex == 0) {
            outRect.left = outRect.left + gapParams.leftMargin
        } else {
            outRect.left = outRect.left + halfXGap
        }

        if (spanIndex == spanCount - 1) {
            outRect.right = outRect.right + gapParams.rightMargin
        } else {
            outRect.right = outRect.right + halfXGap
        }

        outRect.bottom = outRect.bottom + gapParams.yGap
    }

    class GapParams(val xGap: Int, val yGap: Int, val leftMargin: Int, val rightMargin: Int)

    interface GapProvider {
        fun getXGap(position: Int): Int
        fun getYGap(position: Int): Int
        fun getLeftMargin(position: Int): Int
        fun getRightMargin(position: Int): Int
    }
}