package com.dianping.shield.framework;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by hezhi on 17/5/2.
 */

public interface OptionMenuLifecycle {
    void onCreateOptionsMenu(Menu menu, MenuInflater inflater);

    boolean onOptionsItemSelected(MenuItem item);
}


