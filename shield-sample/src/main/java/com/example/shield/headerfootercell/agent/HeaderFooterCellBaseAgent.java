package com.example.shield.headerfootercell.agent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.dianping.shield.viewcell.BaseViewCell;

/**
 * Created by nihao on 2017/7/14.
 */

public abstract class HeaderFooterCellBaseAgent extends LightAgent {

    private BaseViewCell headerFooterViewCell;

    public HeaderFooterCellBaseAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerFooterViewCell = getHeaderFooterViewCell();
        headerFooterViewCell.setOnItemClickListener(new ItemClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int section, int row) {
                StringBuilder sb = new StringBuilder();
                sb.append("module");
                sb.append(getModuleIndex());
                sb.append(",");
                sb.append("section");
                sb.append(section);
                sb.append(",");
                sb.append("row");
                sb.append(row);
                sb.append(",");
                sb.append("clicked");
                Toast.makeText(getContext(), sb, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected abstract int getModuleIndex();

    protected abstract BaseViewCell getHeaderFooterViewCell();

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return headerFooterViewCell;
    }
}
