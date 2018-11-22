package com.assignment3.cerbonnelebret.ilostmyphone;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Service that receives SMS messages
 * that will allow the device to be controlled remotely.
 */
public class ServiceLock extends Service {

    //=====
    // SMS
    //=====

    SmsReceiver myReceiver;
    BroadcastReceiver msgCom;

    //=====
    // GPS
    //=====

    //LocationManager locationManager = null;
    private LocationManager locationManager;
    private String provider; // provider = fournisseur
    private MyLocationListener myListener;

    //======
    // DATA
    //======

    String phoneNumber;


    @Override
    public void onCreate() {
        initData();
        initSms();
        Log.d("BOUYA", "ServiceLock On Create");
        initBroadcast();
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    /**
     * restart in case of shutdown
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY; // allow to have a Start Auto !
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(msgCom);
        unregisterReceiver(myReceiver);
        Log.d("BOUYA", "service done");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initBroadcast() {
        Log.d("BOUYA", "Init Broadcast");
        msgCom = new MsgCom();

        // local receiver for intents
        LocalBroadcastManager.getInstance(this).registerReceiver(msgCom, new IntentFilter("lockMyPhone"));
    }


    private void initSms() {
        Log.d("BOUYA", "Init Sms");
        // Receive SMS
        myReceiver = new SmsReceiver();
        this.registerReceiver(myReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }


    /**
     * Initialize the provider's location with a good accuracy
     */
    private void initGPS() {

        Log.d("BOUYA", "0 : Entrer dans la méthode initGPS");

        // Manager position

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            provider = LocationManager.GPS_PROVIDER;

            Log.d("BOUYA", "provider = " + provider);
        }
        if (provider != null) {

            Log.d("BOUYA", "1 : Nous allons regarder la position");
            // Last location knows by the provider
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d("BOUYA", "2 bis : PROBLEME DE PERMISSION");
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            Log.d("BOUYA", "2 : Nous avons la position " + location);
            myListener = new MyLocationListener(this, phoneNumber);

            if (location != null) {
                Log.d("BOUYA", "3 : La position n'est pas nulle");
                myListener.onLocationChanged(location);
            }

            // conditions for updating the position: at least 10 meters and 5000 millsecs
            locationManager.requestLocationUpdates(provider, 15000, 10, myListener);


        }
    }







    private void stopGPS(){
        if(locationManager != null && myListener != null){
            locationManager.removeUpdates(myListener);
        }

        myListener = null;
    }


    private void initData() {
        // loading of the parameters
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();
        loadMyData.restoreData(this.getApplicationContext(), data);

        ManageSettings.isRecord = Boolean.valueOf(data.get("RECORD"));
        phoneNumber = data.get("TEL");
    }


    private class MsgCom extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), "lockMyPhone")){
                String message = intent.getStringExtra("message");
                Log.d("BOUYA", "Got message: " + message);

                if(message.equals("Thanks!")){ // = Stop Service
                    stopGPS();
                    ServiceLock.this.stopSelf();
                    Log.d("BOUYA", "Service stopped");
                } else if (message.equals("Kebab?")){ // = startGPS
                    initGPS();
                    Log.d("BOUYA","GPS tracking started");
                } else if (message.equals("Ok see you")){ // Stop GPS
                    stopGPS();
                    Log.d("BOUYA", "GPS tracking stopped");
                }

                /**
                 * Add some new commands for different updates
                 */
            }
        }
    }
}
