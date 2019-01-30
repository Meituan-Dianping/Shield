package com.dianping.agentsdk.sectionrecycler.divider;

import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.shield.entity.CellType;

public interface DividerInfoInterface {

    DividerInfo getDividerInfo(CellType cellType, int section, int row);

}
