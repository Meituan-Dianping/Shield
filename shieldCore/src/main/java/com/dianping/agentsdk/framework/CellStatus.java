package com.dianping.agentsdk.framework;

/**
 * Created by hezhi on 16/6/24.
 */
public class CellStatus {
    public enum LoadingStatus {
        EMPTY,
        LOADING,
        FAILED,
        DONE,
        UNKNOWN
    }

    public enum LoadingMoreStatus {
        LOADING,
        FAILED,
        DONE,
        UNKNOWN
    }
}
