package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.adapter.TopPositionAdapter;
import com.dianping.shield.entity.CellType;

import java.util.ArrayList;

/**
 * Created by hezhi on 16/6/23.
 */
public class WrapperPieceAdapter<EI> extends PieceAdapter {

    protected EI extraInterface;
    protected PieceAdapter adapter;

    public WrapperPieceAdapter(@NonNull Context context,
                               @NonNull PieceAdapter adapter, EI extraInterface) {
        super(context);
        this.extraInterface = extraInterface;
        this.adapter = adapter;
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        adapter.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent, viewType);
    }

    public PieceAdapter getInnerAdapter() {
        return this.adapter;
    }

    @Override
    public int getSectionCount() {
        return adapter.getSectionCount();
    }

    @Override
    public int getRowCount(int sectionIndex) {
        return adapter.getRowCount(sectionIndex);
    }

    @Override
    public float getSectionHeaderHeight(int section) {
        return adapter.getSectionHeaderHeight(section);
    }

    @Override
    public Drawable getSectionHeaderDrawable(int section) {
//        if (!TextUtils.isEmpty(getSectionTitle(section))) {
//            return textToDrawable(getSectionTitle(section));
//        }
        return adapter.getSectionHeaderDrawable(section);
    }

//    public Drawable textToDrawable(String textStr) {
//        Bitmap bitmap = Bitmap.createBitmap(200, 250, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setTextSize(ViewUtils.sp2px(getContext(),14));
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setColor(Color.BLUE);
//        Paint.FontMetrics fm = paint.getFontMetrics();
//        canvas.drawText(textStr, 0, 145 + fm.top - fm.ascent, paint);
//        canvas.save();
//        Drawable drawableright = new BitmapDrawable(bitmap);
//        drawableright.setBounds(0, 0, drawableright.getMinimumWidth(),
//                drawableright.getMinimumHeight());
//        return drawableright;
//    }

    @Override
    public float getSectionFooterHeight(int section) {
        return adapter.getSectionFooterHeight(section);
    }

    @Override
    public Drawable getSectionFooterDrawable(int section) {
        return adapter.getSectionFooterDrawable(section);
    }

    @Override
    public String getSectionTitle(int section) {
        return adapter.getSectionTitle(section);
    }

    @Override
    public Drawable getTopDivider(int section, int row) {
        return adapter.getTopDivider(section, row);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        return adapter.getBottomDivider(section, row);
    }

    @Override
    public Rect topDividerOffset(int section, int row) {
        return adapter.topDividerOffset(section, row);
    }

    @Override
    public Rect bottomDividerOffset(int section, int row) {
        return adapter.bottomDividerOffset(section, row);
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        return adapter.getDividerInfo(section, row);
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        return adapter.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        return adapter.getNextLinkType(section);
    }

    @Override
    public long getItemId(int section, int row) {
        return adapter.getItemId(section, row);
    }

    @Override
    public int getItemViewType(int sectionIndex, int row) {
        return adapter.getItemViewType(sectionIndex, row);
    }

    @Override
    public int getInnerType(int wrappedType) {
        return adapter.getInnerType(wrappedType);
    }

    @Override
    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedRow) {
        return adapter.getInnerPosition(wrappedSection, wrappedRow);
    }

    @Override
    public boolean showTopDivider(int section, int row) {
        return adapter.showTopDivider(section, row);
    }

    @Override
    public boolean showBottomDivider(int section, int row) {
        return adapter.showBottomDivider(section, row);
    }

    @Override
    public String getMappingKey() {
        return adapter.getMappingKey();
    }

    @Override
    public void setMappingKey(String mappingKey) {
        adapter.setMappingKey(mappingKey);
    }

    @Override
    public AgentInterface getAgentInterface() {
        return adapter.getAgentInterface();
    }

    @Override
    public void setAgentInterface(AgentInterface agentInterface) {
        adapter.setAgentInterface(agentInterface);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return adapter.getSectionCellInterface();
    }

    @Override
    public void setSectionCellInterface(SectionCellInterface sectionCellInterface) {
        adapter.setSectionCellInterface(sectionCellInterface);
    }

    @Override
    public void setAddSpaceForDivider(boolean addSpaceForDivider) {
        adapter.setAddSpaceForDivider(addSpaceForDivider);
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int row) {
        return adapter.hasBottomDividerVerticalOffset(section, row);
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int row) {
        return adapter.hasTopDividerVerticalOffset(section, row);
    }

    @Override
    public CellType getCellType(int viewType) {
        return adapter.getCellType(viewType);
    }

    public CellType getCellType(int wrappedSection, int wrappedRow) {
        return adapter.getCellType(wrappedSection, wrappedRow);
    }

    public int getTotalItemCount() {
        return adapter.getTotalItemCount();
    }

    @Override
    public boolean isInnerSection(int wrappedSection) {
        return adapter.isInnerSection(wrappedSection);
    }

    @Override
    public void onAdapterChanged() {
        super.onAdapterChanged();
        getInnerAdapter().onAdapterChanged();
    }

    @Override
    public void onAdapterItemRangeChanged(int positionStart, int itemCount) {
        super.onAdapterItemRangeChanged(positionStart, itemCount);
        getInnerAdapter().onAdapterItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onAdapterItemRangeChanged(int positionStart, int itemCount, Object payload) {
        super.onAdapterItemRangeChanged(positionStart, itemCount, payload);
        getInnerAdapter().onAdapterItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void onAdapterItemRangeInserted(int positionStart, int itemCount) {
        super.onAdapterItemRangeInserted(positionStart, itemCount);
        getInnerAdapter().onAdapterItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onAdapterItemRangeRemoved(int positionStart, int itemCount) {
        super.onAdapterItemRangeRemoved(positionStart, itemCount);
        getInnerAdapter().onAdapterItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onAdapterItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onAdapterItemRangeMoved(fromPosition, toPosition, itemCount);
        getInnerAdapter().onAdapterItemRangeMoved(fromPosition, toPosition, itemCount);
    }

    @Override
    public ArrayList<TopPositionAdapter.TopInfo> getTopInfoList() {
        return getInnerAdapter().getTopInfoList();
    }
}
