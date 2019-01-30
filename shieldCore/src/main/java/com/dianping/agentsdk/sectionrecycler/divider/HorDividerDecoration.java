package com.dianping.agentsdk.sectionrecycler.divider;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dianping.shield.layoutmanager.CoveredYInterface;
import com.dianping.shield.sectionrecycler.ShieldRecyclerViewInterface;

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
    private boolean hasTopHeaderDivider = false;

    protected CoveredYInterface coveredYInterface;

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

    public void setCoveredYInterface(CoveredYInterface coveredYInterface) {
        this.coveredYInterface = coveredYInterface;
    }

    public void setBottomFooterDivider(boolean hasBottomFooterDivider) {
        this.hasBottomFooterDivider = hasBottomFooterDivider;
    }

    public void setTopHeaderDivider(boolean hasTopHeaderDivider) {
        this.hasTopHeaderDivider = hasTopHeaderDivider;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (dividerCreator == null || parent == null) {
            return;
        }

        int childPosition;

        if(parent instanceof ShieldRecyclerViewInterface){
            childPosition = ((ShieldRecyclerViewInterface) parent).getShieldChildAdapterPosition(view);
        }else {
            childPosition = parent.getChildAdapterPosition(view);
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
            int lastPosition;
            if(parent instanceof ShieldRecyclerViewInterface){
                lastPosition = ((ShieldRecyclerViewInterface) parent).getShieldAdapterItemCount() - 1;
            }else {
                lastPosition = parent.getAdapter().getItemCount() - 1;
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
        if (dividerCreator == null || parent == null) {
            return;
        }

        int coveredY = 0;
        if (coveredYInterface != null) {
            coveredY = coveredYInterface.getCoveredY();
        }

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition;
            if(parent instanceof ShieldRecyclerViewInterface){
                childPosition = ((ShieldRecyclerViewInterface) parent).getShieldChildAdapterPosition(child);
            }else {
                childPosition = parent.getChildAdapterPosition(child);
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            // draw header drawable in header gap
            Drawable headerDrawable = dividerCreator.getHeaderDrawable(childPosition);
            int headerHeight = (int) dividerCreator.getHeaderHeight(childPosition);
            if (headerDrawable != null && headerHeight > 0) {
                int headerDrawableHeight = headerDrawable.getIntrinsicHeight();
                if (headerDrawableHeight < 0) {
                    headerDrawableHeight = headerHeight;
                }
                int headerDrawableWidth = Math.max(0, headerDrawable.getIntrinsicWidth());
                int headerBottom = (int) child.getY();
                int headerTop = headerBottom - Math.min(headerDrawableHeight, headerHeight);
                int headerLeft = left;
                int headerRight = right;
                if (headerDrawableWidth > 0) {
                    headerRight = left + headerDrawableWidth;
                }

                headerTop = Math.max(headerTop, coveredY);
                if (headerTop <= headerBottom) {
                    Rect headerBounds = new Rect(headerLeft, headerTop, headerRight, headerBottom);
                    headerDrawable.setBounds(headerBounds);
                    headerDrawable.setAlpha((int) (255 * child.getAlpha()));
                    headerDrawable.draw(c);
                }
            }

            // draw footer drawable in header gap
            Drawable footerDrawable = dividerCreator.getFooterDrawable(childPosition);
            int footerHeight = (int) dividerCreator.getFooterHeight(childPosition);

            if (footerDrawable != null && footerHeight > 0) {
                int footerDrawableHeight = footerDrawable.getIntrinsicHeight();
                if (footerDrawableHeight < 0) {
                    footerDrawableHeight = footerHeight;
                }
                int footerTop = (int) (child.getBottom() + child.getTranslationY());
                int footerBottom = footerTop + Math.min(footerHeight, footerDrawableHeight);
                int footerLeft = left;
                int footerRight = right;

                footerTop = Math.max(footerTop, coveredY);
                if (footerTop <= footerBottom) {
                    Rect footerBounds = new Rect(footerLeft, footerTop, footerRight, footerBottom);
                    footerDrawable.setBounds(footerBounds);
                    footerDrawable.setAlpha((int) (255 * child.getAlpha()));
                    footerDrawable.draw(c);
                }
            }

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
                int topDividerHeight = topDivider.getIntrinsicHeight();
                if (topDividerHeight < 0) {
                    topDividerHeight = 1;
                }
                if (dividerCreator.hasTopDividerVerticalOffset(childPosition)) {
                    topTop = (int) child.getY() - params.topMargin - topDividerHeight;
                    topBottom = (int) child.getY() - params.topMargin;
                } else {
                    topTop = (int) child.getY() - params.topMargin;
                    topBottom = (int) child.getY() - params.topMargin + topDividerHeight;
                }

                Rect topBounds = new Rect(topleft, topTop, topRight, topBottom);
                Rect topBoundsPadding = dividerCreator.topDividerOffset(childPosition);
                Rect insetTopBounds = insetInside(topBounds, topBoundsPadding);
                insetTopBounds.top = Math.max(insetTopBounds.top, coveredY);
                if (insetTopBounds.top <= insetTopBounds.bottom) {
                    topDivider.setBounds(insetTopBounds);
                    topDivider.setAlpha((int) (255 * child.getAlpha()));
                    topDivider.draw(c);
                }
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
                int bottomDividerHeight = bottomDivider.getIntrinsicHeight();
                if (bottomDividerHeight < 0) {
                    bottomDividerHeight = 1;
                }
                if (dividerCreator.hasBottomDividerVerticalOffset(childPosition)) {
                    bottomTop = (int) child.getTranslationY() + child.getBottom() + params.bottomMargin;
                    bottomBottom = (int) child.getTranslationY() + child.getBottom() + params.bottomMargin + bottomDividerHeight;
                } else {
                    bottomTop = (int) child.getTranslationY() + child.getBottom() + params.bottomMargin - bottomDividerHeight;
                    bottomBottom = (int) child.getTranslationY() + child.getBottom() + params.bottomMargin;
                }

                Rect bottomBounds = new Rect(bottomleft, bottomTop, bottomRight, bottomBottom);
                Rect bottomBoundsPadding = dividerCreator.bottomDividerOffset(childPosition);
                Rect insetBottomBounds = insetInside(bottomBounds, bottomBoundsPadding);
                insetBottomBounds.top = Math.max(insetBottomBounds.top, coveredY);
                if (insetBottomBounds.top <= insetBottomBounds.bottom) {
                    bottomDivider.setBounds(insetBottomBounds);
                    bottomDivider.setAlpha((int) (255 * child.getAlpha()));
                    bottomDivider.draw(c);
                }
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
