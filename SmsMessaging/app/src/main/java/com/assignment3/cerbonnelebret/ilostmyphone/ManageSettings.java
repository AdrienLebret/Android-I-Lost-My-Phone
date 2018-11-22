package com.assignment3.cerbonnelebret.ilostmyphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Parameter Management = MODEL (MVC)
 *
 * This class allows the complete saving of the form with the saveData() method
 */
public class ManageSettings {

    public static Boolean isRecord;
    // name of the file shareprefs
    public static final String PREFS_PRIVATE = "data";
    private SharedPreferences prefsPrivate;

    public void saveData(Context context, HashMap<String, String> data) {

        prefsPrivate = context.getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsPrivateEditor = prefsPrivate.edit();

        prefsPrivateEditor.putString("TEL", data.get("TEL").trim());
        prefsPrivateEditor.putString("PASS", encode(data.get("PASS").trim()));
        prefsPrivateEditor.putString("RECORD", data.get("RECORD").trim());

        prefsPrivateEditor.apply();
    }

    public void restoreData(Context context, HashMap<String, String> data) {
        SharedPreferences myPrefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        data.put("TEL", myPrefs.getString("TEL", null));
        data.put("PASS", myPrefs.getString("PASS", null));
        data.put("RECORD", myPrefs.getString("RECORD", null));
    }


    public String encode(String msg) {
        // Sending side
        byte[] myPass = new byte[0];
        try {
            myPass = msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(myPass, Base64.DEFAULT);
    }
}
