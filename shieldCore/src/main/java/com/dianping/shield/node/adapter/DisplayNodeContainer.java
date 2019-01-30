package com.dianping.shield.node.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.shield.node.cellnode.ShieldDisplayNode;

/**
 * Created by runqi.wei at 2018/6/20
 */
public class DisplayNodeContainer extends ViewGroup {

    private View subView;

    private ShieldDisplayNode node;

    public DisplayNodeContainer(@NonNull Context context) {
        super(context);
        init();
    }

    public DisplayNodeContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DisplayNodeContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DisplayNodeContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
    }

    public View getSubView() {
        return subView;
    }

    public void setSubView(View subView) {
        removeAllViews();
        this.subView = subView;
        if (this.subView != null && this.subView.getParent() != this) {
            if (this.subView.getParent() instanceof ViewGroup) {
                ((ViewGroup) this.subView.getParent()).removeView(this.subView);
            }
            addView(this.subView);
            if (this.subView.hasFocus() || this.subView.hasWindowFocus()) {
                this.subView.requestFocus();
            }
        }
    }

    public ShieldDisplayNode getNode() {
        return node;
    }

    public void setNode(ShieldDisplayNode node) {
        this.node = node;
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof RecyclerView.LayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RecyclerView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new RecyclerView.LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (subView != null && subView.getParent() == this) {
            LayoutParams subLp = subView.getLayoutParams();
            int left = 0;
            int top = 0;
            int right = r - l;
            int bottom = b - t;
            if (subLp instanceof MarginLayoutParams) {
                MarginLayoutParams mlp = (MarginLayoutParams) subLp;
                left += mlp.leftMargin;
                top += mlp.topMargin;
                right = right - mlp.rightMargin;
                bottom = bottom - mlp.bottomMargin;
            }
            subView.layout(0, 0, Math.min(right, left + subView.getMeasuredWidth()), Math.min(bottom, top + subView.getMeasuredHeight()));
//            subView.layout(0, 0, r, b);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (subView != null) {

            if (subView.getParent() != this) {

                int maxWidth = Math.max(subView.getMeasuredWidth(), getSuggestedMinimumWidth());
                int maxHeight = Math.max(subView.getMeasuredHeight(), getSuggestedMinimumHeight());

                int childState = 0;
                childState = combineMeasuredStates(childState, subView.getMeasuredState());
                setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                        resolveSizeAndState(maxHeight, heightMeasureSpec,
                                childState << MEASURED_HEIGHT_STATE_SHIFT));
                return;
            }

            ViewGroup.LayoutParams originalLp = subView.getLayoutParams();
            LayoutParams customLp;
            if (originalLp == null) {
                customLp = generateDefaultLayoutParams();
            } else {
                customLp = generateLayoutParams(originalLp);
            }
            subView.setLayoutParams(customLp);

            final boolean measureMatchParentChildren =
                    MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                            MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

            int maxHeight = 0;
            int maxWidth = 0;
            int childState = 0;

            boolean isMatchParent = false;

            if (subView.getVisibility() != GONE) {
                if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED
                        && customLp.height == RecyclerView.LayoutParams.MATCH_PARENT
                        && getParent() != null) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(((ViewGroup) getParent()).getMeasuredHeight(), MeasureSpec.EXACTLY);
                }
                measureChildWithMargins(subView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final ViewGroup.LayoutParams lp = subView.getLayoutParams();
                int leftMargin = 0;
                int topMargin = 0;
                int rightMargin = 0;
                int bottomMargin = 0;
                if (lp instanceof MarginLayoutParams) {
                    leftMargin = ((MarginLayoutParams) lp).leftMargin;
                    topMargin = ((MarginLayoutParams) lp).topMargin;
                    rightMargin = ((MarginLayoutParams) lp).rightMargin;
                    bottomMargin = ((MarginLayoutParams) lp).bottomMargin;
                }
                maxWidth = Math.max(maxWidth,
                        subView.getMeasuredWidth() + leftMargin + rightMargin);
                maxHeight = Math.max(maxHeight,
                        subView.getMeasuredHeight() + topMargin + bottomMargin);
                childState = combineMeasuredStates(childState, subView.getMeasuredState());
                if (measureMatchParentChildren) {
                    isMatchParent = lp.width == ViewGroup.LayoutParams.MATCH_PARENT ||
                            lp.height == ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }

            // Check against our minimum height and width
            maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
            maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

            setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                    resolveSizeAndState(maxHeight, heightMeasureSpec,
                            childState << MEASURED_HEIGHT_STATE_SHIFT));

            if (isMatchParent) {
                final MarginLayoutParams lp = (MarginLayoutParams) subView.getLayoutParams();

                final int childWidthMeasureSpec;
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    final int width = Math.max(0, getMeasuredWidth()
                            - lp.leftMargin - lp.rightMargin);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            lp.leftMargin + lp.rightMargin,
                            lp.width);
                }

                final int childHeightMeasureSpec;
                if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    final int height = Math.max(0, getMeasuredHeight()
                            - lp.topMargin - lp.bottomMargin);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            lp.topMargin + lp.bottomMargin,
                            lp.height);
                }

                subView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
            subView.setLayoutParams(originalLp);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
