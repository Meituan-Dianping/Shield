package com.example.shield.linktype.agent;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;

/**
 * Created by nihao on 2017/8/22.
 */

public class LinkTypeCustomAgent extends LightAgent {
    private static final int SECTION_COUNT = 3;
    private static final int ROW_COUNT = 1;
    private static final int VIEW_TYPE_COUNT = 1;

    public LinkTypeCustomAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return new LinkTypeCustomCell(getContext());
    }

    private class LinkTypeCustomCell extends BaseViewCell {

        private SparseArray<LinkState> stateMap = new SparseArray<>();

        LinkTypeCustomCell(Context context) {
            super(context);
            stateMap.put(0, new LinkState(LinkType.Previous.DEFAULT, LinkType.Next.DEFAULT));
            stateMap.put(1, new LinkState(LinkType.Previous.DEFAULT, LinkType.Next.DEFAULT));
            stateMap.put(2, new LinkState(LinkType.Previous.DEFAULT, LinkType.Next.DEFAULT));
        }

        @Override
        public int getSectionCount() {
            return SECTION_COUNT;
        }

        @Override
        public int getRowCount(int sectionPosition) {
            return ROW_COUNT;
        }

        @Override
        public int getViewType(int sectionPosition, int rowPosition) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public LinkType.Next linkNext(int sectionPosition) {
            return stateMap.get(sectionPosition).next;
        }

        @Override
        public LinkType.Previous linkPrevious(int sectionPosition) {
            return stateMap.get(sectionPosition).previous;
        }

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_linktype_custom, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.titleTx = (TextView) view.findViewById(R.id.custom_linktype_title);
            holder.preTx = (TextView) view.findViewById(R.id.custom_linktype_pre);
            holder.nextTx = (TextView) view.findViewById(R.id.custom_linktype_next);
            holder.preTx.setText("DEFAULT");
            holder.nextTx.setText("DEFAULT");
            view.setTag(holder);
            return view;
        }

        @Override
        public void updateView(View view, final int sectionPosition, int rowPosition, ViewGroup parent) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.titleTx.setText(getTitle(sectionPosition, rowPosition));

            holder.preTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinkState state = stateMap.get(sectionPosition);
                    switch (state.previous) {
                        case DEFAULT:
                            state.previous = LinkType.Previous.LINK_TO_PREVIOUS;
                            holder.preTx.setText("LINK_TO_PREVIOUS");
                            updateAgentCell();
                            break;
                        case DISABLE_LINK_TO_PREVIOUS:
                            state.previous = LinkType.Previous.DEFAULT;
                            holder.preTx.setText("DEFAULT");
                            updateAgentCell();
                            break;
                        case LINK_TO_PREVIOUS:
                            state.previous = LinkType.Previous.DISABLE_LINK_TO_PREVIOUS;
                            holder.preTx.setText("DISABLE_LINK_TO_PREVIOUS");
                            updateAgentCell();
                            break;
                    }
                }
            });

            holder.nextTx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinkState state = stateMap.get(sectionPosition);
                    switch (state.next) {
                        case DEFAULT:
                            state.next = LinkType.Next.LINK_TO_NEXT;
                            holder.nextTx.setText("LINK_TO_NEXT");
                            updateAgentCell();
                            break;
                        case DISABLE_LINK_TO_NEXT:
                            state.next = LinkType.Next.DEFAULT;
                            holder.nextTx.setText("DEFAULT");
                            updateAgentCell();
                            break;
                        case LINK_TO_NEXT:
                            state.next = LinkType.Next.DISABLE_LINK_TO_NEXT;
                            holder.nextTx.setText("DISABLE_LINK_TO_NEXT");
                            updateAgentCell();
                            break;
                    }
                }
            });
        }

        private String getTitle(int sectionPosition, int rowPosition) {
            return "section : " + sectionPosition + " row : " + rowPosition;
        }
    }

    private class ViewHolder {
        private TextView titleTx;
        private TextView preTx;
        private TextView nextTx;
    }

    private class LinkState {
        private LinkType.Previous previous;
        private LinkType.Next next;

        private LinkState(LinkType.Previous pre, LinkType.Next next) {
            this.previous = pre;
            this.next = next;
        }
    }
}
