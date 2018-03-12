package com.dianping.agentsdk.sectionrecycler.divider;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * <p>
 * 为 {@link RecyclerView}绘制分隔线的 {@link RecyclerView.ItemDecoration}
 * 这个类只绘制每个 Item 上方和下方的两条分隔线。
 * </p>
 * <p>
 * 需要接口 {@link HorDividerCreator} 的实例提供具体的绘制分隔线所需要的信息
 * </p>
 * <p>
 * Created by runqi.wei
 * 14:06
 * 20.06.2016.
 */
public class HorDividerDecoration extends RecyclerView.ItemDecoration {

    protected HorDividerCreator dividerCreator;
    private boolean hasBottomFooterDivider = true;

    /**
     * <p><strong>Constractor</strong></p>
     * 构造方法，需要接口 {@link HorDividerCreator} 的实例提供具体的绘制分隔线所需要的信息
     *
     * @param dividerCreator 接口 {@link HorDividerCreator} 的一个实例，
     *                       该实例提供具体的绘制分隔线所需要的信息
     */
    public HorDividerDecoration(HorDividerCreator dividerCreator) {
        this.dividerCreator = dividerCreator;
    }

    public void setBottomFooterDivider(boolean hasBottomFooterDivider) {
        this.hasBottomFooterDivider = hasBottomFooterDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (dividerCreator == null) {
            return;
        }

        int childPosition = parent.getChildAdapterPosition(view);

        // 为 dianping 的 PullToRefreshRecyclerView 提供兼容，
        // 考虑到其中添加的一个 header view，调整相应的position
        RecyclerView.Adapter adapter = parent.getAdapter();
        if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
            childPosition--;
        }

        // 计算 Item 上方的间隔
        float headerHeight = dividerCreator.getHeaderHeight(childPosition);
        if (headerHeight > 0) {
            outRect.top += headerHeight;
        }

        // 计算 Item 下方的间隔
        float footerHeight = dividerCreator.getFooterHeight(childPosition);
        if (footerHeight > 0) {
            float bottomHeight = footerHeight;
            int lastPosition = parent.getAdapter().getItemCount() - 1;
            if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                lastPosition--;
            }
            if (parent.getChildCount() > 0 && lastPosition == childPosition && !hasBottomFooterDivider) {
                bottomHeight = 0;
            }
            outRect.bottom += bottomHeight;
        }

        // 计算 Item 上方为分隔线所留的间隔，
        // 根据 HorDividerDecoration#hasTopDividerVerticalOffset(int) 方法
        // 的返回值确定是否为上方分隔线留空间
        Drawable topDivider = dividerCreator.getTopDivider(childPosition);
        if (topDivider != null && dividerCreator.hasTopDividerVerticalOffset(childPosition)) {
            outRect.top += topDivider.getIntrinsicHeight();
        }

        // 计算 Item 下方为分隔线所留的间隔，
        // 根据 HorDividerDecoration#hasBottomDividerVerticalOffset(int) 方法
        // 的返回值确定是否为下方分隔线留空间
        Drawable bottomDivider = dividerCreator.getBottomDivider(childPosition);
        if (bottomDivider != null && dividerCreator.hasBottomDividerVerticalOffset(childPosition)) {
            outRect.bottom += bottomDivider.getIntrinsicHeight();
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (dividerCreator == null) {
            return;
        }

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            RecyclerView.Adapter adapter = parent.getAdapter();
            if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                childPosition--;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            // draw top divider
            Drawable topDivider = dividerCreator.getTopDivider(childPosition);
            if (topDivider != null) {
                int topleft = left;
                int topTop = 0;
                int topRight = right;
                int topBottom = 0;

                // 如果为分隔线留了空间，就将分隔线画在 Item 的 Margin 外，分隔线的底部紧挨 Item 的上 Margin
                // |---------------------|- Top Divider
                // |---------------------|- Margin Top
                // | ------------------- |- Item Top
                // | |       Item      | |
                // | ------------------- |- Item Bottom
                // |---------------------|- Margin Bottom
                //
                // 否则将分隔线画在 Item 的 Margin 内，分隔线的顶部紧贴 Item 的上 Margin,
                // 根据 Divider 的高度不同，可能会遮住 Item 的上部
                // |---------------------|- Margin Top
                // |---------------------|- Top Divider
                // | ------------------- |- Item Top
                // | |       Item      | |
                // | ------------------- |- Item Bottom
                // |---------------------|- Margin Bottom
                //
                if (dividerCreator.hasTopDividerVerticalOffset(childPosition)) {
                    topTop = child.getTop() - params.topMargin - topDivider.getIntrinsicHeight();
                    topBottom = child.getTop() - params.topMargin;
                } else {
                    topTop = child.getTop() - params.topMargin;
                    topBottom = child.getTop() - params.topMargin + topDivider.getIntrinsicHeight();
                }

                Rect topBounds = new Rect(topleft, topTop, topRight, topBottom);
                Rect topBoundsPadding = dividerCreator.topDividerOffset(childPosition);
                topDivider.setBounds(insetInside(topBounds, topBoundsPadding));
//                topDivider.setBounds(topleft, topTop, topRight, topBottom);
                topDivider.draw(c);
            }

            // draw bottom divider
            Drawable bottomDivider = dividerCreator.getBottomDivider(childPosition);
            if (bottomDivider != null) {
                int bottomleft = left;
                int bottomTop = 0;
                int bottomRight = right;
                int bottomBottom = 0;

                // 如果为分隔线留了空间，就将分隔线画在 Item 的 Margin 外，分隔线的顶部紧挨 Item 的下 Margin
                // |---------------------|  Margin Top
                // | ------------------- |-  Item Top
                // | |       Item      | |
                // | ------------------- |- Item Bottom
                // |---------------------|- Margin Bottom
                // |---------------------|- Bottom Divider
                //
                // 否则将分隔线画在 Item 的 Margin 内，分隔线的底部紧贴 Item 的下 Margin,
                // 根据 Divider 的高度不同，可能会遮住 Item 的下部
                // |---------------------|  Margin Top
                // | ------------------- |-  Item Top
                // | |       Item      | |
                // | ------------------- |- Item Bottom
                // |---------------------|- Bottom Divider
                // |---------------------|- Margin Bottom
                //
                if (dividerCreator.hasBottomDividerVerticalOffset(childPosition)) {
                    bottomTop = child.getBottom() + params.bottomMargin;
                    bottomBottom = child.getBottom() + params.bottomMargin + bottomDivider.getIntrinsicHeight();
                } else {
                    bottomTop = child.getBottom() + params.bottomMargin - bottomDivider.getIntrinsicHeight();
                    bottomBottom = child.getBottom() + params.bottomMargin;
                }

                Rect bottomBounds = new Rect(bottomleft, bottomTop, bottomRight, bottomBottom);
                Rect bottomBoundsPadding = dividerCreator.bottomDividerOffset(childPosition);
                bottomDivider.setBounds(insetInside(bottomBounds, bottomBoundsPadding));
                bottomDivider.draw(c);
            }

        }
    }

    protected Rect insetInside(Rect bounds, Rect padding) {
        if (bounds == null) {
            bounds = new Rect();
        }

        if (padding == null) {
            padding = new Rect();
        }

        return new Rect(bounds.left + padding.left,
                bounds.top + padding.top,
                bounds.right - padding.right,
                bounds.bottom - padding.bottom);
    }
}
