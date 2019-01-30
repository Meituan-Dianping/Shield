package com.dianping.shield.utils;

import android.util.Pair;

import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.AdapterExposedList;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ExposeScope;
import com.dianping.shield.entity.ExposedAction;
import com.dianping.shield.entity.ExposedInfo;
import com.dianping.shield.entity.MoveStatusAction;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.feature.CellExposedInterface;
import com.dianping.shield.feature.CellMoveStatusInterface;
import com.dianping.shield.feature.ExposedInterface;
import com.dianping.shield.feature.ExtraCellExposedInterface;
import com.dianping.shield.feature.ExtraCellMoveStatusInterface;
import com.dianping.shield.feature.MoveStatusInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hezhi on 17/2/21.
 */

public class ExposedEngine {

    ArrayList<ExposedInfo> innerInfos = new ArrayList<>();

    HashMap<PieceAdapter, AdapterExposedList> adapterMap = new HashMap<>();

    //store only when Activity is Pauseing
    HashMap<PieceAdapter, AdapterExposedList> adapterStatusMap = new HashMap<>();

    //record page resume agent list
    HashMap<PieceAdapter, AdapterExposedList> resumeStatusMap = new HashMap<>();

    ExposedDispatcher dispatcher = new ExposedDispatcher();

    MoveStatusDispatcher moveStatusDispatcher = new MoveStatusDispatcher();

    boolean isPageResumed = false;

