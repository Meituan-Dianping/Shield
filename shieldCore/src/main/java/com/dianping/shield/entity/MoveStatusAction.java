package com.dianping.shield.entity;

import com.dianping.shield.feature.CellMoveStatusInterface;
import com.dianping.shield.feature.ExtraCellMoveStatusInterface;
import com.dianping.shield.feature.MoveStatusInterface;

/**
 * Created by hezhi on 17/4/6.
 */

public class MoveStatusAction {
    public MoveStatusInterface moveStatusInterface;
    public CellMoveStatusInterface cellMoveStatusInterface;
    public ExtraCellMoveStatusInterface extraCellMoveStatusInterface;
    public ExposeScope scope;
    public ScrollDirection direction;
    public int section;
    public int row;
    public CellType cellType;
    public boolean isAppear; //ture:appear,false:disappear
    public boolean isSCI;

    public MoveStatusAction(ExposeScope scope, ScrollDirection direction, int section, int row, CellType cellType, boolean isAppear, boolean isSCI) {
        this.scope = scope;
        this.direction = direction;
        this.section = section;
        this.row = row;
        this.cellType = cellType;
        this.isAppear = isAppear;
        this.isSCI = isSCI;
    }
}
