package com.assignment3.cerbonnelebret.ilostmyphone;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Adrien LEBRET
 */
public class Sms {

    private static final String TAG = "SmsSender";

    public void sendSMS(String phoneNumber, String message, final Context myContext) {

        if (phoneNumber == null || message == null || myContext == null) return;
        if (phoneNumber.trim().isEmpty() || message.trim().isEmpty()) return;

        Log.d(TAG, "phoneNumber: " + phoneNumber + "  " + "message : " + message);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        //Toast.makeText(myContext.getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
    }

}
