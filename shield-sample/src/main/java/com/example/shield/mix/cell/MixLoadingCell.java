package com.example.shield.mix.cell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;

/**
 * Created by nihao on 2017/7/18.
 */

public class MixLoadingCell extends BaseViewCell implements View.OnClickListener {
    private MixLoadingListener listener;

    public MixLoadingCell(Context context) {
        super(context);
    }

    @Override
    public int getSectionCount() {
        return 1;
    }

    @Override
    public int getRowCount(int sectionPosition) {
        return 1;
    }

    @Override
    public int getViewType(int sectionPosition, int rowPosition) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.mix_title, parent, false);
        v.findViewById(R.id.bt_loading).setOnClickListener(this);
        v.findViewById(R.id.bt_empty).setOnClickListener(this);
        v.findViewById(R.id.bt_failed).setOnClickListener(this);
        v.findViewById(R.id.bt_more).setOnClickListener(this);
        return v;
    }

    @Override
    public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {
    }

    @Override
    public void onClick(View view) {
        if (listener == null) { //监听为空;
            return;
        }
        switch (view.getId()) {
            case R.id.bt_loading:
                listener.onLoading();
                break;
            case R.id.bt_empty:
                listener.onEmpty();
                break;
            case R.id.bt_failed:
                listener.onFailed();
                break;
            case R.id.bt_more:
                listener.onMore();
                break;
        }
    }

    public void setOnMixLoadingListener(MixLoadingListener listener) {
        this.listener = listener;
    }

    public interface MixLoadingListener {
        void onLoading();

        void onEmpty();

        void onFailed();

        void onMore();

        void onDone();
    }
}
