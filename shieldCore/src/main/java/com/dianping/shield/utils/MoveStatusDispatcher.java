package com.dianping.shield.utils;

import android.os.Handler;
import android.os.Message;

import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.MoveStatusAction;

import java.lang.ref.WeakReference;

/**
 * Created by hezhi on 17/4/6.
 */

public class MoveStatusDispatcher {
    private static class MyHandler extends Handler {

        private final WeakReference<MoveStatusDispatcher> dispatcherWeakReference;

        public MyHandler(MoveStatusDispatcher dispatcherWeakReference) {
            this.dispatcherWeakReference = new WeakReference<MoveStatusDispatcher>(dispatcherWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.dispatcherWeakReference.get() == null) {
                return;
            }
            if (msg.obj == null) {
                return;
            }

            if (msg.obj instanceof MoveStatusAction) {
                MoveStatusAction action = (MoveStatusAction) msg.obj;
                if (action.moveStatusInterface != null && action.isSCI) {
                    if (action.isAppear) {
                        action.moveStatusInterface.onAppear(action.scope, action.direction);
                    } else {
                        action.moveStatusInterface.onDisappear(action.scope, action.direction);
                    }
                }

                if (action.cellMoveStatusInterface != null && action.cellType == CellType.NORMAL) {
                    if (action.isAppear) {
                        action.cellMoveStatusInterface.onAppear(action.scope, action.direction, action.section, action.row);
                    } else {
                        action.cellMoveStatusInterface.onDisappear(action.scope, action.direction, action.section, action.row);
                    }
                }

                if (action.extraCellMoveStatusInterface != null && action.cellType != CellType.NORMAL) {
                    if (action.isAppear) {
                        action.extraCellMoveStatusInterface.onAppear(action.scope, action.direction, action.section, action.cellType);
                    } else {
                        action.extraCellMoveStatusInterface.onDisappear(action.scope, action.direction, action.section, action.cellType);
                    }
                }
            }

        }
    }

    private MoveStatusDispatcher.MyHandler mStatusHandler;

    public MoveStatusDispatcher() {
        mStatusHandler = new MoveStatusDispatcher.MyHandler(this);
    }

    public void moveAction(MoveStatusAction moveStatusAction) {
        if (moveStatusAction == null || (moveStatusAction.moveStatusInterface == null
                && moveStatusAction.cellMoveStatusInterface == null && moveStatusAction.extraCellMoveStatusInterface == null)) {
            return;
        }
        Message moveMessage = new Message();
        moveMessage.what = moveStatusAction.hashCode();
        moveMessage.obj = moveStatusAction;
        mStatusHandler.sendMessage(moveMessage);

    }

    public void stopDispatch() {
        mStatusHandler.removeCallbacksAndMessages(null);
    }
}
