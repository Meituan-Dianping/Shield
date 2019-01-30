package com.dianping.shield.node.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/11/2
 */
public class AttachStatusCollection {


    protected ArrayList<ViewLocationChangeProcessor> processorList = new ArrayList<>();

    private boolean running = true;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void addAttStatusManager(ViewLocationChangeProcessor manager) {
        processorList.add(manager);
    }

    public void removeAttStatusManager(ViewLocationChangeProcessor manager) {
        processorList.remove(manager);
    }

    public void updateFistLastPositionInfo(RecyclerView recyclerView, int positionOffset, ScrollDirection scrollDirection) {

        if (!running) {
            return;
        }

        if (recyclerView == null) {
            return;
        }

        updateIndex(recyclerView, positionOffset);
        updateProcessors(scrollDirection);

    }

    private void updateIndex(RecyclerView recyclerView, int positionOffset) {
        int spanCount = getSpanCount(recyclerView);

        for (ViewLocationChangeProcessor manager : processorList) {
            if (manager.firstLastPositionInfo != null) {
                manager.firstLastPositionInfo.clear();
            }
        }

        for (int childIndex = 0; childIndex < recyclerView.getChildCount(); childIndex++) {
            View child = recyclerView.getChildAt(childIndex);
            int childPos = recyclerView.getChildLayoutPosition(child) + positionOffset; //getChildAdapterPosition
            int spanIndex = 0;
            //int childPos = recyclerView.getChildAdapterPosition(child) + positionOffset;

            if (child.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) child.getLayoutParams();
                if (!sglp.isFullSpan()) {
                    spanIndex = (sglp).getSpanIndex();
                }
            }

            if (spanIndex < 0) {
                continue;
            }

            int childTop = child.getTop();
            int childBottom = child.getBottom();

            for (ViewLocationChangeProcessor manager : processorList) {
                ViewLocationChangeProcessor.FirstLastPositionInfo result = manager.firstLastPositionInfo;
                if (result == null || result.spanCount != spanCount) {
                    result = new ViewLocationChangeProcessor.FirstLastPositionInfo(spanCount);
                }

                int actualTop = manager.getActualTop(recyclerView);
                int actualBottom = manager.getActualBottom(recyclerView);

                HotZoneLocation hotZoneLocation = HotZoneLocation.createFrom(childTop, childBottom, actualTop, actualBottom);
                result.locationSparseArray.put(childPos, hotZoneLocation);
                switch (hotZoneLocation) {
                    case US_BT:
                    case US_BE:
                    case BT_BE:
                        result.updateFistVisibleItemPosition(spanIndex, childPos);
                        result.updateLastVisibleItemPosition(spanIndex, childPos);
                        break;
                    case BT_BT:
                        result.updateFistVisibleItemPosition(spanIndex, childPos);
                        result.addCompletelyVisibleItemPosition(childPos);
                        result.updateLastVisibleItemPosition(spanIndex, childPos);
                        break;
                    default:
                        break;
                }

                manager.firstLastPositionInfo = result;
            }
        }
    }

    private void updateProcessors(ScrollDirection scrollDirection) {

        for (ViewLocationChangeProcessor manager : processorList) {
            manager.onViewLocationChanged(scrollDirection);
        }

    }

    private int getSpanCount(RecyclerView recyclerView) {
        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

}
