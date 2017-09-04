package com.example.shield.mix.agent;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.mix.cell.MixLoadingCell;

/**
 * Created by nihao on 2017/7/18.
 */
public class MixLoadingAgent extends LightAgent implements MixLoadingCell.MixLoadingListener {
    public static final String KEY_LOADING = "loading";
    public static final String KEY_EMPTY = "empty";
    public static final String KEY_FAILED = "failed";
    public static final String KEY_MORE = "more";
    public static final String KEY_DONE = "done";

    private MixLoadingCell mixLoadingCell;

    public MixLoadingAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
        mixLoadingCell = new MixLoadingCell(getContext());
        mixLoadingCell.setOnMixLoadingListener(this);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mixLoadingCell;
    }

    @Override
    public void onLoading() {
        getWhiteBoard().putBoolean(KEY_LOADING, true);
    }

    @Override
    public void onEmpty() {
        getWhiteBoard().putBoolean(KEY_EMPTY, true);
    }

    @Override
    public void onFailed() {
        getWhiteBoard().putBoolean(KEY_FAILED, true);
    }

    @Override
    public void onMore() {
        getWhiteBoard().putBoolean(KEY_MORE, true);
    }

    @Override
    public void onDone() {
        getWhiteBoard().putBoolean(KEY_DONE, true);
    }
}
