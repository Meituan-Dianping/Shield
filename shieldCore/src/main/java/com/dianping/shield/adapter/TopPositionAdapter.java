package com.dianping.shield.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.dianping.agentsdk.adapter.WrapperPieceAdapter;
import com.dianping.agentsdk.pagecontainer.SetTopParams;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.feature.ExtraCellTopInterface;
import com.dianping.shield.feature.ExtraCellTopParamsInterface;
import com.dianping.shield.feature.SetTopInterface;
import com.dianping.shield.feature.SetTopParamsInterface;
import com.dianping.shield.feature.TopPositionInterface;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/5/23
 */
public class TopPositionAdapter extends WrapperPieceAdapter<TopPositionInterface> implements TopInfoListProvider {

    protected SetTopInterface setTopInterface;
    protected SetTopParamsInterface setTopParamsInterface;
    protected ExtraCellTopInterface extraCellTopInterface;
    protected ExtraCellTopParamsInterface extraCellTopParamsInterface;
    protected ArrayList<TopInfo> topInfoArrayList = new ArrayList<>();

    public TopPositionAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, TopPositionInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setSetTopInterface(SetTopInterface setTopInterface) {
        this.setTopInterface = setTopInterface;
    }

    public void setSetTopParamsInterface(SetTopParamsInterface setTopParamsInterface) {
        this.setTopParamsInterface = setTopParamsInterface;
    }

    public void setExtraCellTopInterface(ExtraCellTopInterface extraCellTopInterface) {
        this.extraCellTopInterface = extraCellTopInterface;
    }

    public void setExtraCellTopParamsInterface(ExtraCellTopParamsInterface extraCellTopParamsInterface) {
        this.extraCellTopParamsInterface = extraCellTopParamsInterface;
    }

    public void updateTopInfo() {
        if (extraInterface != null) {
            updateTopInfoArrayList(new TopInfoCreator() {
                @Override
                public TopInfo getTopInfo(int section, int row) {
                    CellType cellType = getCellType(section, row);
                    Pair<Integer, Integer> pair = getInnerPosition(section, row);
                    TopPositionInterface.TopPositionInfo positionInfo = extraInterface.getTopPositionInfo(cellType, pair.first, pair.second);
                    if (positionInfo == null) {
                        return null;
                    }
                    return new TopInfo(section, row, positionInfo.zPosition, positionInfo.offset, positionInfo.startTop, positionInfo.stopTop, positionInfo.onTopStateChangeListener);
                }
            });
        } else if (setTopInterface != null || setTopParamsInterface != null || extraCellTopInterface != null || extraCellTopParamsInterface != null) {
            updateTopInfoArrayList(new TopInfoCreator() {
                @Override
                public TopInfo getTopInfo(int section, int row) {
                    int viewType = getItemViewType(section, row);
                    int innerType = getInnerType(viewType);
                    CellType cellType = getCellType(viewType);
                    boolean isTop = false;
                    int offset = 0;

                    if (cellType == CellType.NORMAL) {
                        if (setTopParamsInterface != null) {
                            isTop = setTopParamsInterface.isTopView(innerType);
                            SetTopParams params = setTopParamsInterface.getSetTopParams(innerType);
                            if (params != null) {
                                offset = params.marginTopHeight;
                            }
                        } else if (setTopInterface != null) {
                            isTop = setTopInterface.isTopView(innerType);
                        }
                    } else if (cellType == CellType.HEADER) {
                        if (extraCellTopParamsInterface != null) {
                            isTop = extraCellTopParamsInterface.isHeaderTopView(innerType);
                            SetTopParams params = extraCellTopParamsInterface.getHeaderSetTopParams(innerType);
                            if (params != null) {
                                offset = params.marginTopHeight;
                            }
                        } else if (extraCellTopInterface != null) {
                            isTop = extraCellTopInterface.isHeaderTopView(innerType);
                        }
                    } else if (cellType == CellType.FOOTER) {
                        if (extraCellTopParamsInterface != null) {
                            isTop = extraCellTopParamsInterface.isFooterTopView(innerType);
                            SetTopParams params = extraCellTopParamsInterface.getFooterSetTopParams(innerType);
                            if (params != null) {
                                offset = params.marginTopHeight;
                            }
                        } else if (extraCellTopInterface != null) {
                            isTop = extraCellTopInterface.isFooterTopView(innerType);
                        }
                    }

                    if (isTop) {
                        return new TopInfo(section, row, 0, offset, TopPositionInterface.AutoStartTop.SELF, TopPositionInterface.AutoStopTop.NONE, null);
                    }
                    return null;
                }
            });
        }

    }

    @Override
    public ArrayList<TopInfo> getTopInfoList() {
        updateTopInfo();
        return topInfoArrayList;
    }


    protected interface TopInfoCreator {

        TopInfo getTopInfo(int section, int row);
    }

    protected void updateTopInfoArrayList(TopInfoCreator creator) {
        topInfoArrayList.clear();
        for (int i = 0; i < getSectionCount(); i++) {
            int rowCount = getRowCount(i);
            for (int j = 0; j < rowCount; j++) {
                TopInfo info = creator.getTopInfo(i, j);
                if (info != null) {
                    topInfoArrayList.add(info);
                }
            }
        }
    }

    public static class TopInfo {

        public int section;
        public int row;
        public int zPosition;
        public int offset;
        public TopPositionInterface.AutoStartTop start;
        public TopPositionInterface.AutoStopTop end;
        public TopPositionInterface.OnTopStateChangeListener onTopStateChangeListener;
        public int sectionStart = -1;
        public int sectionEnd = -1;
        public int moduleStart = -1;
        public int moduleEnd = -1;

        public TopInfo(int section, int row, int zPosition, int offset, TopPositionInterface.AutoStartTop start, TopPositionInterface.AutoStopTop end, TopPositionInterface.OnTopStateChangeListener onTopStateChangeListener) {
            this.section = section;
            this.row = row;
            this.zPosition = zPosition;
            this.offset = offset;
            this.start = start;
            this.end = end;
            this.onTopStateChangeListener = onTopStateChangeListener;
        }
    }
}
