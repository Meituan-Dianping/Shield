package com.dianping.agentsdk.framework;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hezhi on 16/9/23.
 * 页面容器接口,提供定制化容器能力
 */
public interface PageContainerInterface<T extends ViewGroup> {

    void onCreate(Bundle savedInstanceState);

    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onActivityCreated(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    T getAgentContainerView();
}
