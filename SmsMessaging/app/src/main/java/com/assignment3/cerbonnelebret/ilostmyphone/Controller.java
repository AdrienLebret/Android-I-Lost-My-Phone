package com.assignment3.cerbonnelebret.ilostmyphone;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;

/**
 * CONTROLLER (MVC)
 */
public class Controller {
    Context mContext;
    String pass;
    String tel;
    Boolean modeInit = false;
    Boolean unlock = false;
    ManageSettings manageData;
    MainActivity mActivity;

    Controller(Context mContext, MainActivity mActivity){
        this.mContext = mContext;
        this.mActivity = mActivity;

        manageData = new ManageSettings();

        // Loading parameters
        loadSettings();
    }

    public Boolean getRecord(){
        return ManageSettings.isRecord;
    }

    public String getTel(){
        return tel;
    }

    public Boolean unlock(String pass){
        // loading the parameters
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();
        loadMyData.restoreData(mContext, data);

        if (pass.trim().isEmpty() || pass == null){
            return false;
        }

        if (loadMyData.encode(pass).equals(data.get("PASS"))){
            unlock = true;
            return true;
        } else {
            unlock = false;
            return false;
        }
    }

    public Boolean stopIsOK(String pass){

        if(mActivity.isMyServiceRunning(ServiceLock.class) && unlock(pass)){
            return true;
        } else {
            return false;
        }
    }

    private void loadSettings() {
        // loading of parameters of the configuration
        ManageSettings loadMyData = new ManageSettings();
        HashMap<String, String> data = new HashMap<String, String>();

        loadMyData.restoreData(mContext, data);

        if (data.get("TEL") == null && data.get("PASS") == null){
            modeInit = true;
            unlock = true;
        }

        pass = data.get("PASS");
        ManageSettings.isRecord = Boolean.parseBoolean(data.get("RECORD"));
        tel = data.get("TEL");
    }

    public Boolean broadCastMessage(String msg){
        Log.d("MainActivity", "Broadcasting message");
        Intent intent = new Intent("lockMyPhone");
        intent.putExtra("message", msg);
        return LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    public Boolean saveSettings(String tel, String pass){
        HashMap<String, String> data = new HashMap<String, String>();

        if (tel == null || pass == null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty()) return false;

        data.put("TEL", tel);
        data.put("PASS", pass);

        if (ManageSettings.isRecord) {
            data.put("RECORD", "true");
        } else
            data.put("RECORD", "false");

        manageData.saveData(mContext, data);

        return true;
    }

    public boolean isStartOk(String tel, String pass) {

        if(tel==null || pass==null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty())  return false;

        // premiere utilisation du soft
        if (modeInit) {
            saveSettings(tel, pass);
            modeInit = false;

            if (!mActivity.isMyServiceRunning(ServiceLock.class)) {
                Intent i = new Intent(mContext, ServiceLock.class);
                mContext.startService(i);
                return true;
            }
            return false;
        }

        // unlock
        if (!unlock) {
            return false;
        }

        // IHM Unlock + test password ?
        if (unlock(pass) && !mActivity.isMyServiceRunning(ServiceLock.class)) {
            saveSettings(tel, pass);
            Intent i = new Intent(mContext, ServiceLock.class);
            mContext.startService(i);
            return true;
        }

        return false;
    }

    public boolean onPauseOK(String tel, String pass) {

        if(tel==null || pass==null) return false;

        if (tel.trim().isEmpty() || pass.trim().isEmpty()) return false;

        if (unlock) {
            saveSettings(tel, pass);
            return true;
        }
        return false;
    }

}
