package com.forwork.triolan.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.forwork.triolan.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.triolan.android.api.ApiCore;

import java.util.UUID;

public class TriolanAPI extends Application {

    private static final String TAG = "TriolanAPI";

    private final String APPLICATION_UUID = "APPLICATION_UUID";
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
    public void trackScreenView(String screenName) {
        Tracker t = getDefaultTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String applicationUUID = sharedPreferences.getString(APPLICATION_UUID,
                UUID.randomUUID().toString());
        if (!sharedPreferences.contains(APPLICATION_UUID)) {
            sharedPreferences.edit()
                    .putString(APPLICATION_UUID, applicationUUID).commit();
        }
        Log.d(TAG, "applicationUUID: " + applicationUUID);
        // ApiCore.init(this, applicationUUID);
        ApiCore.init(this, applicationUUID);                 //инициализация методв из библиотеки triolanapi
        ApiCore.getAlacarteAddress("CustomerLogin");
        AdBuddiz.setPublisherKey(getString(R.string.adbuddiz_key));



    }

}