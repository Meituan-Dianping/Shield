package com.dianping.shield.node.cellnode;

/**
 * Created by runqi.wei at 2018/7/11
 */
public enum AppearanceEvent {
    PARTLY_APPEAR,
    FULLY_APPEAR,
    PARTLY_DISAPPEAR,
    FULLY_DISAPPEAR;

    public static AppearanceEvent[] parseFromAttachStatus(AttachStatus oldStatus, AttachStatus newStatus) {
        if (oldStatus == newStatus) {
            return null;
        }
        if (oldStatus == null) {
            oldStatus = AttachStatus.DETACHED;
        }
        if (newStatus == null) {
            newStatus = AttachStatus.DETACHED;
        }
        switch (oldStatus) {
            case FULLY_ATTACHED:
                if (newStatus == AttachStatus.PARTLY_ATTACHED) {
                    return new AppearanceEvent[]{PARTLY_DISAPPEAR};
                } else if (newStatus == AttachStatus.DETACHED) {
                    return new AppearanceEvent[]{PARTLY_DISAPPEAR, FULLY_DISAPPEAR};
                }
                break;
            case PARTLY_ATTACHED:
                if (newStatus == AttachStatus.FULLY_ATTACHED) {
                    return new AppearanceEvent[]{FULLY_APPEAR};
                } else if (newStatus == AttachStatus.DETACHED) {
                    return new AppearanceEvent[]{FULLY_DISAPPEAR};
                }
                break;
            case DETACHED:
                if (newStatus == AttachStatus.FULLY_ATTACHED) {
                    return new AppearanceEvent[]{PARTLY_APPEAR, FULLY_APPEAR};
                } else if (newStatus == AttachStatus.PARTLY_ATTACHED) {
                    return new AppearanceEvent[]{PARTLY_APPEAR};
                }
                break;
        }
        return null;
    }
}
