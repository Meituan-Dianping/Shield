package com.dianping.agentsdk.manager;

import android.text.TextUtils;

import com.dianping.agentsdk.adapter.FinalPieceAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by runqi.wei
 * 14:17
 * 17.11.2016.
 */

public class GroupManager {

    protected ArrayList<FinalPieceAdapter> adapters;
    protected HashMap<FinalPieceAdapter, String> adapterGroupMap;

    public GroupManager() {
        adapters = new ArrayList<>();
        adapterGroupMap = new HashMap<>();
    }

    public String findGroupId(FinalPieceAdapter adapter) {
        if (adapter != null && adapterGroupMap.containsKey(adapter)) {
            return adapterGroupMap.get(adapter);
        }

        return null;
    }

    public boolean atFirstOfGroup(FinalPieceAdapter adapter) {
        if (adapter == null || !adapterGroupMap.containsKey(adapter)) {
            return false;
        }

        FinalPieceAdapter previousPieceAdapter = null;
        int index = adapters.indexOf(adapter);
        for (int i = index - 1; i >= 0; i--) {
            int rowCount = 0;
            previousPieceAdapter = adapters.get(i);
            for (int j = 0; j < previousPieceAdapter.getSectionCount(); j++) {
                rowCount += previousPieceAdapter.getRowCount(j);
            }
            if (rowCount > 0) {
                break;
            } else {
                previousPieceAdapter = null;
            }
        }

        if (previousPieceAdapter == null) {
            return true;
        }

        if (!adapterGroupMap.containsKey(previousPieceAdapter)) {
            return true;
        }

        String previousGroup = adapterGroupMap.get(previousPieceAdapter);
        String group = adapterGroupMap.get(adapter);
        if (!TextUtils.equals(previousGroup, group)) {
            return true;
        }

        return false;
    }

    public boolean atLastOfGroup(FinalPieceAdapter adapter) {
        if (adapter == null || !adapterGroupMap.containsKey(adapter)) {
            return false;
        }

        FinalPieceAdapter nextPieceAdapter = null;
        int index = adapters.indexOf(adapter);
        for (int i = index + 1; i < adapters.size(); i++) {
            int rowCount = 0;
            nextPieceAdapter = adapters.get(i);
            for (int j = 0; j < nextPieceAdapter.getSectionCount(); j++) {
                rowCount += nextPieceAdapter.getRowCount(j);
            }
            if (rowCount > 0) {
                break;
            } else {
                nextPieceAdapter = null;
            }
        }

        if (nextPieceAdapter == null) {
            return true;
        }

        if (!adapterGroupMap.containsKey(nextPieceAdapter)) {
            return true;
        }

        String nextGroup = adapterGroupMap.get(nextPieceAdapter);
        String group = adapterGroupMap.get(adapter);
        if (!TextUtils.equals(nextGroup, group)) {
            return true;
        }

        return false;
    }


    public void addAdapter(FinalPieceAdapter adapter, String group) {
        addAdapter(adapter, -1, group);
    }

    public void addAdapter(FinalPieceAdapter adapter, int index, String group) {

        if (adapter == null) {
            return;
        }

        if (index < 0 || index > adapters.size()) {
            adapters.add(adapter);
        } else {
            adapters.add(index, adapter);
        }
        adapterGroupMap.put(adapter, group);

    }

    public void removeAdapter(FinalPieceAdapter adapter) {
        if (adapter == null) {
            return;
        }

        if (adapterGroupMap.containsKey(adapter)) {
            adapterGroupMap.remove(adapter);
            adapters.remove(adapter);
        }
    }

    public void clear() {
        adapters.clear();
        adapterGroupMap.clear();
    }

}
