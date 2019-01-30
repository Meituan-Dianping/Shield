package com.dianping.shield.node.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation;
import com.dianping.shield.node.cellnode.AttachStatus;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by runqi.wei at 2018/11/2
 */
public abstract class ViewLocationChangeProcessor implements Cloneable {

    public static final int POSITION_RV_BOTTOM = -1;
    protected FirstLastPositionInfo firstLastPositionInfo = new FirstLastPositionInfo(1);
    protected int top;
    protected int bottom = POSITION_RV_BOTTOM;
    protected HashMap<ShieldDisplayNode, AttachStatus> statusHashMap = new HashMap<>();
    protected HashMap<ShieldDisplayNode, Integer> positionHashMap = new HashMap<>();

    ViewLocationChangeProcessor(int bottom, int top) {
        this.bottom = bottom;
        this.top = top;
    }

    public void setHotZoneYRange(HotZoneYRange hotZoneYRange) {
        this.bottom = hotZoneYRange.endY;
        this.top = hotZoneYRange.startY;
    }

    public int getActualTop(RecyclerView recyclerView) {
        return top;
    }

    public int getActualBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            if (bottom < 0) {
                return 0;
            }
            return bottom;
        }

        return bottom == POSITION_RV_BOTTOM ? recyclerView.getHeight() : bottom;
    }

    public abstract void onViewLocationChanged(ScrollDirection scrollDirection);

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public void clear() {
        statusHashMap.clear();
    }

    public static class AppearanceDispatchData {

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

    public static class FirstLastPositionInfo implements Cloneable {

        public ArrayList<Integer> firstVisibleItemPositions;
        public ArrayList<Integer> completelyVisibleItemPositions;
        public ArrayList<Integer> lastVisibleItemPositions;
        public int spanCount;

        public SparseArray<HotZoneLocation> locationSparseArray;

        public FirstLastPositionInfo(int spanCount) {
            this.spanCount = spanCount;
            firstVisibleItemPositions = new ArrayList<>();
            completelyVisibleItemPositions = new ArrayList<>();
            lastVisibleItemPositions = new ArrayList<>();
            locationSparseArray = new SparseArray<>();

            clear();
        }

        public void clear() {
            completelyVisibleItemPositions.clear();
            firstVisibleItemPositions.clear();
            lastVisibleItemPositions.clear();
            locationSparseArray.clear();
            for (int i = 0; i < spanCount; i++) {
                firstVisibleItemPositions.add(-1);
                lastVisibleItemPositions.add(-1);
            }
        }

        public boolean isEmpty(){
            if (firstVisibleItemPositions != null && firstVisibleItemPositions.size() > 0) {
                for (int i = 0; i < firstVisibleItemPositions.size(); i++) {
                    Integer firstIndex = firstVisibleItemPositions.get(i);
                    if (firstIndex != null && firstIndex >= 0){
                        return false;
                    }
                }
            }

            if (lastVisibleItemPositions != null && lastVisibleItemPositions.size() > 0) {
                for (int i = 0; i < lastVisibleItemPositions.size(); i++) {
                    Integer lastIndex = lastVisibleItemPositions.get(i);
                    if (lastIndex != null && lastIndex >= 0){
                        return false;
                    }
                }
            }

            if (completelyVisibleItemPositions != null && completelyVisibleItemPositions.size() > 0) {
                for (int i = 0; i < completelyVisibleItemPositions.size(); i++) {
                    Integer completeIndex = completelyVisibleItemPositions.get(i);
                    if (completeIndex != null && completeIndex >= 0) {
                        return false;
                    }
                }
            }

            if (locationSparseArray != null && locationSparseArray.size() >= 0){
                for (int i = 0; i < locationSparseArray.size(); i++) {
                    if (locationSparseArray.keyAt(i) >= 0) {
                        return false;
                    }
                }
            }

            return true;
        }

        public void updateFistVisibleItemPosition(int spanIndex, int pos) {
            if (firstVisibleItemPositions.get(spanIndex) < 0 || firstVisibleItemPositions.get(spanIndex) > pos) {
                firstVisibleItemPositions.set(spanIndex, pos);
            }
        }

        public void addCompletelyVisibleItemPosition(int pos) {
            if (completelyVisibleItemPositions.contains(pos)) {
                return;
            }
            completelyVisibleItemPositions.add(pos);
        }

        public void updateLastVisibleItemPosition(int spanIndex, int pos) {
            if (lastVisibleItemPositions.get(spanIndex) < 0 || lastVisibleItemPositions.get(spanIndex) < pos) {
                lastVisibleItemPositions.set(spanIndex, pos);
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            FirstLastPositionInfo info = (FirstLastPositionInfo) super.clone();

            info.firstVisibleItemPositions = new ArrayList<>(this.firstVisibleItemPositions.size());
            info.firstVisibleItemPositions.addAll(this.firstVisibleItemPositions);

            info.lastVisibleItemPositions = new ArrayList<>(this.lastVisibleItemPositions.size());
            info.lastVisibleItemPositions.addAll(this.lastVisibleItemPositions);

            info.completelyVisibleItemPositions = new ArrayList<>(this.completelyVisibleItemPositions.size());
            info.completelyVisibleItemPositions.addAll(this.completelyVisibleItemPositions);

            info.spanCount = spanCount;
            info.locationSparseArray = new SparseArray<>(this.locationSparseArray.size());
            for (int i = 0; i < this.locationSparseArray.size(); i++) {
                info.locationSparseArray.put(this.locationSparseArray.keyAt(i), this.locationSparseArray.valueAt(i));
            }

            return info;
        }
    }
}
