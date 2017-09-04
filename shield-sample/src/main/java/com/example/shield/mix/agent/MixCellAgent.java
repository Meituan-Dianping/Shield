package com.example.shield.mix.agent;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.mix.cell.MixCell;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by nihao on 2017/7/17.
 */

public class MixCellAgent extends LightAgent {
    private MixCell mixCell;
    private Subscription loadingSubscription;
    private Subscription emptySubscription;
    private Subscription failedSubscription;
    private Subscription moreSubscription;

    public MixCellAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mixCell = new MixCell(getContext(), this);
        loadingSubscription = getWhiteBoard().getObservable(MixLoadingAgent.KEY_LOADING).filter(new Func1() {
            @Override
            public Object call(Object o) {
                return o instanceof Boolean && ((Boolean) o);
            }
        }).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                loading();
            }
        });

        emptySubscription = getWhiteBoard().getObservable(MixLoadingAgent.KEY_EMPTY).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return o instanceof Boolean && ((Boolean) o);
            }
        }).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                mixCell.onEmpty();
            }
        });

        failedSubscription = getWhiteBoard().getObservable(MixLoadingAgent.KEY_FAILED).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return o instanceof Boolean && ((Boolean) o);
            }
        }).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                mixCell.onFailed();
            }
        });

        moreSubscription = getWhiteBoard().getObservable(MixLoadingAgent.KEY_MORE).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return o instanceof Boolean && ((Boolean) o);
            }
        }).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                moring();
            }
        });
    }

    public void loading() {
        // 触发loading;
        mixCell.onLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mixCell.onDone();
            }
        }, 1000);
    }

    public void moring() {
        mixCell.onMore();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mixCell.moredata();
            }
        }, 1000);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mixCell;
    }

    @Override
    public void onDestroy() {
        if (loadingSubscription != null) {
            loadingSubscription.unsubscribe();
            loadingSubscription = null;
        }

        if (emptySubscription != null) {
            emptySubscription.unsubscribe();
        }

        if (failedSubscription != null) {
            failedSubscription.unsubscribe();
        }

        if (moreSubscription != null) {
            moreSubscription.unsubscribe();
        }
    }
}
