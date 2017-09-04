package com.dianping.agentsdk.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.framework.CellManagerInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hezhi on 15/12/11.
 */
public class ViewGroupCellManager implements CellManagerInterface<ViewGroup> {
    public final static Handler handler = new Handler(Looper.getMainLooper());
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


    private Context mContext;

    private ViewGroup agentContainerView;

    public ViewGroupCellManager(Context mContext) {
        this.mContext = mContext;
    }

    public void updateAgentContainer() {
        ArrayList<Cell> sort = new ArrayList<Cell>(cells.values());
        Collections.sort(sort, cellComparator);
        resetAgentContainerView();
        for (Cell c : sort) {
//            String host = agentHolderInterface.findHostForAgent(c.owner);
//            if (TextUtils.isEmpty(host)) {
//                return;
//            }
            addCellToContainerView(c);
        }
    }

    public ViewGroup getAgentContainerView() {
        return agentContainerView;
    }

    public void resetAgentContainerView() {
        agentContainerView.removeAllViews();
    }

    public void addCellToContainerView(Cell cell) {
        agentContainerView.addView(cell.view);
    }

    @Override
    public void setAgentContainerView(ViewGroup containerView) {
        agentContainerView = containerView;
    }

    @Override
    public void notifyCellChanged() {
        handler.removeCallbacks(notifyCellChanged);
        handler.post(notifyCellChanged);
    }

    @Override
    public void updateAgentCell(AgentInterface agent) {

        Iterator<Map.Entry<String, Cell>> itr = cells.entrySet().iterator();
        while (itr.hasNext()) {
            Cell c = itr.next().getValue();
            if (c.owner == agent) {
                itr.remove();
            }
        }

        if (agent.getSectionCellInterface() != null) {
            for (int sectionPosition = 0; sectionPosition < agent.getSectionCellInterface().getSectionCount(); sectionPosition++) {
                for (int rowPosition = 0; rowPosition < agent.getSectionCellInterface().getRowCount(sectionPosition); rowPosition++) {
                    Cell c = new Cell();
                    c.owner = agent;

                    String sectionName = addZeroPrefix(sectionPosition);
                    String rowName = addZeroPrefix(rowPosition);
                    //Map key  外部index加上内部name
                    String cellNameSwp = getCellName(agent, agent.getAgentCellName() + sectionName + "." + rowName);
                    //内部name 排序先比较外部index再比较内部name
                    c.name = agent.getAgentCellName() + sectionName + "." + rowName;
                    c.view = agent.getSectionCellInterface().onCreateView(null, agent.getSectionCellInterface().getViewType(sectionPosition, rowPosition));
                    agent.getSectionCellInterface().updateView(c.view, sectionPosition, rowPosition, null);
                    cells.put(cellNameSwp, c);
                }
            }
        }

        notifyCellChanged();
    }

