package com.forwork.triolan.main;

import android.app.Application;

import com.forwork.triolan.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

/**
 * Created by Dryhulias on 18.12.2015.
 */
public class TriolanTvApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdBuddiz.setPublisherKey(getString(R.string.adbuddiz_key));
    }
}
