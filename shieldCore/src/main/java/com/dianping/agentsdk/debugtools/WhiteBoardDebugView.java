package com.dianping.agentsdk.debugtools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.agentsdk.framework.WhiteBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by runqi.wei on 2018/1/9.
 */

public class WhiteBoardDebugView extends LinearLayout {

    protected RecyclerView recyclerView;
    protected CustomRecyclerAdapter adapter;
    protected ArrayList<String> keyList;
    protected HashMap<String, Object> dataMap;
    protected int defaultPadding = 0;

    public WhiteBoardDebugView(Context context) {
        super(context);
        init();
    }

    public WhiteBoardDebugView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhiteBoardDebugView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WhiteBoardDebugView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        defaultPadding = ViewUtils.dip2px(getContext(), 10);

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(recyclerView);
    }

    public void setData(@NonNull WhiteBoard whiteBoard) {
        dataMap = whiteBoard.getContent();
        keyList = new ArrayList<>();
        keyList.addAll(dataMap.keySet());
        Collections.sort(keyList);
        adapter.notifyDataSetChanged();
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public CustomViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }

    protected class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
            return new CustomViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {

            String key = keyList.get(position);
            Object value = dataMap.get(key);
            StringBuilder builder = new StringBuilder();
            builder.append(key).append("\n").append("\t").append(value);
            holder.textView.setText(builder.toString());
        }

        @Override
        public int getItemCount() {
            return keyList.size();
        }
    }
}
