package com.dianping.shield.node.adapter;

import android.util.SparseArray;

import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.adapter.hotzone.HotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.ArrayList;

/**
 * Created by bingwei.zhou at 2018/11/16
 */
public abstract class HotZoneManager extends ViewLocationChangeProcessor {
    private boolean needObserver;
    private NodeList nodeList;
    private boolean onlyObserverInHotZone = true;
    private boolean reverseRange = false;

    HotZoneManager(int bottom, int top) {
        super(bottom, top);
    }

    void setNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    public void isObserverLocationChanged(boolean needObserver) {
        this.needObserver = needObserver;
    }

    public void setReverseRange(boolean reverseRange) {
        this.reverseRange = reverseRange;
    }

    public void setOnlyObserverInHotZone(boolean onlyObserverInHotZone) {
        this.onlyObserverInHotZone = onlyObserverInHotZone;
    }

    @Override
    public void onViewLocationChanged(ScrollDirection scrollDirection) {
        if (!needObserver) {
            return;
        }
        if (getHotZoneYRange() != null) {
            if (reverseRange) {
                setHotZoneYRange(new HotZoneYRange(getHotZoneYRange().endY, POSITION_RV_BOTTOM));
            } else {
                setHotZoneYRange(new HotZoneYRange(getHotZoneYRange().startY, getHotZoneYRange().endY));
            }
            updateHotZoneState(firstLastPositionInfo.locationSparseArray, scrollDirection);
        }
    }

    private ShieldDisplayNode getDisplayNode(int position) {
        if (position >= 0 && nodeList != null && position < nodeList.size()) {
            return nodeList.getShieldDisplayNode(position);
        }
        return null;
    }

    private void updateHotZoneState(SparseArray<HotZoneLocation> locationSparseArray, ScrollDirection scrollDirection) {
        if (locationSparseArray.size() == 0) {
            return;
        }
        ArrayList<HotZoneInfo> hotZoneInfoList = new ArrayList<>();

        for (int i = 0; i < locationSparseArray.size(); i++) {
            int globalPosition = locationSparseArray.keyAt(i);
            HotZoneLocation hotZoneLocation = locationSparseArray.valueAt(i);
            ShieldDisplayNode shieldDisplayNode = getDisplayNode(globalPosition);
            if (shieldDisplayNode == null) {
                break;
            }
            if (onlyObserverInHotZone) {
                if (hotZoneLocation == HotZoneLocation.BT_BE || hotZoneLocation == HotZoneLocation.BT_BT
                        || hotZoneLocation == HotZoneLocation.US_BE || hotZoneLocation == HotZoneLocation.US_BT) {
                    hotZoneInfoList.add(new HotZoneInfo(shieldDisplayNode, hotZoneLocation));
                }
            } else {
                hotZoneInfoList.add(new HotZoneInfo(shieldDisplayNode, hotZoneLocation));
            }
        }
        updateHotZoneLocation(hotZoneInfoList, scrollDirection);
    }

    public abstract HotZoneYRange getHotZoneYRange();

    public abstract void updateHotZoneLocation(ArrayList<HotZoneInfo> hotZoneInfoList, ScrollDirection scrollDirection);
}
