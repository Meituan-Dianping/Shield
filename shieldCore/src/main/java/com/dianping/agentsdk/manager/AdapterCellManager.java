package com.dianping.agentsdk.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dianping.agentsdk.adapter.MergeAdapter;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.core.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hezhi on 15/12/11.
 */
public class AdapterCellManager implements CellManagerInterface<ListView> {

    public final static Handler handler = new Handler(Looper.getMainLooper());
    protected ListView listView;
    private boolean isSetList;
    protected MergeAdapter mergeAdapter;

    protected Context mContext;

    private final Runnable notifyCellChanged = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            updateAgentContainer();
        }

    };

    protected final HashMap<String, Cell> cells = new HashMap<String, Cell>();

    protected static final Comparator<Cell> cellComparator = new Comparator<Cell>() {
        @Override
        public int compare(Cell lhs, Cell rhs) {
            return lhs.owner.getIndex().equals(rhs.owner.getIndex()) ? lhs.name.compareTo(rhs.name)
                    : lhs.owner.getIndex().compareTo(rhs.owner.getIndex());
        }
    };

    public AdapterCellManager(Context mContext) {
        this.mContext = mContext;
        mergeAdapter = new MergeAdapter();
    }

    public void updateAgentContainer() {
        ArrayList<Cell> sort = new ArrayList<Cell>(cells.values());
        Collections.sort(sort, cellComparator);
        resetAgentContainerView();
        for (Cell c : sort) {
            if (c.adpater == null)
                continue;

//            String host = agentHolderInterface.findHostForAgent(c.owner);
//            if (TextUtils.isEmpty(host)) {
//                return;
//            }
            addCellToContainerView(c);
        }
        mergeAdapter.notifyDataSetChanged();

        if (isSetList) {
            listView.setAdapter(mergeAdapter);
            isSetList = false;
        }
    }

    public ViewGroup getAgentContainerView() {
        return this.listView;
    }

    public void resetAgentContainerView() {
        mergeAdapter.clear();
    }

    public void addCellToContainerView(Cell cell) {
        mergeAdapter.addAdapter(cell.adpater);
    }

    @Override
    public void setAgentContainerView(ListView containerView) {
        isSetList = true;
        this.listView = containerView;
    }

    @Override
    public void notifyCellChanged() {
        handler.removeCallbacks(notifyCellChanged);
        handler.post(notifyCellChanged);
    }

    @Override
    public void updateAgentCell(AgentInterface agent) {
        Cell targetCell = findCellForAgent(agent);
        if (targetCell != null && targetCell.adpater != null && targetCell.adpater instanceof BaseAdapter) {
            ((BaseAdapter) targetCell.adpater).notifyDataSetChanged();
        }
    }

    @Override
    public void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList) {
        if (addList != null && !addList.isEmpty()) {
            //添加新的
            for (AgentInterface addCell : addList) {
                if (addCell.getSectionCellInterface() != null) {
                    ListAdapter adapter;
                    SectionCellInterface cellInterface = addCell.getSectionCellInterface();
                    adapter = createListAdapter(cellInterface);
                    Cell c = new Cell();
                    c.owner = addCell;
                    c.name = addCell.getAgentCellName();
                    c.adpater = adapter;
                    cells.put(getCellName(addCell), c);
                }
            }
        }
        //更新原来有的位置
        //只更新之前存在的Agent的Cell的index
        //因为是viewgroup会有多个cell,需要把agent对应的多个cell的index都更新,之后统一notify
        if (updateList != null && !updateList.isEmpty()) {
            HashMap<String, Cell> copyOfCells = (HashMap<String, Cell>) cells.clone();
            for (AgentInterface updateCell : updateList) {
                if (updateCell.getSectionCellInterface() != null) {
                    for (Map.Entry<String, Cell> entry : copyOfCells.entrySet()) {
                        //判断owner属于该agent,并且之前的name是和目前cellNam+内部顺序一致(找到对应的Cell)
                        if (entry.getValue().owner == updateCell) {
                            //替换cell的index
                            Cell temp = entry.getValue();
                            cells.remove(entry.getKey());
                            cells.put(getCellName(updateCell), temp);
                        }
                    }
                }
            }
        }
        //删除需要删除的
        if (deleteList != null && !deleteList.isEmpty()) {
            for (AgentInterface deleteCell : deleteList) {
                Iterator<Map.Entry<String, Cell>> itr = cells.entrySet().iterator();
                while (itr.hasNext()) {
                    Cell c = itr.next().getValue();
                    if (c.owner == deleteCell) {
                        itr.remove();
                    }
                }
            }
        }
        notifyCellChanged();
    }

    public void addAgentCell(AgentInterface agent) {
        if (agent.getSectionCellInterface() != null) {
            ListAdapter adapter;
            SectionCellInterface cellInterface = agent.getSectionCellInterface();
            adapter = createListAdapter(cellInterface);
            Cell c = new Cell();
            c.owner = agent;
            c.name = agent.getAgentCellName();
            c.adpater = adapter;
            cells.put(getCellName(agent), c);
        }
        notifyCellChanged();
    }

    public void removeAllCells(AgentInterface agent) {
        Iterator<Map.Entry<String, Cell>> itr = cells.entrySet().iterator();
        while (itr.hasNext()) {
            Cell c = itr.next().getValue();
            if (c.owner == agent) {
                itr.remove();
            }
        }
        notifyCellChanged();
    }

    protected ListAdapter createListAdapter(final SectionCellInterface sectionCellInterface) {
        ListAdapter adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                int count = 0;
                for (int i = 0; i < sectionCellInterface.getSectionCount(); i++) {
                    count = count + sectionCellInterface.getRowCount(i);
                }
                return count;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return sectionCellInterface.getViewTypeCount();
            }

            @Override
            public int getItemViewType(int position) {
                int rowPosition = position;
                int sectionPosition = 0;
                for (int i = 0; i < sectionCellInterface.getSectionCount(); i++) {
                    if (rowPosition < sectionCellInterface.getRowCount(i)) {
                        sectionPosition = i;
                        break;
                    } else {
                        rowPosition = rowPosition - sectionCellInterface.getRowCount(i);
                    }
                }
                return sectionCellInterface.getViewType(sectionPosition, rowPosition);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null || convertView.getTag(R.id.adapter_cell_tag_id) == null ||
                        getItemViewType(position) != (int) convertView.getTag(R.id.adapter_cell_tag_id)) {
                    view = sectionCellInterface.onCreateView(parent, getItemViewType(position));
                    view.setTag(R.id.adapter_cell_tag_id, getItemViewType(position));
                } else {
                    view = convertView;
                }
                int rowPositon = position;
                for (int i = 0; i < sectionCellInterface.getSectionCount(); i++) {
                    if (rowPositon < sectionCellInterface.getRowCount(i)) {
                        sectionCellInterface.updateView(view, i, rowPositon, parent);
                        break;
                    } else {
                        rowPositon = rowPositon - sectionCellInterface.getRowCount(i);
                    }
                }

                return view;
            }
        };
        return adapter;
    }


    public Cell findCellForAgent(AgentInterface c) {
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            if (c == entry.getValue().owner) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected String getCellName(AgentInterface agent) {
        return TextUtils.isEmpty(agent.getIndex()) ? agent.getAgentCellName() : agent.getIndex() + agent.getAgentCellName();
    }

}
