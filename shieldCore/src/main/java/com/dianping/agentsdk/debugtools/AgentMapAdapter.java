package com.dianping.agentsdk.debugtools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dianping.shield.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianhe.dong on 2017/7/18.
 * email xianhe.dong@dianping.com
 */

public class AgentMapAdapter extends BaseAdapter {

    private List<AgentMapListItemModel> list = new ArrayList<>();
    private Context context;

    public AgentMapAdapter(Context context, List<AgentMapListItemModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        AgentMapListItemModel agentMapListItemModel = (AgentMapListItemModel) getItem(i);
        if (convertView == null) {
            convertView = initView(viewGroup);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.agentKeyText.setText(agentMapListItemModel.key);
        viewHolder.agentClassNameText.setText(agentMapListItemModel.agentClassName);
        return convertView;
    }

    private View initView(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.agentmap_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.agentKeyText = (TextView) view.findViewById(R.id.agentmap_item_agentkey);
        viewHolder.agentClassNameText = (TextView) view.findViewById(R.id.agentmap_item_agent_classname);
        view.setTag(viewHolder);
        return view;
    }

    class ViewHolder {
        TextView agentKeyText;
        TextView agentClassNameText;
    }
}
