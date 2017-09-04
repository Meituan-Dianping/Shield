package com.dianping.agentsdk.framework;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by hezhi on 16/1/26.
 */
public interface AgentInterface {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    Bundle saveInstanceState();

    @Deprecated
    void onAgentChanged(Bundle data);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    String getIndex();

    void setIndex(String index);

    String getHostName();

    void setHostName(String hostName);

    SectionCellInterface getSectionCellInterface();

    String getAgentCellName();
}
