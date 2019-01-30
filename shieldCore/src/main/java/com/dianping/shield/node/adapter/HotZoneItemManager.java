package com.dianping.shield.node.adapter;

import android.util.Log;

import com.dianping.agentsdk.framework.Cell;
import com.dianping.shield.entity.CellInfo;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.feature.HotZoneItemStatusInterface;
import com.dianping.shield.node.adapter.hotzone.CellHotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.HotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.ArrayList;

/**
 * Created by bingwei.zhou at 2018/11/16
 */
public class HotZoneItemManager extends HotZoneManager {

    protected HotZoneItemStatusInterface hotZoneItemStatusInterface;
    protected ArrayList<CellInfo> targetCellList;
    protected ArrayList<CellHotZoneInfo> cellLocationList = new ArrayList<>();
    protected ArrayList<CellHotZoneInfo> hotZoneCellList = new ArrayList<>();

    public HotZoneItemManager(int bottom, int top) {
        super(bottom, top);
    }

    @Override
    public HotZoneYRange getHotZoneYRange() {
        if (hotZoneItemStatusInterface != null) {
            return hotZoneItemStatusInterface.defineHotZone();
        } else {
            return null;
        }
    }

    public void setHotZoneItemStatusInterface(HotZoneItemStatusInterface hotZoneItemStatusInterface, Cell cell) {
        this.hotZoneItemStatusInterface = hotZoneItemStatusInterface;
        this.targetCellList = hotZoneItemStatusInterface.targetCells();
        int lastSection = 0;
        int lastRow = 0;
        if (cell.shieldViewCell.shieldSections.size() > 0) {
            lastSection = cell.shieldViewCell.shieldSections.size() - 1;
            if (cell.shieldViewCell.getEntry(lastSection).shieldRows.size() > 0) {
                lastRow = cell.shieldViewCell.getEntry(lastSection).shieldRows.size() - 1;
            }
        }
        this.targetCellList.add(getCellInfo(lastSection, lastRow));
    }

    private CellInfo getCellInfo(ShieldDisplayNode shieldDisplayNode) {
        int row = shieldDisplayNode.rowParent.currentRowIndex();
        int section = shieldDisplayNode.rowParent.sectionParent.currentSectionIndex();
        CellType cellType = null;
        if (row > 0) {
            cellType = CellType.NORMAL;
        } else if (row == -1) {
            cellType = CellType.HEADER;
        } else if (row == -2) {
            cellType = CellType.FOOTER;
        }
        return new CellInfo(section, row, cellType);
    }

    private CellInfo getCellInfo(int section, int row) {
        CellType cellType = null;
        if (row > 0) {
            cellType = CellType.NORMAL;
        } else if (row == -1) {
            cellType = CellType.HEADER;
        } else if (row == -2) {
            cellType = CellType.FOOTER;
        }
        return new CellInfo(section, row, cellType);
    }

    private boolean isHotZoneCell(ArrayList<CellInfo> targetCellList, CellInfo cellInfo) {
        if (targetCellList.size() == 0) {
            return false;
        }
        for (int i = 0; i < targetCellList.size() - 1; i++) {
            CellInfo startCellInfo = targetCellList.get(i);
            CellInfo endCellInfo = targetCellList.get(i);
            if (cellInfo.section >= startCellInfo.section && cellInfo.section <= endCellInfo.section
                    && cellInfo.row >= startCellInfo.row && cellInfo.row <= cellInfo.row) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateHotZoneLocation(ArrayList<HotZoneInfo> hotZoneInfoList, ScrollDirection scrollDirection) {
        if (hotZoneInfoList.size() == 0 || hotZoneItemStatusInterface == null) {
            return;
        }
        cellLocationList.clear();
        hotZoneCellList.clear();
        for (int i = 0; i < hotZoneInfoList.size(); i++) {
            HotZoneLocation hotZoneLocation = hotZoneInfoList.get(i).hotZoneLocation;
            ShieldDisplayNode shieldDisplayNode = hotZoneInfoList.get(i).shieldDisplayNode;
            if (shieldDisplayNode == null) {
                break;
            }
            CellInfo cellInfo = getCellInfo(shieldDisplayNode);
            CellHotZoneInfo cellHotZoneInfo = new CellHotZoneInfo(cellInfo, hotZoneLocation);
            if (isHotZoneCell(targetCellList, cellInfo)) {
                hotZoneCellList.add(cellHotZoneInfo);
            }
            cellLocationList.add(cellHotZoneInfo);
            Log.d("tab", "--------------前index" + "section" + cellHotZoneInfo.cellInfo.section + "row" + cellHotZoneInfo.cellInfo.row + "Location" + cellHotZoneInfo.hotZoneLocation);
        }

        if (targetCellList.size() > 0) {
            hotZoneItemStatusInterface.onHotZoneLocationChanged(hotZoneCellList, scrollDirection);
        } else {
            hotZoneItemStatusInterface.onHotZoneLocationChanged(cellLocationList, scrollDirection);
        }

    }
}
