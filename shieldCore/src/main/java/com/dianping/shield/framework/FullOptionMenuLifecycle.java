package com.dianping.shield.framework;

import android.view.Menu;

/**
 * Created by hezhi on 17/5/2.
 */

public interface FullOptionMenuLifecycle extends OptionMenuLifecycle {
    void onPrepareOptionsMenu(Menu menu);

    void onDestroyOptionsMenu();

    void onOptionsMenuClosed(Menu menu);
}
