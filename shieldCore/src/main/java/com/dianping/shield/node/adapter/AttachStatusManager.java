package com.dianping.shield.node.adapter;

import android.util.SparseArray;

import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.cellnode.AppearanceEvent;
import com.dianping.shield.node.cellnode.AttachStatus;
import com.dianping.shield.node.cellnode.AttachStatusChangeListener;
import com.dianping.shield.node.cellnode.MoveStatusEventListener;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by runqi.wei at 2018/7/9
 */
public class AttachStatusManager extends ViewLocationChangeProcessor {

    protected NodeList nodeList;

    public AttachStatusManager(int top, int bottom) {
        super(bottom, top);
    }

    //store only when Activity is Pauseing
    FirstLastPositionInfo adapterPositionInfo = null;

    public void storeCurrentInfo() {
        try {
            adapterPositionInfo = (FirstLastPositionInfo) firstLastPositionInfo.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCurrentInfo() {
        firstLastPositionInfo = adapterPositionInfo;
    }

    public void clearStoredPositionInfo() {
        adapterPositionInfo = null;
    }

    public void setNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public void onViewLocationChanged(ScrollDirection scrollDirection) {
        updateCurrentInScreenNode(getDisplayNodeList(firstLastPositionInfo.completelyVisibleItemPositions),
                getDisplayNodeList(firstLastPositionInfo.firstVisibleItemPositions),
                getDisplayNodeList(firstLastPositionInfo.lastVisibleItemPositions),
                scrollDirection);
    }

    public void clearCurrentInfo() {
        firstLastPositionInfo.clear();
    }

    public void clear() {
        statusHashMap.clear();
        positionHashMap.clear();
    }

    public AttachStatus getAttachStatus(ShieldDisplayNode displayNode) {
        return statusHashMap.get(displayNode);
    }

    private AttachStatus getStatus(HashMap<ShieldDisplayNode, AttachStatus> map, ShieldDisplayNode node) {
        AttachStatus status = map.get(node);
        if (status == null) {
            status = AttachStatus.DETACHED;
        }
        return status;
    }

    public ShieldDisplayNode getDisplayNode(int position) {
        if (position >= 0 && position < nodeList.size()) {
            return nodeList.getShieldDisplayNode(position);
        }
        return null;
    }

    public SparseArray<ShieldDisplayNode> getDisplayNodeList(List<Integer> positionList) {

        if (positionList == null || positionList.isEmpty()) {
            return null;
        }

        SparseArray<ShieldDisplayNode> list = new SparseArray<>(positionList.size());
        int count = positionList.size();
        for (int i = 0; i < count; i++) {
            int pos = positionList.get(i);
            if (pos < 0) {
                continue;
            }
            ShieldDisplayNode node = getDisplayNode(pos);
            if (node == null) {
                continue;
            }
            list.put(pos, node);
        }

        return list;
    }

    /**
     * 输入参数是有序的
     *
     * @param completeInScreenNodeList
     * @param firstNodeList
     * @param lastNodeList
     */
    public void updateCurrentInScreenNode(SparseArray<ShieldDisplayNode> completeInScreenNodeList,
                                          SparseArray<ShieldDisplayNode> firstNodeList,
                                          SparseArray<ShieldDisplayNode> lastNodeList,
                                          ScrollDirection scrollDirection) {

        if (completeInScreenNodeList == null) {
            completeInScreenNodeList = new SparseArray<>();
        }

        if (firstNodeList == null) {
            firstNodeList = new SparseArray<>();
        }

        if (lastNodeList == null) {
            lastNodeList = new SparseArray<>();
        }

        HashMap<ShieldDisplayNode, AttachStatus> oldStatusMap = statusHashMap;
        if (oldStatusMap == null) {
            oldStatusMap = new HashMap<>();
        }
        HashMap<ShieldDisplayNode, Integer> oldPositionMap = positionHashMap;
        if (oldPositionMap == null) {
            oldPositionMap = new HashMap<>();
        }

        statusHashMap = new HashMap<>();
        positionHashMap = new HashMap<>();

        SparseArray<AppearanceDispatchData> inScreenNodesArr = new SparseArray<>();
        SparseArray<AppearanceDispatchData> deleteNodesArr = new SparseArray<>();
        for (int i = 0; i < firstNodeList.size(); i++) {
            ShieldDisplayNode firstNode = firstNodeList.valueAt(i);
            if (completeInScreenNodeList.indexOfValue(firstNode) < 0) {
                int pos = firstNodeList.keyAt(i);
                AttachStatus oldStatus = getStatus(oldStatusMap, firstNode);
                oldPositionMap.remove(firstNode);
                oldStatusMap.remove(firstNode);
                setNodeStatus(firstNode, AttachStatus.PARTLY_ATTACHED);
                positionHashMap.put(firstNode, pos);
                inScreenNodesArr.put(pos, new AppearanceDispatchData(firstNode, oldStatus, AttachStatus.PARTLY_ATTACHED, scrollDirection));
            }
        }

        for (int i = 0; i < completeInScreenNodeList.size(); i++) {
            int pos = completeInScreenNodeList.keyAt(i);
            ShieldDisplayNode node = completeInScreenNodeList.valueAt(i);
            AttachStatus oldStatus = getStatus(oldStatusMap, node);
            oldPositionMap.remove(node);
            oldStatusMap.remove(node);
            positionHashMap.put(node, pos);
            setNodeStatus(node, AttachStatus.FULLY_ATTACHED);
            inScreenNodesArr.put(pos, new AppearanceDispatchData(node, oldStatus, AttachStatus.FULLY_ATTACHED, scrollDirection));
        }

        for (int i = 0; i < lastNodeList.size(); i++) {
            ShieldDisplayNode lastNode = lastNodeList.valueAt(i);
            if (completeInScreenNodeList.indexOfValue(lastNode) < 0) {
                int pos = lastNodeList.keyAt(i);
                AttachStatus oldStatus = getStatus(oldStatusMap, lastNode);
                oldPositionMap.remove(lastNode);
                oldStatusMap.remove(lastNode);
                positionHashMap.put(lastNode, pos);
                setNodeStatus(lastNode, AttachStatus.PARTLY_ATTACHED);
                inScreenNodesArr.put(pos, new AppearanceDispatchData(lastNode, oldStatus, AttachStatus.PARTLY_ATTACHED, scrollDirection));
            }
        }

        for (Map.Entry<ShieldDisplayNode, AttachStatus> entry : oldStatusMap.entrySet()) {
            ShieldDisplayNode oldNode = entry.getKey();
            AttachStatus oldStatus = entry.getValue();
            if (oldNode != null) {
                setNodeStatus(oldNode, AttachStatus.DETACHED);
                Integer oldPos = oldPositionMap.get(oldNode);
                if (oldPos == null) {
                    oldPos = -1;
                }
                deleteNodesArr.put(oldPos, new AppearanceDispatchData(oldNode, oldStatus, AttachStatus.DETACHED, scrollDirection));
            }
        }

        for (int i = 0; i < deleteNodesArr.size(); i++) {
            int pos = deleteNodesArr.keyAt(i);
            AttachStatusManager.AppearanceDispatchData data = deleteNodesArr.valueAt(i);
            dispatchNodeAppearanceEvent(pos, data.node, data.oldStatus, data.newStatus, data.scrollDirection);
        }

        for (int i = 0; i < inScreenNodesArr.size(); i++) {
            int pos = inScreenNodesArr.keyAt(i);
            AttachStatusManager.AppearanceDispatchData data = inScreenNodesArr.valueAt(i);
            dispatchNodeAppearanceEvent(pos, data.node, data.oldStatus, data.newStatus, data.scrollDirection);
        }
    }

    public void setNodeStatus(ShieldDisplayNode displayNode, AttachStatus status) {

        if (status == AttachStatus.DETACHED) {
            statusHashMap.remove(displayNode);
        } else {
            statusHashMap.put(displayNode, status);
        }
    }

    private void dispatchNodeAppearanceEvent(int position, ShieldDisplayNode displayNode,
                                             AttachStatus oldStatus, AttachStatus status, ScrollDirection scrollDirection) {
        if (oldStatus == null) {
            oldStatus = AttachStatus.DETACHED;
        }

        if (status == null) {
            status = AttachStatus.DETACHED;
        }

        if (oldStatus == status) {
            return;
        }

        if (displayNode.attachStatusChangeListenerList != null
                && !displayNode.attachStatusChangeListenerList.isEmpty()) {
            for (AttachStatusChangeListener<ShieldDisplayNode> asInterface : displayNode.attachStatusChangeListenerList) {
                asInterface.onAttachStatusChanged(position, displayNode, oldStatus, status, scrollDirection);
            }
        }

        if (displayNode.moveStatusEventListenerList != null
                && !displayNode.moveStatusEventListenerList.isEmpty()) {
            for (MoveStatusEventListener<ShieldDisplayNode> adInterface : displayNode.moveStatusEventListenerList) {
                AppearanceEvent[] appearanceEvents = AppearanceEvent.parseFromAttachStatus(oldStatus, status);

                if (appearanceEvents == null || appearanceEvents.length <= 0) {
                    continue;
                }

                for (int i = 0; i < appearanceEvents.length; i++) {
                    AppearanceEvent appearanceEvent = appearanceEvents[i];
                    if (appearanceEvent == AppearanceEvent.PARTLY_APPEAR || appearanceEvent == AppearanceEvent.FULLY_APPEAR) {
                        adInterface.onAppeared(position, displayNode, appearanceEvent, scrollDirection);
                    } else {
                        adInterface.onDisappeared(position, displayNode, appearanceEvent, scrollDirection);
                    }
                }
            }
        }
    }

    private static class AppearanceDispatchData {

        ShieldDisplayNode node;
        AttachStatus oldStatus;
        AttachStatus newStatus;
        ScrollDirection scrollDirection;

        public AppearanceDispatchData(ShieldDisplayNode node, AttachStatus oldStatus, AttachStatus newStatus, ScrollDirection scrollDirection) {
            this.node = node;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.scrollDirection = scrollDirection;
        }
    }

}
