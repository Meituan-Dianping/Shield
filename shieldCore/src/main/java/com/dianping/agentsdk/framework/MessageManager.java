package com.dianping.agentsdk.framework;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by runqi.wei on 2018/1/8.
 */

public class MessageManager<H> {
    protected String indexPrefix;

    protected OnMessageQueriedListener<H> onMessageQueriedListener;

    protected HashMap<String, ArrayList<H>> registrationMap;
    protected HashMap<String, Pair<String, H>> indexMap;
    protected int autoIncreaseIndex;

    public MessageManager(@NonNull String indexPrefix, OnMessageQueriedListener<H> onMessageQueriedListener) {
        autoIncreaseIndex = 0;
        registrationMap = new HashMap<>();
        indexMap = new HashMap<>();
        this.indexPrefix = indexPrefix;
        this.onMessageQueriedListener = onMessageQueriedListener;
    }

    public void clear() {
        if (registrationMap != null) {
            registrationMap.clear();
        }
        if (indexMap != null) {
            indexMap.clear();
        }
        autoIncreaseIndex = 0;
    }

    /**
     * For Test
     * @return
     */
    HashMap<String, ArrayList<H>> getRegistrationMap() {
        return registrationMap;
    }

    /**
     * For Test
     * @return
     */
    HashMap<String, Pair<String, H>> getIndexMap() {
        return indexMap;
    }

    public void setOnMessageQueriedListener(OnMessageQueriedListener<H> onMessageQueriedListener) {
        this.onMessageQueriedListener = onMessageQueriedListener;
    }

    protected String getNewIndex() {
        String index = ((indexPrefix == null) ? "" : indexPrefix) + autoIncreaseIndex;
        autoIncreaseIndex++;
        return index;
    }

    public String registerHandler(@NonNull String key, @NonNull H handler) {
        if (registrationMap == null) {
            registrationMap = new HashMap<>();
        }

        if (indexMap == null) {
            indexMap = new HashMap<>();
        }

        ArrayList<H> list = registrationMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
            registrationMap.put(key, list);
        }

        String idx = null;
        if (!list.contains(handler)) {
            // 如果没有注册，添加新的
            list.add(handler);
            idx = getNewIndex();
            indexMap.put(idx, new Pair<>(key, handler));
        } else {
            // 如果有注册, 返回上次注册的 id
            Pair<String, H> pair = new Pair<>(key, handler);
            for (Map.Entry<String, Pair<String, H>> entry : indexMap.entrySet()) {
                if (pair.equals(entry.getValue())) {
                    idx = entry.getKey();
                    break;
                }
            }
        }

        return idx;
    }

    public void removeHandler(@NonNull String id) {
        if (indexMap == null || indexMap.isEmpty()) {
            return;
        }
        Pair<String, H> pair = indexMap.get(id);
        if (pair == null || pair.first == null || pair.second == null) {
            return;
        }

        removeHandler(pair.first, pair.second);
    }

    public void removeHandler(@NonNull String key, @NonNull H handler) {
        if (registrationMap == null || registrationMap.isEmpty()) {
            return;
        }

        ArrayList<H> list = registrationMap.get(key);
        if (list == null || list.isEmpty() || !list.contains(handler)) {
            return;
        }

        list.remove(handler);
        removeId(key, handler);
    }

    public void removeHandler(@NonNull H handler) {
        if (registrationMap == null || registrationMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, ArrayList<H>> entry : registrationMap.entrySet()) {
            ArrayList<H> list = entry.getValue();
            if (list == null || list.isEmpty() || !list.contains(handler)) {
                continue;
            }
            list.remove(handler);
            removeId(entry.getKey(), handler);
        }
    }

    protected void removeId(@NonNull String key, @NonNull H handler){

        if (indexMap == null || indexMap.isEmpty()) {
            return;
        }

        String idx = null;
        Pair<String, H> pair = new Pair<>(key, handler);

        for (Map.Entry<String, Pair<String, H>> entry : indexMap.entrySet()) {
            if (pair.equals(entry.getValue())) {
                idx = entry.getKey();
                break;
            }
        }

        indexMap.remove(idx);
    }

    public ArrayList<Object> queryMessage(String key, Object parameter){
        ArrayList<Object> result = new ArrayList<>();
        ArrayList<H> handlerArrayList = registrationMap.get(key);
        if (handlerArrayList != null && !handlerArrayList.isEmpty()) {
            for (H handler : handlerArrayList) {
                if (handler == null) {
                    continue;
                }
                Object res = null;
                if (onMessageQueriedListener != null) {
                    res = onMessageQueriedListener.onMessageQueried(key, parameter, handler);
                }
                if (res != null) {
                    result.add(res);
                }
            }
        }

        return result;
    }

    public interface OnMessageQueriedListener<HA>{
        Object onMessageQueried(String key, Object parameter, HA handler);
    }
}
