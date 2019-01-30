package com.dianping.shield.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ExposedAction;
import com.dianping.shield.feature.CellExposedInterface;
import com.dianping.shield.feature.ExposedInterface;
import com.dianping.shield.feature.ExtraCellExposedInterface;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by zdh on 17/2/22.
 */

public class ExposedDispatcher {
    private static final long ONE_WEEK_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;
    private MyHandler mExposeHandler;

    public ExposedDispatcher() {
        mExposeHandler = new MyHandler(this);
    }

    public void exposedAction(ExposedAction exposedAction) {
        if (exposedAction == null || exposedAction.owner == null) {
            return;
        }
        if (!exposedAction.isAddExposed) {
            mExposeHandler.removeMessages(exposedAction.hashCode());
        } else {
            long delayTime = 0;
            int exposeCount = 0;
            ExposedObj exposedObj = new ExposedObj();
            exposedObj.section = exposedAction.section;
            exposedObj.row = exposedAction.row;
            exposedObj.cellType = exposedAction.cellType;

            if (exposedAction.isAgentExposed && exposedAction.owner instanceof ExposedInterface) {
                ExposedInterface exposedInterface = (ExposedInterface) exposedAction.owner;
                delayTime = exposedInterface.stayDuration();
                exposeCount = exposedInterface.maxExposeCount();
                exposedObj.delayTime = exposedInterface.exposeDuration();
                exposedObj.exposedInterface = exposedInterface;
            } else if (exposedAction.cellType == CellType.NORMAL && exposedAction.owner instanceof CellExposedInterface) {
                CellExposedInterface exposedInterface = (CellExposedInterface) exposedAction.owner;
                delayTime = exposedInterface.stayDuration(exposedAction.section, exposedAction.row);
                exposeCount = exposedInterface.maxExposeCount(exposedAction.section, exposedAction.row);
                exposedObj.delayTime = exposedInterface.exposeDuration(exposedAction.section, exposedAction.row);
                exposedObj.cellExposedInterface = exposedInterface;
            } else if (exposedAction.cellType != CellType.NORMAL && exposedAction.owner instanceof ExtraCellExposedInterface) {
                ExtraCellExposedInterface exposedInterface = (ExtraCellExposedInterface) exposedAction.owner;
                delayTime = exposedInterface.extraCellStayDuration(exposedAction.section, exposedAction.cellType);
                exposeCount = exposedInterface.maxExtraExposeCount(exposedAction.section, exposedAction.cellType);
                exposedObj.delayTime = exposedInterface.extraCellExposeDuration(exposedAction.section, exposedAction.cellType);
                exposedObj.extraExposedInterface = exposedInterface;
            } else {
                return;
            }

            int what = exposedAction.hashCode();
            int temExposedCount = 0;
            if (mExposeHandler.exposedActionCountMap.containsKey(what)) {
                temExposedCount = mExposeHandler.exposedActionCountMap.get(what).count;
                Log.d("ExposeCountMap","check count for " + what + ", count = " + temExposedCount + " Action = " + exposedAction);
            } else {
                mExposeHandler.exposedActionCountMap.put(what, new CountObj(0));
                Log.d("ExposeCountMap","ExposeCountMap.put(" + what + "， 0), Action = " + exposedAction);
            }
            if (temExposedCount < exposeCount && delayTime <= ONE_WEEK_MILLISECONDS) {
                Message exposeMessage = new Message();
                exposeMessage.what = exposedAction.hashCode();
                exposeMessage.obj = exposedObj;
                mExposeHandler.sendMessageDelayed(exposeMessage, delayTime);
            }
        }
    }

    public void finishExposed() {
        mExposeHandler.removeCallbacksAndMessages(null);
        mExposeHandler.exposedActionCountMap.clear();
    }

