package com.example.shield.status.cell;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.shield.viewcell.BaseViewCell;

/**
 * Created by bingweizhou on 17/7/17.
 */

public abstract class LoadingBaseCell extends BaseViewCell {
    private View.OnClickListener mLoadingRetryListener;
    private View.OnClickListener mLoadingMoreRetryListener;
    private View.OnClickListener mResetOnClickListener;

    public LoadingBaseCell(Context context) {
        super(context);
    }

    @Override
    public View loadingView() {
        LinearLayout rootView = (LinearLayout) getRootView();
        TextView textView = new TextView(mContext);
        textView.setHeight(ViewUtils.dip2px(getContext(), 50));
        textView.setText("loading: 加载中");
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mResetOnClickListener != null) {
                    mResetOnClickListener.onClick(v);
                }
            }
        });
        rootView.addView(textView);
        return rootView;
    }

    @Override
    public View loadingFailedView() {
        LinearLayout rootView = (LinearLayout) getRootView();
        TextView textView = new TextView(mContext);
        textView.setHeight(ViewUtils.dip2px(getContext(), 50));
        textView.setText("loading fail: 点击重新加载");
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingRetryListener != null) {
                    mLoadingRetryListener.onClick(v);
                }
            }
        });
        rootView.addView(textView);
        return rootView;
    }

    @Override
    public View emptyView() {
        LinearLayout rootView = (LinearLayout) getRootView();
        TextView textView = new TextView(mContext);
        textView.setHeight(ViewUtils.dip2px(getContext(), 50));
        textView.setText("loading empty: 您查询的内容为空");
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResetOnClickListener != null) {
                    mResetOnClickListener.onClick(v);
                }
            }
        });
        rootView.addView(textView);
        return rootView;
    }

    @Override
    public View loadingMoreView() {

        LinearLayout rootView = (LinearLayout) getRootView();

        TextView textView = new TextView(mContext);
        textView.setHeight(ViewUtils.dip2px(getContext(), 50));
        textView.setText("loadingmore: 正在载入");
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResetOnClickListener != null) {
                    mResetOnClickListener.onClick(v);
                }
            }
        });
        rootView.addView(textView);
        return rootView;
    }

    @Override
    public View loadingMoreFailedView() {

        LinearLayout rootView = (LinearLayout) getRootView();
        TextView textView = new TextView(mContext);
        textView.setHeight(ViewUtils.dip2px(getContext(), 50));
        textView.setText("loadingmore fail: 点击重新加载");
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingMoreRetryListener != null) {
                    mLoadingMoreRetryListener.onClick(v);
                }
            }
        });
        rootView.addView(textView);
        return rootView;
    }


    private ViewGroup getRootView() {
        LinearLayout rootView = new LinearLayout(mContext);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(ViewUtils.dip2px(getContext(), 30), 0, 0, 0);
        rootView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        return rootView;
    }

    public void setResetOnClickListener(View.OnClickListener resetOnClickListener) {
        this.mResetOnClickListener = resetOnClickListener;
    }

    public void setLoadingRetryListener(View.OnClickListener loadingRetryListener) {
        this.mLoadingRetryListener = loadingRetryListener;
    }


    public void setLoadingMoreRetryListener(View.OnClickListener loadingMoreRetryListener) {
        this.mLoadingMoreRetryListener = loadingMoreRetryListener;
    }
}
