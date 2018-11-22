/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assignment3.cerbonnelebret.ilostmyphone;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;

/**
 * This app provides SMS features that enable the user to:
 * - Enter a phone number.
 * - Enter a message and send the message to the phone number.
 * - Receive SMS messages and display them in a toast.
 *
 * VIEW (MVC)
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    Context myContext;
    Controller myController;

    EditText editTextPhone;
    EditText editTextPass;

    Button buttonUnlock, buttonStart, buttonStop, btnPermSMS, btnPermLoc;

    // Animation
    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;


    /**
     * Creates the activity, sets the view, and checks for SMS permission.
     *
     * @param savedInstanceState Instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this.getApplicationContext();
        myController = new Controller(this.getApplicationContext(), MainActivity.this);

        // Place for the telephone number
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPass = (EditText) findViewById(R.id.editTextPass);

        //========
        // UNLOCK
        //========

        buttonUnlock = (Button) findViewById(R.id.buttonUnLock);
        buttonUnlock.setEnabled(false);
        buttonUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((myController.unlock(editTextPass.getText().toString()))) {
                    editTextPhone.setText(myController.getTel());
                } else {
                    Toast.makeText(myContext, "Unlock failed", Toast.LENGTH_LONG).show();
                }
            }
        });


        //=======
        // START
        //=======

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setEnabled(true);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myController.isStartOk(editTextPhone.getText().toString(), editTextPass.getText().toString())) {
                    Toast.makeText(myContext, "Start service", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(myContext, "Can't start service", Toast.LENGTH_LONG).show();
                }
                mRotateAnim.end();
            }
        });

        //======
        // STOP
        //======

        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setEnabled(false);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myController.stopIsOK(editTextPass.getText().toString())) {

                    if (myController.broadCastMessage("stopService")) {
                        Toast.makeText(myContext, "Stop service", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(myContext, "Can't stop service", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPermSMS = (Button) findViewById(R.id.btnPermSMS);
        btnPermSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSmsPermission();
            }
        });

        btnPermLoc = (Button) findViewById(R.id.btnPermLocalisation);
        btnPermLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermission();
                buttonUnlock.setEnabled(true);
                buttonStop.setEnabled(true);
            }
        });

        // Check to see if SMS is enabled.

        // Set up the animation.
        mAndroidImageView = (ImageView) findViewById(R.id.imageView2);
        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator
                (this, R.animator.rotate);
        mRotateAnim.setTarget(mAndroidImageView);

        mRotateAnim.start();


    }



    /**
     * Checks whether the app has SMS permission.
     */
    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, getString(R.string.permission_not_granted));

            /**
             * Permission not yet granted.
             * Use requestPermissions().MY_PERMISSIONS_REQUEST_SEND_SMS
             * is anapp-defined int constant.
             * The callback method gets the
             * result of the request.
             */

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            enableSmsButton();
        }
    }


    /**
     * Processes permission request codes.
     *
     * @param requestCode  The request code passed in requestPermissions()
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        //=====================
        // SMS PERMISSION PART
        //=====================

        // For the requestCode, check if permission was granted or not.
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Enable sms button.
                    enableSmsButton();

                } else {
                    // Permission denied.
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(this, getString(R.string.failure_permission),
                            Toast.LENGTH_LONG).show();
                    // Disable the sms button.
                    disableSmsButton();
                }
            }
        }

        //=====================
        // GPS PERMISSION PART
        //=====================

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

    /**
     * Defines a string (destinationAddress) for the phone number
     * and gets the input text for the SMS message.
     * Uses SmsManager.sendTextMessage to send the message.
     * Before sending, checks to see if permission is granted.
     *
     * @param view View (buttonTest) that was clicked.
     */
    public void smsSendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editTextPhone);
        // Set the destination phone number to the string in editText.
        String destinationAddress = editText.getText().toString();


        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Check for permission first.
        checkForSmsPermission();
        // Use SmsManager.

        Sms mySms = new Sms();
        mySms.sendSMS(destinationAddress, "test 2", myContext);

    }

    /**
     * Makes the sms button (buttonTest) invisible so that it can't be used,
     * and makes the Retry button visible.
     */
    private void disableSmsButton() {
        Toast.makeText(this, R.string.sms_disabled, Toast.LENGTH_LONG).show();
        Button smsButton = (Button) findViewById(R.id.buttonStart);
        smsButton.setVisibility(View.INVISIBLE);

    }

    /**
     * Makes the sms button (buttonTest) visible so that it can be used.
     */
    private void enableSmsButton() {
        Button smsButton = (Button) findViewById(R.id.buttonStart);
        smsButton.setVisibility(View.VISIBLE);
    }

    public void onPause() {
        myController.onPauseOK(editTextPhone.getText().toString(), editTextPass.getText().toString());
        super.onPause();
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Allow us to access this device's location ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
