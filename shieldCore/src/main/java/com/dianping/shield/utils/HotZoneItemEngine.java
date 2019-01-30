package com.dianping.shield.utils;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellInfo;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.feature.HotZoneItemListener;
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface;
import com.dianping.shield.sectionrecycler.ShieldRecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by zhi.he on 2018/1/3.
 * 模块自身行级别的热区监控引擎
 */

public class HotZoneItemEngine {

    public HotZoneItemListener hotZoneItemListener;
    protected HotZoneYRange hotZoneYRange;
    protected MergeSectionDividerAdapter mergeRecyclerAdapter;
    protected PieceAdapter ownerAdapter;
    protected Cell cell;
    protected ArrayList<CellInfo> hotZoneCells = new ArrayList<>();
    protected ArrayList<CellInfo> targetCells = new ArrayList<>();

    protected HashMap<CellInfo, Integer> endCellMap = new HashMap();
    protected HashMap<CellInfo, Integer> startCellMap = new HashMap();

    public void setHotZoneItemListener(Cell cell, HotZoneItemListener hotZoneItemListener, MergeSectionDividerAdapter mergeRecyclerAdapter) {
        this.cell = cell;
        this.hotZoneItemListener = hotZoneItemListener;
        this.hotZoneYRange = hotZoneItemListener.defineHotZone();
        this.targetCells = hotZoneItemListener.targetCells();
        this.mergeRecyclerAdapter = mergeRecyclerAdapter;
    }

    public void scroll(ScrollDirection scrollDirection, RecyclerView recyclerView, MergeSectionDividerAdapter mergeRecyclerAdapter) {
        long startTime = System.currentTimeMillis();
        if (mergeRecyclerAdapter == null || scrollDirection == null
                || !(recyclerView.getLayoutManager() instanceof ShieldLayoutManagerInterface)
                || hotZoneItemListener == null) {
            return;
        }

        this.hotZoneYRange = hotZoneItemListener.defineHotZone();

        ShieldLayoutManagerInterface layoutManager = (ShieldLayoutManagerInterface) recyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition(false);
        int lastPostion = layoutManager.findLastVisibleItemPosition(false);

        ownerAdapter = (PieceAdapter) cell.recyclerViewAdapter;
        //计算目标Cell的最后一行
        if ((endCellMap.isEmpty() && startCellMap.isEmpty()) || scrollDirection == ScrollDirection.STATIC) {
            startCellMap.clear();
            endCellMap.clear();
            for (int z = 0; z < targetCells.size(); z++) {
                if (z == 0) {
                    int targetCellPosition = mergeRecyclerAdapter.getGlobalPosition(ownerAdapter, targetCells.get(z).section, targetCells.get(z).row);
                    if (targetCellPosition > 0) {
                        startCellMap.put(targetCells.get(z), targetCellPosition);
                    }
                }

                if (z < targetCells.size() - 1) {
                    int globalPosition = mergeRecyclerAdapter.getGlobalPosition(ownerAdapter, targetCells.get(z + 1).section, targetCells.get(z + 1).row);
                    if (globalPosition > 0) {
                        startCellMap.put(targetCells.get(z + 1), globalPosition);
                        endCellMap.put(targetCells.get(z), globalPosition - 1);
                    }
                } else if (z == targetCells.size() - 1) {
                    int lastSection = 0;
                    int lastRow = 0;
                    if (ownerAdapter.getSectionCount() > 0) {
                        lastSection = ownerAdapter.getSectionCount() - 1;
                        if (ownerAdapter.getRowCount(lastSection) > 0) {
                            lastRow = ownerAdapter.getRowCount(lastSection) - 1;
                        }
                    }
                    Pair<Integer, Integer> lastPair = ownerAdapter.getInnerPosition(lastSection, lastRow);
                    int globalPosition = mergeRecyclerAdapter.getGlobalPosition(ownerAdapter, lastPair.first, lastPair.second);
                    if (globalPosition > 0) {
                        endCellMap.put(targetCells.get(z), globalPosition);
                    }
                }
            }
        }

        Log.i("MixData", "Scroll ================ " + " & Direction end0::" + (System.currentTimeMillis() - startTime));

        //补快速滚动遗漏的移出热区事件
        ArrayList<CellInfo> tempArray = (ArrayList<CellInfo>) hotZoneCells.clone();

        for (CellInfo tempCell : tempArray) {
//            int globalPosition = mergeRecyclerAdapter.getGlobalPosition(ownerAdapter, tempCell.section, tempCell.row);
            //已经不在屏幕内
            if ((startCellMap.get(tempCell) != null && startCellMap.get(tempCell) > lastPostion)
                    || (endCellMap.get(tempCell) != null && endCellMap.get(tempCell) < firstPosition)) {
                hotZoneCells.remove(tempCell);
                Log.i("MixCellAgent", "Scroll " + " & 补:" + tempCell.section + "to:" + tempCell.row);
                hotZoneItemListener.scrollOut(tempCell, scrollDirection);
            }
        }
        Log.i("MixData", "Scroll ================ " + " & Direction end1::" + (System.currentTimeMillis() - startTime));

        ArrayList<Integer> targetPosition = new ArrayList<>();

        for (int i = firstPosition; i <= lastPostion; i++) {

            Pair<Integer, Integer> temSectionInfo = mergeRecyclerAdapter.getSectionIndex(i);
            if (temSectionInfo == null) {
                continue;
            }
            MergeSectionDividerAdapter.DetailSectionPositionInfo sectionInfo
                    = mergeRecyclerAdapter.getDetailSectionPositionInfo(temSectionInfo.first, temSectionInfo.second);
            if (sectionInfo == null) {
                continue;
            }

            //根据全局position找到对应的PieceAdapter
            PieceAdapter adapterOwner = mergeRecyclerAdapter.getPieceAdapter(sectionInfo.adapterIndex);

            //不是本模块不看
            if (adapterOwner == null || adapterOwner != ownerAdapter) {
                continue;
            }

            //未设置热区的不看
            if (hotZoneYRange == null) {
                continue;
            }
            View itemView = null;
            int viewIndex;
            if(recyclerView instanceof ShieldRecyclerViewInterface){
                viewIndex = i + ((ShieldRecyclerViewInterface)recyclerView).getHeaderCount();
            }else {
                viewIndex = i;
            }
            for (int index = 0; index < recyclerView.getChildCount(); index++) {
                if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(index)) == viewIndex) {
                    itemView = recyclerView.getChildAt(index);
                }
            }
            if (itemView == null) continue;

