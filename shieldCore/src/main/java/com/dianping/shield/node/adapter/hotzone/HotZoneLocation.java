package com.dianping.shield.node.adapter.hotzone;

/**
 * Created by runqi.wei at 2018/9/4
 */
public enum HotZoneLocation {
    // (Top, Bottom)
    DETACHED,
    US_US, // (UP_START, UP_START)
    US_BT, // (UP_START, BETWEEN_START_END)
    US_BE, // (UP_START, BELOW_END)
    BT_BT, // (BETWEEN_START_END, BETWEEN_START_END)
    BT_BE, // (BETWEEN_START_END, BELOW_END)
    BE_BE; // (BELOW_END, BELOW_END)

    private static HotZoneLocation[] valueArr = values();

    public static HotZoneLocation createFrom(int top, int bottom, int hotZoneStart, int hotZoneEnd) {
        if (hotZoneStart >= hotZoneEnd || bottom <= top) {
            return DETACHED;
        }
        if (top < hotZoneStart) {
            if (bottom <= hotZoneStart) {
                return US_US;
            } else if (bottom <= hotZoneEnd) {
                return US_BT;
            } else {
                return US_BE;
            }
        } else if (top < hotZoneEnd) {
            if (bottom <= hotZoneEnd) {
                return BT_BT;
            } else {
                return BT_BE;
            }
        } else {
            return BE_BE;
        }

    }

    public static HotZoneLocation createDetachedPosition(int position, int first, int last) {
        if (position < first) {
            return HotZoneLocation.US_US;
        } else if (position > last) {
            return HotZoneLocation.BE_BE;
        } else {
            return HotZoneLocation.DETACHED;
        }
    }
}
