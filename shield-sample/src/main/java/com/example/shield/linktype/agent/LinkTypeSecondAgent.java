package com.example.shield.linktype.agent;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.linktype.cell.LinkTypeSecondCell;

/**
 * Created by nihao on 2017/7/17.
 */

public class LinkTypeSecondAgent extends LightAgent {
    public LinkTypeSecondAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return new LinkTypeSecondCell(getContext());
    }
}