            Rect itemRect = new Rect();
            itemView.getHitRect(itemRect);

//            Pair<Integer, Integer> postionPair = adapterOwner.getInnerPosition(sectionInfo.adapterSectionIndex, sectionInfo.adapterSectionPosition);
//            CellType cellType = adapterOwner.getCellType(sectionInfo.adapterSectionIndex, sectionInfo.adapterSectionPosition);
//            CellInfo cellInfo = new CellInfo(postionPair.first, postionPair.second, cellType);

            if (itemRect.top <= hotZoneYRange.endY
                    && itemRect.bottom > hotZoneYRange.startY) {

                targetPosition.add(i);
                //往上top边缘进入热区
                //判断边缘确定是否在热区内
                //并且之前没有在热区内

                Log.i("MixData", "Scroll ================ " + " & Direction end loop1::" + (System.currentTimeMillis() - startTime));

                Log.i("MixData", "Scroll ================ " + " & Direction end loop2::" + (System.currentTimeMillis() - startTime));

            }

            if (itemRect.top > hotZoneYRange.endY) {
                break;
            }
        }

        ArrayList<CellInfo> temp2Array = new ArrayList<>();
        for (int i : targetPosition) {
            for (int j = 0; j < targetCells.size(); j++) {
                CellInfo targetCell = targetCells.get(j);
                //找到所属cell
                if ((startCellMap.get(targetCell) != null && i >= startCellMap.get(targetCell)
                        && endCellMap.get(targetCell) != null && i <= endCellMap.get(targetCell))) {
                    if (!temp2Array.contains(targetCell)) {
                        temp2Array.add(targetCell);

                        if (!hotZoneCells.contains(targetCell)) {
                            hotZoneItemListener.scrollReach(targetCell, scrollDirection);
                        }
                    }
                    break;
                }
            }
        }

        hotZoneCells.removeAll(temp2Array);

        for (CellInfo cellInfo : hotZoneCells) {
            hotZoneItemListener.scrollOut(cellInfo, scrollDirection);
        }

        hotZoneCells = temp2Array;
//        Log.e("HotZone", "ScrollEnd:" + (System.currentTimeMillis() - time));
        Log.i("MixData", "Scroll ================ " + " & Direction end::" + (System.currentTimeMillis() - startTime));
    }
}
