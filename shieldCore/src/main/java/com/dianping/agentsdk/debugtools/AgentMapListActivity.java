package com.dianping.agentsdk.debugtools;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dianping.shield.core.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xianhe.dong on 2017/7/17.
 * email xianhe.dong@dianping.com
 * 模块map列表页
 */

public class AgentMapListActivity extends AppCompatActivity {

    private HashMap<String, AgentMapListItemModel> agentMap;//key:key value:模块全路径
    private List<AgentMapListItemModel> listViewList = new ArrayList<>();

    private int search_mode_flag = SEARCH_MODE_AGENTKEY;
    private static final int SEARCH_MODE_AGENTKEY = 0;
    private static final int SEARCH_MODE_AGENTCLASSNAME = 1;

    private PopupWindow popupWindow;

    private EditText searchEditText;
    private TextView spinnerText;
    private TextView searchBtnText;
    private FrameLayout searchContent;

    private String keyWord;

    private AgentMapAdapter agentMapAdapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agentmap_list_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
//        agentMap = (HashMap<String, AgentMapListItemModel>) getIntent().getExtras().getSerializable("agentmap");
        agentMap = new HashMap<>();
        searchEditText = (EditText) findViewById(R.id.search_edit);
        spinnerText = (TextView) findViewById(R.id.spinner_text);
        searchBtnText = (TextView) findViewById(R.id.search_text);
        searchContent = (FrameLayout) findViewById(R.id.search_content);
        spinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAsDropDown(view, 20, 0);
            }
        });
        searchBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //本地缓存
                doSearch(keyWord);
                hideKeyboard(searchBtnText);
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyWord = editable.toString().trim();
            }
        });
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
                    doSearch(keyWord);
                }
                return false;
            }
        });
        initListView();
        initPopupWindow();
        generalData();
        initListData();
    }

    /**
     * 生成假数据
     */
    private void generalData() {
        for (int i = 0; i < 105; i++) {
            AgentMapListItemModel agentMapListItemModel = new AgentMapListItemModel();
            agentMapListItemModel.agentClassName = "com.dianping.shield.test.AgentCarter" + i;
            agentMapListItemModel.key = "Hello Agent" + i;
            agentMap.put("Hello Agent" + i, agentMapListItemModel);
        }
    }

    private void initListData() {
        listViewList.clear();
        for (Map.Entry<String, AgentMapListItemModel> entry : agentMap.entrySet()) {
            listViewList.add(entry.getValue());
        }
        Collections.sort(listViewList);
        if (agentMapAdapter != null) {
            agentMapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 进行搜索操作
     * @param keyWord
     */
    private void doSearch(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        listViewList.clear();
        for (Map.Entry<String, AgentMapListItemModel> entry : agentMap.entrySet()) {
            if (search_mode_flag == SEARCH_MODE_AGENTKEY) {
                String agentKey = entry.getKey();
                if (agentKey.contains(keyWord)) {
                    listViewList.add(entry.getValue());
                }
            } else if (search_mode_flag == SEARCH_MODE_AGENTCLASSNAME) {
                AgentMapListItemModel model = entry.getValue();
                if (model.agentClassName.contains(keyWord)) {
                    listViewList.add(model);
                }
            }
        }
        agentMapAdapter.notifyDataSetChanged();
    }

    private void initListView() {
        listView = new ListView(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hideKeyboard(searchBtnText);
                }
                return false;
            }
        });
        agentMapAdapter = new AgentMapAdapter(getApplicationContext(), listViewList);
        listView.setAdapter(agentMapAdapter);
        searchContent.addView(listView);
        this.listViewList.clear();
    }

    private void initPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.agentmap_list_search_popup_window, null);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        TextView shopview = (TextView) contentView.findViewById(R.id.key);
        TextView agent = (TextView) contentView.findViewById(R.id.agent);
        shopview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search_mode_flag = SEARCH_MODE_AGENTKEY;
                searchEditText.setText("");
                spinnerText.setText("键值");
                agentMapAdapter.clear();
                agentMapAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                searchEditText.setHint("搜索键值");
            }
        });
        agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_mode_flag = SEARCH_MODE_AGENTCLASSNAME;
                searchEditText.setText("");
                spinnerText.setText("类名");
                agentMapAdapter.clear();
                agentMapAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                searchEditText.setHint("搜索类名");
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.agentmap_popupwindow_bg));
    }

    /**
     * 隐藏键盘
     * @param view
     */
    private void hideKeyboard(View view) {
        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