    public void addAgentCell(AgentInterface agent) {
        if (agent.getSectionCellInterface() != null) {
            for (int sectionPosition = 0; sectionPosition < agent.getSectionCellInterface().getSectionCount(); sectionPosition++) {
                for (int rowPosition = 0; rowPosition < agent.getSectionCellInterface().getRowCount(sectionPosition); rowPosition++) {
                    //Map key  外部index加上内部name
                    Cell c = new Cell();
                    c.owner = agent;

                    String sectionName = addZeroPrefix(sectionPosition);
                    String rowName = addZeroPrefix(rowPosition);
                    //Map key  外部index加上内部name
                    String cellNameSwp = getCellName(agent, agent.getAgentCellName() + sectionName + "." + rowName);
                    //内部name 排序先比较外部index再比较内部name
                    c.name = agent.getAgentCellName() + sectionName + "." + rowName;
                    c.view = agent.getSectionCellInterface().onCreateView(null, agent.getSectionCellInterface().getViewType(sectionPosition, rowPosition));
                    agent.getSectionCellInterface().updateView(c.view, sectionPosition, rowPosition, null);
                    cells.put(cellNameSwp, c);
                    addCellToContainerView(c);
                }
            }
        }
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

    //在agentlist发生变化时或resetagents时会被调用,更新完后会notifycellchange
    @Override
    public void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList) {
        if (addList != null && !addList.isEmpty()) {
            //添加新的
            for (AgentInterface addCell : addList) {
                if (addCell.getSectionCellInterface() != null) {
                    for (int sectionPosition = 0; sectionPosition < addCell.getSectionCellInterface().getSectionCount(); sectionPosition++) {
                        for (int rowPosition = 0; rowPosition < addCell.getSectionCellInterface().getRowCount(sectionPosition); rowPosition++) {
                            Cell c = new Cell();
                            c.owner = addCell;
                            String sectionName = addZeroPrefix(sectionPosition);
                            String rowName = addZeroPrefix(rowPosition);
                            //Map key  外部index加上内部name
                            String cellNameSwp = getCellName(addCell, addCell.getAgentCellName() + sectionName + "." + rowName);

                            //内部name 排序先比较外部index再比较内部name
                            c.name = addCell.getAgentCellName() + sectionName + "." + rowName;
                            c.view = addCell.getSectionCellInterface().onCreateView(null, addCell.getSectionCellInterface().getViewType(sectionPosition, rowPosition));
                            addCell.getSectionCellInterface().updateView(c.view, sectionPosition, rowPosition, null);
                            cells.put(cellNameSwp, c);
                        }
                    }
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
                    //现在是三重循环了,
                    for (int sectionPosition = 0; sectionPosition < updateCell.getSectionCellInterface().getSectionCount(); sectionPosition++) {
                        for (int rowPosition = 0; rowPosition < updateCell.getSectionCellInterface().getRowCount(sectionPosition); rowPosition++) {
//                        for (int i = 0; i < updateCell.getSectionCellInterface().getViewCount(); i++) {
                            //因为每个cell都要找一遍,所以是双重循环
                            for (Map.Entry<String, Cell> entry : copyOfCells.entrySet()) {
                                //获取当前Cell的加零Name
                                String sectionName = addZeroPrefix(sectionPosition);
                                String rowName = addZeroPrefix(rowPosition);

                                //判断owner属于该agent,并且之前的name是和目前cellNam+内部顺序一致(找到对应的Cell)
                                if (entry.getValue().owner == updateCell && entry.getValue().name == updateCell.getAgentCellName() + sectionName + "." + rowName) {
                                    //替换cell的index
                                    Cell temp = entry.getValue();
                                    cells.remove(entry.getKey());
                                    cells.put(getCellName(updateCell, temp.name), temp);
                                }
                            }
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

    protected String getCellName(AgentInterface agent, String name) {
        return TextUtils.isEmpty(agent.getIndex()) ? name : agent.getIndex() + name;
    }


    public void addCell(AgentInterface agentInterface, String name, View view) {
        String cellName = getCellName(agentInterface, name);
        Cell c = new Cell();
        c.owner = agentInterface;
        c.name = name;
        c.view = view;
        cells.put(cellName, c);

        notifyCellChanged();
    }


    public boolean hasCell(AgentInterface cellAgent, String name) {
        Cell c = cells.get(getCellName(cellAgent, name));
        if (c == null)
            return false;
        return cellAgent == null || c.owner == cellAgent;
    }

    public void removeCell(AgentInterface cellAgent, String name) {
        if (hasCell(cellAgent, name)) {
            cells.remove(getCellName(cellAgent, name));
            notifyCellChanged();
        }
    }

    private String addZeroPrefix(int num) {
        int j = ((int) Math.log10(num) + 1);
        String zeroStr = "";
        for (int k = 0; k < 6 - j; k++) {
            zeroStr = zeroStr + "0";
        }
        return zeroStr + num;
    }

    public Cell getFirstCellForAgent(AgentInterface agent) {
        ArrayList<Cell> sort = new ArrayList<Cell>(cells.values());
        Collections.sort(sort, cellComparator);
        for (Cell c : sort) {
            if (c.owner == agent) {
                return c;
            }
        }
        return null;
    }
}
