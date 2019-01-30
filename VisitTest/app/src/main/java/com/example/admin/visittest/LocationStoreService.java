package com.example.admin.visittest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.indooratlas.android.sdk.IALocation;

public class LocationStoreService extends IntentService {

    public LocationStoreService() {
        super("LocationStoreService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        IALocation location = IALocation.from(intent);
        if (location != null) {
            LocationStore.obtain(this).store(location);
            sendLocalBroadcast(location);
        }
    }

    private void sendLocalBroadcast(IALocation location) {
        Intent intent = new Intent("location-update");
        intent.putExtra("location", location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}