    public void pauseExposed() {
        mExposeHandler.removeCallbacksAndMessages(null);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ExposedDispatcher> dispatcherWeakReference;
        HashMap<Integer, CountObj> exposedActionCountMap = new HashMap<Integer, CountObj>();

        public MyHandler(ExposedDispatcher dispatcherWeakReference) {
            this.dispatcherWeakReference = new WeakReference<ExposedDispatcher>(dispatcherWeakReference);
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

            ExposedObj exposeObj = (ExposedObj) msg.obj;
            CountObj hadExposedCountObj = exposedActionCountMap.get(msg.what);
            int maxExposedCount = 0;
            if (exposeObj.exposedInterface != null) {
                maxExposedCount = exposeObj.exposedInterface.maxExposeCount();
                exposeObj.delayTime = exposeObj.exposedInterface.exposeDuration();
                if (hadExposedCountObj.count >= maxExposedCount) {
                    return;
                }
                exposeObj.exposedInterface.onExposed(hadExposedCountObj.count + 1);
            } else if (exposeObj.cellExposedInterface != null) {

                if (exposeObj.cellExposedInterface instanceof SectionCellInterface) {
                    if (exposeObj.section < 0 || (exposeObj.section >= ((SectionCellInterface) exposeObj.cellExposedInterface).getSectionCount())
                            || exposeObj.row < 0 || (exposeObj.row >= ((SectionCellInterface) exposeObj.cellExposedInterface).getRowCount(exposeObj.section))) {
                        Log.e("Shield", "Expose Index out of bound");
                        return;
                    }
                }

                maxExposedCount = exposeObj.cellExposedInterface.maxExposeCount(exposeObj.section, exposeObj.row);
                exposeObj.delayTime = exposeObj.cellExposedInterface.exposeDuration(exposeObj.section, exposeObj.row);
                if (hadExposedCountObj.count >= maxExposedCount) {
                    return;
                }
                exposeObj.cellExposedInterface.onExposed(exposeObj.section, exposeObj.row, hadExposedCountObj.count + 1);
                Log.d("ExposedDispatcher", "OnCellExposed - (" + exposeObj.section + ", " + exposeObj.row + "), CellType = " + exposeObj.cellType);
            } else if (exposeObj.extraExposedInterface != null) {
                if (exposeObj.extraExposedInterface instanceof SectionCellInterface) {
                    if (exposeObj.section < 0 || (exposeObj.section >= ((SectionCellInterface) exposeObj.extraExposedInterface).getSectionCount())) {
                        Log.e("Shield", "Expose Index out of bound");
                        return;
                    }
                }
                maxExposedCount = exposeObj.extraExposedInterface.maxExtraExposeCount(exposeObj.section, exposeObj.cellType);
                exposeObj.delayTime = exposeObj.extraExposedInterface.extraCellStayDuration(exposeObj.section, exposeObj.cellType);
                if (hadExposedCountObj.count >= maxExposedCount) {
                    return;
                }
                exposeObj.extraExposedInterface.onExtraCellExposed(exposeObj.section, exposeObj.cellType, hadExposedCountObj.count + 1);
                Log.d("ExposedDispatcher", "OnExtraCellExposed - (" + exposeObj.section + ", " + exposeObj.row + "), CellType = " + exposeObj.cellType);
            } else {
                return;
            }

            hadExposedCountObj.count += 1;

            if (exposeObj.delayTime > ONE_WEEK_MILLISECONDS || exposeObj.delayTime <= 0) {
                return;
            }

            Message newMessage = new Message();
            newMessage.what = msg.what;
            newMessage.obj = exposeObj;
            this.sendMessageDelayed(newMessage, exposeObj.delayTime);
        }
    }

    private static class CountObj {
        int count;

        public CountObj(int count) {
            this.count = count;
        }
    }

    private static class ExposedObj {
        long delayTime = 0;
        int section = 0;
        int row = 0;
        CellType cellType;
        ExposedInterface exposedInterface;
        CellExposedInterface cellExposedInterface;
        ExtraCellExposedInterface extraExposedInterface;
    }
}
