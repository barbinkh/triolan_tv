package com.forwork.triolan.soap;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.triolan.android.api.ApiCore;

import java.util.UUID;

public class TriolanAPI extends Application {

    private static final String TAG = "TriolanAPI";

    private final String APPLICATION_UUID = "APPLICATION_UUID";

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

    }

}