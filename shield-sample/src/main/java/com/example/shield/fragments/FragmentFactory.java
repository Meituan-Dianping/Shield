package com.example.shield.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shield.R;
import com.example.shield.basicfeatureandclick.ClickFragment;
import com.example.shield.divider.DividerFragment;
import com.example.shield.headerfootercell.HeaderFooterCellFragment;
import com.example.shield.linktype.LinkTypeFragment;
import com.example.shield.mix.MixFragment;
import com.example.shield.status.StatusFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {
    public static final FragmentFactory instance = new FragmentFactory();
    private List<AbsExampleFragment> fragments;

    private FragmentFactory() {
        fragments = new ArrayList<>();
        fragments.add(new DividerFragment());
        fragments.add(new StatusFragment());
        fragments.add(new ClickFragment());
        fragments.add(new HeaderFooterCellFragment());
        fragments.add(new LinkTypeFragment());
        fragments.add(new MixFragment());
    }

    public ViewGroup getContainerLayout(final Fragment mainFragment) {
        LinearLayout containerLayout = new LinearLayout(mainFragment.getContext());
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        for (AbsExampleFragment fragment : fragments) {
            View v = LayoutInflater.from(mainFragment.getContext()).inflate(R.layout.module_cell_item, null);
            TextView textView = (TextView) v.findViewById(R.id.header_footer_item_tx);
            textView.setText(fragment.functionName());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            final Fragment myFragment = fragment;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = mainFragment.getFragmentManager();
                    fm.beginTransaction().hide(mainFragment).add(android.R.id.primary, myFragment, "SampleFragment")
                            .addToBackStack(null).commit();
                }
            });
            containerLayout.addView(v, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return containerLayout;
    }
}