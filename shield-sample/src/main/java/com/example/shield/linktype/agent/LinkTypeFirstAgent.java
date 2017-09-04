package com.example.shield.linktype.agent;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.linktype.cell.LinkTypeFirstCell;

/**
 * Created by nihao on 2017/7/14.
 */

public class LinkTypeFirstAgent extends LightAgent {

    public LinkTypeFirstAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return new LinkTypeFirstCell(getContext());
    }
}
