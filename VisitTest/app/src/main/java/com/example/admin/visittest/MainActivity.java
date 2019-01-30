package com.example.admin.visittest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;

public class MainActivity extends AppCompatActivity {

    IALocationManager mManager;
    LocationStore mStore;
    private TextView mLat,mLng;

    private final int CODE_PERMISSIONS=888;
    IALocationListener mIALocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation iaLocation) {
            mLat.setText(String.valueOf(iaLocation.getLatitude()));
            mLng.setText(String.valueOf(iaLocation.getLongitude()));
            Log.d("Lat:", String.valueOf(iaLocation.getLatitude()) );
            Log.d("Lng:", String.valueOf(iaLocation.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions( MainActivity.this, neededPermissions, CODE_PERMISSIONS );


       /* mLat=(TextView)findViewById(R.id.lat);
        mLng=(TextView)findViewById(R.id.lng);*/
        mManager = IALocationManager.create(this);
        mStore = LocationStore.obtain(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("location-update"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CODE_PERMISSIONS:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(),mIALocationListener);
    }*/

    /*@Override
    protected void onPause() {
        super.onPause();
        mIALocationManager.removeLocationUpdates(mIALocationListener);
    }*/

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mManager.destroy();
        super.onDestroy();
    }

    PendingIntent getPendingIntent() {
        return PendingIntent.getService(this, 0, new Intent(this, LocationStoreService.class), 0);
    }

    public void onStart(View v) {
        mManager.requestLocationUpdates(IALocationRequest.create(), getPendingIntent());
    }

    public  void onStop(View v){
        mManager.removeLocationUpdates(getPendingIntent());
    }

    public  void onShare(View v){
        mStore.share();
    }

    public  void onReset(View v){
        mStore.reset();
    }

    public void onOverlay (View v){
        Intent overlaystart=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(overlaystart);
        finish();
        return;
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("location")) {
                IALocation location = intent.getParcelableExtra("location");
                ((TextView) findViewById(R.id.location)).setText("location: "+String.valueOf(location));
            }
        }
    };
}
