package com.dianping.shield.entity;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by hezhi on 17/2/22.
 */

public class AdapterExposedList {
    public ArrayList<Pair<Integer, Integer>> completeExposedList;
    public ArrayList<Pair<Integer, Integer>> partExposedList;

    public AdapterExposedList() {
        completeExposedList = new ArrayList<>();
        partExposedList = new ArrayList<>();
    }

    public void addToList(ExposedDetails details) {
        if (details.isComplete) {
            completeExposedList.add(new Pair<Integer, Integer>(details.section, details.row));
        } else {
            partExposedList.add(new Pair<Integer, Integer>(details.section, details.row));
        }
    }
}
