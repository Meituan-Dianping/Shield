package com.dianping.agentsdk.sectionrecycler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dianping.shield.sectionrecycler.ShieldRecyclerViewInterface;

import java.util.ArrayList;


/**
 * Created by runqi.wei
 * 12:06
 * 09.01.2017.
 */

public class GroupBorderDecoration extends RecyclerView.ItemDecoration {

    private static final int LINE_WIDTH = 8;
    private static final int HALF_LINE_WIDTH = LINE_WIDTH / 2;
    private static final int CORNER_LINE_WIDTH = 12;
    private static final int HALF_CORNER_LINE_WIDTH = LINE_WIDTH / 2;
    private static final int CORNER_WIDTH = 30;

    private static final int TEXT_SIZE = 40;
    private static final int TEXT_MARGIN = 15;
    private static final int DOUBLE_TEXT_MARGIN = 2 * TEXT_MARGIN;

    private Paint linePaint;
    private Paint cornerPaint;
    private TextPaint textPaint;
    private Paint textBgPaint;

    private GroupInfoProvider groupInfoProvider;
    private ArrayList<String> textLineList = new ArrayList<>();
    private String textLine = "";
    private Rect bgRect = new Rect();


    public GroupBorderDecoration(GroupInfoProvider groupInfoProvider) {
        this.groupInfoProvider = groupInfoProvider;

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(LINE_WIDTH);

        cornerPaint = new Paint();
        cornerPaint.setColor(Color.BLUE);
        cornerPaint.setStrokeWidth(CORNER_LINE_WIDTH);

        textPaint = new TextPaint();
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setColor(Color.WHITE);

        textBgPaint = new Paint();
        textBgPaint.setColor(Color.DKGRAY);
        textBgPaint.setAlpha(180);

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (groupInfoProvider == null || parent == null) {
            return;
        }

        int start = 0;
        int end = 0;

        int childCount = parent.getChildCount();
        Log.v("rect start end", "start is " + start + " and end is " + end);
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int childPosition;
            if (parent instanceof ShieldRecyclerViewInterface) {
                childPosition = ((ShieldRecyclerViewInterface) parent).getShieldChildAdapterPosition(childView);
            } else {
                childPosition = parent.getChildAdapterPosition(childView);
            }
            drawBorderLine(c, childView, childPosition, parent, childPosition == 0, childPosition == parent.getAdapter().getItemCount() - 1);
        }
    }

    protected void drawBorderLine(Canvas c, View v, int viewPosition, RecyclerView parent, boolean isFirstInList, boolean isLastInList) {
        if (c == null || v == null || parent == null) {
            return;
        }

        Rect viewRect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        Rect canvasRect = c.getClipBounds();
        if (viewRect.left > canvasRect.left) {
            viewRect.left = canvasRect.left;
        }
        if (viewRect.right < canvasRect.right) {
            viewRect.right = canvasRect.right;
        }

        Rect drawRect = new Rect(viewRect.left + HALF_LINE_WIDTH,
                viewRect.top,
                viewRect.right - HALF_LINE_WIDTH,
                viewRect.bottom);

        if (isFirstInList) {
            drawRect.top += HALF_LINE_WIDTH;
        }

        if (isLastInList) {
            drawRect.bottom -= HALF_LINE_WIDTH;
        }

        c.save();

        // left
        c.drawLine(drawRect.left, drawRect.top, drawRect.left, drawRect.bottom, linePaint);

        // right
        c.drawLine(drawRect.right, drawRect.top, drawRect.right, drawRect.bottom, linePaint);

        if (isFirstInGroup(viewPosition, parent)) {
            // top
            c.drawLine(drawRect.left, drawRect.top, drawRect.right, drawRect.top, linePaint);

            // top left corner top
            c.drawLine(drawRect.left - HALF_LINE_WIDTH, drawRect.top, drawRect.left + CORNER_WIDTH, drawRect.top, cornerPaint);
            // top left corner left
            c.drawLine(drawRect.left, drawRect.top, drawRect.left, drawRect.top + CORNER_WIDTH, cornerPaint);
            // top right corner top
            c.drawLine(drawRect.right - CORNER_WIDTH, drawRect.top, drawRect.right + HALF_LINE_WIDTH, drawRect.top, cornerPaint);
            // top right corner right
            c.drawLine(drawRect.right, drawRect.top, drawRect.right, drawRect.top + CORNER_WIDTH, cornerPaint);

            // Text
            String text = groupInfoProvider.getGroupText(viewPosition);
            if (!TextUtils.isEmpty(text)) {

                Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
                textPaint.getFontMetrics(fontMetrics);

                // break text to multiple lines
                String textForBreak = text;
                textLineList.clear();
                while (true) {
                    int textMeasured = textPaint.breakText(textForBreak, 0, textForBreak.length() - 1, true, drawRect.width() - 2 * LINE_WIDTH - DOUBLE_TEXT_MARGIN, null);
                    if (textMeasured >= 0 && textMeasured < textForBreak.length() - 1) {
                        textLineList.add(textForBreak.substring(0, textMeasured));
                        textForBreak = textForBreak.substring(textMeasured);
                    } else {
                        textLineList.add(textForBreak);
                        break;
                    }
                }

                // calculate bg
                float textHeight = fontMetrics.bottom - fontMetrics.top;
                int lineHeight = (int) textHeight + DOUBLE_TEXT_MARGIN;
                bgRect.left = drawRect.left + HALF_LINE_WIDTH;
                bgRect.top = drawRect.top;
                bgRect.bottom = bgRect.top + lineHeight;
                bgRect.right = drawRect.right;

                // draw text lines
                for (int i = 0; i < textLineList.size(); i++) {
                    textLine = textLineList.get(i);
                    if (TextUtils.isEmpty(textLine)) {
                        continue;
                    }

                    c.drawRect(bgRect, textBgPaint);
                    c.drawText(textLine, 0, textLine.length(),
                            bgRect.left + TEXT_MARGIN,
                            bgRect.top + TEXT_MARGIN - fontMetrics.ascent,
                            textPaint);

                    bgRect.top += lineHeight;
                    bgRect.bottom += lineHeight;
                }

            }
        }


        if (isLastInGroup(viewPosition, parent)) {
            // bottom
            c.drawLine(drawRect.left, drawRect.bottom, drawRect.right, drawRect.bottom, linePaint);

            // bottom left corner bottom
            c.drawLine(drawRect.left - HALF_LINE_WIDTH, drawRect.bottom, drawRect.left + CORNER_WIDTH, drawRect.bottom, cornerPaint);
            // bottom left corner left
            c.drawLine(drawRect.left, drawRect.bottom - CORNER_WIDTH, drawRect.left, drawRect.bottom, cornerPaint);
            // bottom right corner bottom
            c.drawLine(drawRect.right, drawRect.bottom - CORNER_WIDTH, drawRect.right, drawRect.bottom + HALF_LINE_WIDTH, cornerPaint);
            // bottom right corner right
            c.drawLine(drawRect.right - CORNER_WIDTH, drawRect.bottom, drawRect.right, drawRect.bottom, cornerPaint);
        }

        c.restore();
    }

    private boolean isFirstInGroup(int position, RecyclerView parent) {
        if (position == 0) {
            return true;
        }

        int previousPosition = position - 1;

        if (groupInfoProvider != null) {
            int previousGroup = groupInfoProvider.getGroupPosition(previousPosition);
            int group = groupInfoProvider.getGroupPosition(position);
            if (group != GroupInfoProvider.NO_GROUP
                    && previousGroup != GroupInfoProvider.NO_GROUP
                    && group != previousGroup) {
                return true;
            }
        }

        return false;
    }

    private boolean isLastInGroup(int position, RecyclerView parent) {
        if (parent == null) {
            return false;
        }

        if (position == parent.getAdapter().getItemCount() - 1) {
            return true;
        }

        int nextPosition = position + 1;

        if (groupInfoProvider != null) {
            int nextGroup = groupInfoProvider.getGroupPosition(nextPosition);
            int group = groupInfoProvider.getGroupPosition(position);
            if (group != nextGroup) {
                return true;
            }
        }

        return false;
    }

    public interface GroupInfoProvider {
        int NO_GROUP = -1;

        int getGroupPosition(int position);

        String getGroupText(int position);
    }
}
