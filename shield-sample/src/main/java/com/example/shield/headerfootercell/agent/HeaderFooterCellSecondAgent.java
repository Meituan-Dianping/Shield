package com.example.shield.headerfootercell.agent;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.headerfootercell.cell.HeaderFooterViewSecondCell;

/**
 * Created by nihao on 2017/7/14.
 */

public class HeaderFooterCellSecondAgent extends HeaderFooterCellBaseAgent {
    public HeaderFooterCellSecondAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    protected BaseViewCell getHeaderFooterViewCell() {
        return new HeaderFooterViewSecondCell(getContext());
    }

    @Override
    protected int getModuleIndex() {
        return 1;
    }
}
