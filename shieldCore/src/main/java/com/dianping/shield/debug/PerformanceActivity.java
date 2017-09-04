package com.dianping.shield.debug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.shield.core.R;

public class PerformanceActivity extends AppCompatActivity {

    private ListView listView;
    private PerformanceAdapter adapter;
    private PerformanceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_performance);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        manager = new PerformanceManager(getBaseContext());


        LinearLayout switchItem = (LinearLayout) getLayoutInflater().inflate(R.layout.simple_switch_item_layout, null);

        TextView switchText = (TextView) switchItem.findViewById(R.id.text_view);
        switchText.setText("开启性能监控");

        Switch logSwitch = (Switch) switchItem.findViewById(R.id.switch_view);
        SharedPreferences preferences = getSharedPreferences(MergeSectionDividerAdapter.FILE_NAME, Context.MODE_PRIVATE);
        boolean logPerformance = preferences.getBoolean(MergeSectionDividerAdapter.NEED_PERFORMANCE_KEY, false);
        logSwitch.setChecked(logPerformance);

        logSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getSharedPreferences(
                        MergeSectionDividerAdapter.FILE_NAME, MODE_PRIVATE);
                prefs.edit().putBoolean(MergeSectionDividerAdapter.NEED_PERFORMANCE_KEY, isChecked).apply();
            }
        });

        Button button = new Button(getBaseContext());
        button.setText("清除数据");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.clearData();
                refreshCursor();
            }
        });
        listView = (ListView) findViewById(R.id.activity_performance);
        listView.addHeaderView(switchItem);
        listView.addHeaderView(button);
        adapter = new PerformanceAdapter(getBaseContext(), manager.findPages(), true);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCursor();
    }

    protected void refreshCursor() {
        if (adapter != null && adapter.getCursor() != null && !adapter.getCursor().isClosed()) {
            adapter.getCursor().close();
        }
        adapter.changeCursor(manager.findPages());
    }

    @Override
    protected void onDestroy() {
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
        super.onDestroy();
    }

    public class PerformanceAdapter extends CursorAdapter {

        public PerformanceAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public PerformanceAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView tv = new TextView(context);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(20, 40, 20, 40);
            return tv;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (view instanceof TextView) {
//                StringBuilder builder = new StringBuilder();
//                for (int i = 0; i < cursor.getColumnCount(); i++) {
//                    builder.append(cursor.getColumnName(i)).append(" : ")
//                            .append(cursor.getString(i)).append("\n");
//                }
//                ((TextView) view).setText(builder.toString());
                String pageName = cursor.getString(0);
                ((TextView) view).setText(pageName);
                view.setOnClickListener(new OnPageClickListener(pageName));
            }
        }
    }

    public class OnPageClickListener implements View.OnClickListener {
        String pageName;

        public OnPageClickListener(String pageName) {
            this.pageName = pageName;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), PagePerformanceActivity.class);
            intent.putExtra("pagename", pageName);
            startActivity(intent);

        }
    }
}
