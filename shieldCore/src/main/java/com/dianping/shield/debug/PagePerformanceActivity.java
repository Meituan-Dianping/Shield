package com.dianping.shield.debug;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dianping.shield.core.R;

public class PagePerformanceActivity extends AppCompatActivity {

    private String pageName;
    private ListView listView;
    private Button button;
    private PerformanceAdapter adapter;
    private PerformanceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_performance);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            pageName = extras.getString("pagename");
        }

        manager = new PerformanceManager(getBaseContext());

        Button button = new Button(getBaseContext());
        button.setText("清除 " + pageName + " 数据");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.clearData(pageName);
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.activity_page_performance);
        listView.addHeaderView(button);
        adapter = new PerformanceAdapter(getBaseContext(), manager.searchPage(pageName), true);
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
        if (adapter != null) {
            adapter.swapCursor(manager.searchPage(pageName));
        }
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
                StringBuilder builder = new StringBuilder();
                builder.append(PerformanceManager.PerfEntry.HOST_NAME).append(": ").append(cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.HOST_NAME))).append("\n");
                builder.append(PerformanceManager.PerfEntry.PAGE_NAME).append(": ").append(cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.PAGE_NAME))).append("\n");
                builder.append(PerformanceManager.PerfEntry.AGENT_NAME).append(": ").append(cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.AGENT_NAME))).append("\n");
                builder.append(PerformanceManager.PerfEntry.AGENT_HASH_CODE).append(": ").append(cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.AGENT_HASH_CODE))).append("\n");
                builder.append(PerformanceManager.PerfEntry.CELL_NAME).append(": ").append(cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.CELL_NAME))).append("\n");
                String methodName = cursor.getString(cursor.getColumnIndex(PerformanceManager.PerfEntry.METHOD_NAME));
                builder.append(PerformanceManager.PerfEntry.METHOD_NAME).append(": ").append(methodName).append("\n");
                long timeCost = cursor.getLong(cursor.getColumnIndex("TimeCost"));
                int runTimes = cursor.getInt(cursor.getColumnIndex("RunTimes"));
                float avgTime = cursor.getFloat(cursor.getColumnIndex("AvgTime"));
                builder.append("TimeCost").append(": ").append(timeCost).append("\n");
                builder.append("RunTimes").append(": ").append(runTimes).append("\n\n");
                builder.append("AvgTimeCost").append(": ").append(avgTime);

                view.setBackgroundColor(Color.WHITE);

                if ("onCreateView".equals(methodName)) {

                    if (avgTime > 128) {
                        view.setBackgroundColor(Color.parseColor("#ffa500"));
                    } else if (avgTime > 64) {
                        view.setBackgroundColor(Color.parseColor("#ffb732"));
                    } else if (avgTime > 32) {
                        view.setBackgroundColor(Color.parseColor("#ffc966"));
                    } else if (avgTime > 16) {
                        view.setBackgroundColor(Color.parseColor("#ffdb99"));
                    }

                } else if ("updateView".equals(methodName)) {
                    if (avgTime > 32) {
                        view.setBackgroundColor(Color.parseColor("#ff0000"));
                    } else if (avgTime > 16) {
                        view.setBackgroundColor(Color.RED);
                        view.setBackgroundColor(Color.parseColor("#ff1919"));
                    } else if (avgTime > 8) {
                        view.setBackgroundColor(Color.parseColor("#ff4c4c"));
                    } else if (avgTime > 4) {
                        view.setBackgroundColor(Color.parseColor("#ff7f7f"));
                    }
                }
                ((TextView) view).setText(builder.toString());
            }
        }
    }
}
