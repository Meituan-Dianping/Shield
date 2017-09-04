package com.example.shield.headerfootercell.agent;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.headerfootercell.cell.HeaderFooterViewFirstCell;


/**
 * 模块Agent.
 * Created by nihao on 2017/7/13.
 */
public class HeaderFooterCellFirstAgent extends HeaderFooterCellBaseAgent {

    public HeaderFooterCellFirstAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    protected BaseViewCell getHeaderFooterViewCell() {
        return new HeaderFooterViewFirstCell(getContext());
    }

    @Override
    protected int getModuleIndex() {
        return 0;
    }
}