    public synchronized void updateExposedSections(ArrayList<ExposedInfo> exposedInfos, ScrollDirection direction) {

        //新的列表copy 用来判断行
        ArrayList<ExposedInfo> tempInfos = (ArrayList<ExposedInfo>) exposedInfos.clone();
        //旧的Map copy 用来判断整个模块
        HashMap<PieceAdapter, AdapterExposedList> tempMap = (HashMap<PieceAdapter, AdapterExposedList>) adapterMap.clone();

        adapterMap.clear();
//        AdapterExposedList pieceDetailArray = null;
        //遍历新列表
        for (ExposedInfo info : exposedInfos) {
            //组建新MAP
            if (!adapterMap.containsKey(info.owner)) {
                //如果不包含，说明是第一个新的
//                if (pieceDetailArray != null) {
//                    adapterMap.put(info.owner, pieceDetailArray);
//                }
                AdapterExposedList pieceDetailArray = new AdapterExposedList();
                pieceDetailArray.addToList(info.details);
                adapterMap.put(info.owner, pieceDetailArray);
            } else {
                //如果包含说明有旧的，拿出来更新
                AdapterExposedList dArray = adapterMap.get(info.owner);
                dArray.addToList(info.details);
                adapterMap.put(info.owner, dArray);
            }


            if (innerInfos.contains(info)) {
                tempInfos.remove(info);
                innerInfos.remove(info);
            } else {
                SectionCellInterface cellInterface = info.owner.getSectionCellInterface();
                if (cellInterface instanceof CellExposedInterface || cellInterface instanceof ExtraCellExposedInterface
                        || cellInterface instanceof CellMoveStatusInterface || cellInterface instanceof ExtraCellMoveStatusInterface) {
//                    CellType type = info.owner.getCellType(info.details.section, info.details.row);
                    CellType type = info.details.cellType;
                    Pair<Integer, Integer> pair = info.owner.getInnerPosition(info.details.section, info.details.row);
                    ExposeScope scope = getScope(cellInterface, type, pair);
                    if (info.details.isComplete) {
                        ExposedInfo temp = new ExposedInfo();
                        temp.owner = info.owner;
                        temp.details.section = info.details.section;
                        temp.details.row = info.details.row;
                        temp.details.cellType = info.details.cellType;
                        temp.details.isComplete = false;
                        if (innerInfos.contains(temp)) {
                            //PART_TO_FULL
                            //先调移入移出屏幕回调
                            dispatchCellMove(cellInterface, ExposeScope.COMPLETE, direction, pair.first, pair.second, type, true, false);
                            //再走曝光逻辑
                            innerInfos.remove(temp);
                            //complete 是新增
                            if (scope == ExposeScope.COMPLETE) {
                                dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, true, false));
                            }
                        } else {
                            //NEW_FULL  px和complete都是新增
                            //先调移入移出屏幕回调
                            dispatchCellMove(cellInterface, ExposeScope.PX, direction, pair.first, pair.second, type, true, false);
                            dispatchCellMove(cellInterface, ExposeScope.COMPLETE, direction, pair.first, pair.second, type, true, false);
                            //再走曝光逻辑
                            dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, true, false));
                        }

                    } else {
                        ExposedInfo temp = new ExposedInfo();
                        temp.owner = info.owner;
                        temp.details.section = info.details.section;
                        temp.details.row = info.details.row;
                        temp.details.cellType = info.details.cellType;
                        temp.details.isComplete = true;
                        if (innerInfos.contains(temp)) {
                            //FULL_TO_PART
                            innerInfos.remove(temp);
                            //先调移入屏幕回调
                            dispatchCellMove(cellInterface, ExposeScope.PX, direction, pair.first, pair.second, type, false, false);
                            //再走曝光逻辑
                            if (scope == ExposeScope.COMPLETE) {
                                //complete是移除
                                dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, false, false));
                            }
                        } else {
                            //NEW_PART

                            //先调移入屏幕回调
                            dispatchCellMove(cellInterface, ExposeScope.PX, direction, pair.first, pair.second, type, true, false);
                            //再走曝光逻辑

                            if (scope == ExposeScope.PX) {
                                //PX是增加
                                dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, true, false));
                            }
                        }
                    }

                }
            }
        }

        //一轮遍历之后innerInfos里剩下的就是PART_TO_END和FULL_TO_END
        for (ExposedInfo info : innerInfos) {
            SectionCellInterface cellInterface = info.owner.getSectionCellInterface();
            if (cellInterface instanceof CellExposedInterface || cellInterface instanceof ExtraCellExposedInterface
                    || cellInterface instanceof CellMoveStatusInterface || cellInterface instanceof ExtraCellMoveStatusInterface) {
                //过滤掉数据变化导致的index越界
                if (info.owner.getSectionCount() > info.details.section && info.owner.getRowCount(info.details.section) > info.details.row) {
//                    CellType type = info.owner.getCellType(info.details.section, info.details.row);
                    CellType type = info.details.cellType;
                    Pair<Integer, Integer> pair = info.owner.getInnerPosition(info.details.section, info.details.row);
                    ExposeScope scope = getScope(cellInterface, type, pair);
                    if (info.details.isComplete) {
                        //FULL_TO_END
                        //先调移入移出屏幕回
                        dispatchCellMove(cellInterface, ExposeScope.PX, direction, pair.first, pair.second, type, false, false);
                        dispatchCellMove(cellInterface, ExposeScope.COMPLETE, direction, pair.first, pair.second, type, false, false);

                        //再走曝光逻辑
                        //complete和px都是移除
                        dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, false, false));
                    } else {
                        //PART_TO_END
                        //先调移入屏幕回调
                        dispatchCellMove(cellInterface, ExposeScope.COMPLETE, direction, pair.first, pair.second, type, false, false);
                        //再走曝光逻辑
                        //只有px是移除
                        if (scope == ExposeScope.PX) {
                            dispatcher.exposedAction(new ExposedAction(cellInterface, pair.first, pair.second, type, false, false));
                        }
                    }
                }
            }
        }

        //保存新的
        innerInfos = exposedInfos;

        //计算模块级开始
        //MAP遍历
        ArrayList<ExposedAction> actionList = new ArrayList<>();

        for (Map.Entry<PieceAdapter, AdapterExposedList> entry : adapterMap.entrySet()) {
            SectionCellInterface sectionCellInterface = entry.getKey().getSectionCellInterface();
            if (sectionCellInterface instanceof ExposedInterface || sectionCellInterface instanceof MoveStatusInterface) {
                if (!tempMap.containsKey(entry.getKey())) {
                    //旧Map不包含这个Adapter，说明这是新进入的

                    AdapterExposedList newList = entry.getValue();
                    if (newList.partExposedList.isEmpty()
                            && newList.completeExposedList.size() == entry.getKey().getItemCount()) {
                        //过滤掉一次已经PageResume后的MoveStatus Appear分发
                        if (direction != ScrollDirection.STATIC || (!isPageResumed) || !resumeStatusMap.containsKey(entry.getKey())) {
                            //SC_NEW_FULL
                            //先调移入屏幕回调
                            dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, true, true);
                            dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, true, true);
                        }
                        //再走曝光逻辑

                        //px和complete都是新增
                        dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, true, true));
                        //列表内全为NEW_FULL
                    } else {
                        //过滤掉一次已经PageResume后的MoveStatus Appear分发
                        if (direction != ScrollDirection.STATIC || (!isPageResumed)||!resumeStatusMap.containsKey(entry.getKey())) {
                            //SC_NEW_PART
                            //先调移入屏幕回调
                            dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, true, true);
                            //再走曝光逻辑
                        }
                        if (((ExposedInterface) sectionCellInterface).getExposeScope() == ExposeScope.PX) {
                            dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, true, true));
                        }
                        //FULL列表内全为NEW_FULL，PART列表内全为NEW_PART

                    }

                } else {
                    //旧的包含分几种情况：
                    AdapterExposedList newList = entry.getValue();
                    AdapterExposedList oldList = tempMap.get(entry.getKey());
                    if (newList != null && oldList != null) {
                        if (!(newList.completeExposedList.equals(oldList.completeExposedList)
                                && newList.partExposedList.equals(oldList.partExposedList))) {
                            //内部列表不一样
                            if (newList.partExposedList.isEmpty()
                                    && newList.completeExposedList.size() == entry.getKey().getItemCount()) {
                                //过滤掉一次已经PageResume后的MoveStatus Appear分发
                                if (direction != ScrollDirection.STATIC || (!isPageResumed)||!resumeStatusMap.containsKey(entry.getKey())) {
                                    //新的part为空，full为全, SC_PART_TO_FULL,不是移出,remove temp
                                    //先调移入屏幕回调
                                    dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, true, true);
                                    //再走曝光逻辑
                                }
                                //增加一条按模块曝光
                                if (((ExposedInterface) sectionCellInterface).getExposeScope() == ExposeScope.COMPLETE) {
                                    dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, true, true));
                                }

                            } else if (oldList.partExposedList.isEmpty()
                                    && oldList.completeExposedList.size() == entry.getKey().getItemCount()) {

                                //老的part为空，full为全, SC_FULL_TO_PART,不是移出,remove temp
                                //先调移入屏幕回调
                                dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, false, true);
                                //再走曝光逻辑

                                //移除一条按模块曝光
                                if (((ExposedInterface) sectionCellInterface).getExposeScope() == ExposeScope.COMPLETE) {
                                    dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, false, true));
                                }
                            } else {
                                //part内部变化，或者full内部变化，都不做处理 ,直接从旧的tempMap移除
                            }
                        }
                    }
                    //内部列表一样，说明没变

                    tempMap.remove(entry.getKey());
                    isPageResumed = false;
                    resumeStatusMap.clear();
                }
            }
        }

        //一遍循环后 旧tempMap剩下的就是SC_PART_TO_END和SC_FULL_TO_END
        for (Map.Entry<PieceAdapter, AdapterExposedList> entry : tempMap.entrySet()) {
            SectionCellInterface sectionCellInterface = entry.getKey().getSectionCellInterface();
            if (sectionCellInterface instanceof ExposedInterface || sectionCellInterface instanceof MoveStatusInterface) {
                AdapterExposedList newList = entry.getValue();
                if (newList.partExposedList.isEmpty()
                        && newList.completeExposedList.size() == entry.getKey().getItemCount()) {
                    //FULL_TO_END
                    //先调移入移出屏幕回调
                    dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, false, true);
                    dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, false, true);
                    //再走曝光逻辑

                    //px和complete都移除
                    dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, false, true));

                } else {
                    //PART_TO_END
                    //先调移入屏幕回调
                    dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, false, true);
                    //再走曝光逻辑

                    //px 移除
                    if (((ExposedInterface) sectionCellInterface).getExposeScope() == ExposeScope.PX) {
                        dispatcher.exposedAction(new ExposedAction(sectionCellInterface, -1, -1, null, false, true));
                    }
                }
            }
        }

    }

    //获取模块行曝光范围
    private ExposeScope getScope(SectionCellInterface sectionCellInterface, CellType type, Pair<Integer, Integer> postion) {
        if (sectionCellInterface instanceof CellExposedInterface && type == CellType.NORMAL) {
            return ((CellExposedInterface) sectionCellInterface).getExposeScope(postion.first, postion.second);
        }

        if (sectionCellInterface instanceof ExtraCellExposedInterface && (type == CellType.HEADER || type == CellType.FOOTER)) {
            return ((ExtraCellExposedInterface) sectionCellInterface).getExtraCellExposeScope(postion.first, type);
        }

        return null;
    }

    public void stopExposedDispatcher() {
        innerInfos.clear();
        adapterMap.clear();
        dispatcher.finishExposed();
    }

    public void pauseExposedDispatcher() {
        innerInfos.clear();
        adapterMap.clear();
        dispatcher.pauseExposed();
//        moveStatusDispatcher.stopDispatch();
    }

    public void stopMoveStatusDispatch() {
        moveStatusDispatcher.stopDispatch();
    }

    public void storeMoveStatusMap() {
        if (!adapterMap.isEmpty()) {
            adapterStatusMap = (HashMap<PieceAdapter, AdapterExposedList>) adapterMap.clone();
        }
    }

    public void clearMoveStatusMap() {
        adapterStatusMap.clear();
    }

    public void dispatchAgentDisappearWithLifecycle(ScrollDirection direction) {
        //一遍循环后 旧tempMap剩下的就是SC_PART_TO_END和SC_FULL_TO_END
        for (Map.Entry<PieceAdapter, AdapterExposedList> entry : adapterMap.entrySet()) {
            SectionCellInterface sectionCellInterface = entry.getKey().getSectionCellInterface();
            if (sectionCellInterface instanceof MoveStatusInterface) {
                dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, false, true);
            }
        }
    }

    public void dispatchAgentAappearWithLifecycle(ScrollDirection direction) {
        //一遍循环后 旧tempMap剩下的就是SC_NEW_FULL和SC_NEW_PART
        for (Map.Entry<PieceAdapter, AdapterExposedList> entry : adapterStatusMap.entrySet()) {
            SectionCellInterface sectionCellInterface = entry.getKey().getSectionCellInterface();
            if (sectionCellInterface instanceof MoveStatusInterface) {
                AdapterExposedList newList = entry.getValue();
                if (newList.partExposedList.isEmpty()
                        && newList.completeExposedList.size() == entry.getKey().getItemCount()) {
                    //SC_NEW_FULL
                    //先调移入屏幕回调
                    dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, true, true);
                    dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, true, true);
                    //列表内全为NEW_FULL
                } else {
                    //SC_NEW_PART
                    //先调移入屏幕回调
                    dispatchCellMove(sectionCellInterface, ExposeScope.PX, direction, -1, -1, null, true, true);
                    //FULL列表内全为NEW_FULL，PART列表内全为NEW_PART
                }
//                dispatchCellMove(sectionCellInterface, ExposeScope.COMPLETE, direction, -1, -1, null, true, true);
                if (direction == ScrollDirection.PAGE_RESUME) {
                    resumeStatusMap.put(entry.getKey(),entry.getValue());
                }
            }
        }
        if (direction == ScrollDirection.PAGE_RESUME) {
            isPageResumed = true;
        }
    }


    public ArrayList<ExposedInfo> getInnerInfos() {
        return innerInfos;
    }

    private void dispatchCellMove(SectionCellInterface sci, ExposeScope scope, ScrollDirection direction, int section, int row, CellType type, boolean isAppear, boolean isSCI) {
        if (sci instanceof MoveStatusInterface && isSCI) {
            MoveStatusAction action = new MoveStatusAction(scope, direction, section, row, type, isAppear, isSCI);
            action.moveStatusInterface = (MoveStatusInterface) sci;
            moveStatusDispatcher.moveAction(action);
        } else if (type == CellType.NORMAL && sci instanceof CellMoveStatusInterface && !isSCI) {
            MoveStatusAction action = new MoveStatusAction(scope, direction, section, row, type, isAppear, isSCI);
            action.cellMoveStatusInterface = (CellMoveStatusInterface) sci;
            moveStatusDispatcher.moveAction(action);
        } else if (type != CellType.NORMAL && sci instanceof ExtraCellMoveStatusInterface && !isSCI) {
            MoveStatusAction action = new MoveStatusAction(scope, direction, section, row, type, isAppear, isSCI);
            action.extraCellMoveStatusInterface = (ExtraCellMoveStatusInterface) sci;
            moveStatusDispatcher.moveAction(action);
        }

    }
}
