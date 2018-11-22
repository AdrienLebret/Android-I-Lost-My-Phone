package com.assignment3.cerbonnelebret.ilostmyphone;


import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class receives localization updates via the void onLocationChanged method
 */
public class MyLocationListener implements LocationListener {

    private static final String TAG = "LocationListener";
    Geocoder geocoder;
    ServiceLock main;

    String phoneNumber;

    double latitude;
    double longitude;

    JSONArray result = null;

    MyLocationListener(ServiceLock m, String pN){
        this.main = m;
        phoneNumber = pN;
        geocoder = new Geocoder(main);
    }


    /**
     * A SMS is sended when the location change
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged with location " + location.toString());

        Log.d("BOUYA", "4 : Nous allons envoyer le SMS nous sommes dans onLocationChanged");

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        String text = String.format("Latitude:\t %f\nLongitude:\t %f\n", location.getLatitude(), location.getLongitude());

        //============
        // NEW METHOD
        //============

        new GetAddress().execute(String.format("%.4f,%.4f",latitude,longitude));

        /*

        //=============================
        // TEST WHO WORK / WITHOUT API
        //==============================

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            for(Address address : addresses){
                text += " " + address.getAddressLine(0);
            }
            Log.d(TAG, "address with location " + text);

            // SEND SMS WITH LOCATION

            Log.d("BOUYA", "5 : address with location " + text);

            Sms mySms = new Sms();
            //mySms.sendSMS(phoneNumber, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), main.getApplicationContext());

            // Send a link to go to gmaps with the location of the phone
            mySms.sendSMS(phoneNumber, "https://www.google.com/maps/@"+location.getLongitude()+"," + location.getLatitude() + ",15z", main.getApplicationContext());


            //mySms.sendSMS(phoneNumber, "Where is the phone?-->" + text, main.getApplicationContext());

            Log.d("BOUYA", "6 : SMS envoyé normalement");


        } catch(IOException e){
            Log.e(TAG, "Could not get Geocoder data", e);
        }*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(main, provider + "Status changed to " + status + "!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(main, "Provider " + provider + " enabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(main, "Provider " + provider + " disabled!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Inner class : API / Geocoding -> create a physical address
     * thanks to a latitude and a longitude
     */
    private class GetAddress extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            try{

                Log.d("BOUYA", "5 : getAddress() Do in background");
                double lat = latitude;
                double lng = longitude;
                String response;
                HttpDataHandler http = new HttpDataHandler();
                // This is url of Google API to convert Lat & Lng to address
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&key=AIzaSyBretkoFzQ7G1Hd08vM9lNdUjJVndqjtg8",lat,lng);
                response = http.GetHTTPData(url);

                Log.d("BOUYA", "6 : on a passé l'étape de l'URL");
                Log.d("BOUYA", "URL :" + url);

                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("BOUYA", "X : On est dans PostExecute + string : " + s);

            try{

                Log.d("BOUYA", "Y : On est dans le try");

                JSONObject jsonObject = new JSONObject(s);

                result = jsonObject.getJSONArray("results");

                if (result.length() > 0){

                    Log.d("BOUYA", "7 : Lenght > 0");

                    JSONObject c = result.getJSONObject(0);
                    String address = c.get("formatted_address").toString();

                    Sms mySms = new Sms();
                    mySms.sendSMS(phoneNumber, address, main.getApplicationContext());

                    Log.d("BOUYA", "8 : SMS envoyé normalement");
                } else {
                    // if there is a problem with the API key (that we use the key to many time in a day)

                    // Send a link to go to gmaps with the location of the phone
                    giveURLGmaps(new IsGd.ShortenerListener() {
                        public void done(String shortUrl) {
                            Sms mySms = new Sms();
                            mySms.sendSMS(phoneNumber, shortUrl, main.getApplicationContext());
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * API : Short URL
     * @return new URL
     */

    private void giveURLGmaps(IsGd.ShortenerListener callback){
        String url = "https://www.google.com/maps/@"+ latitude +"," +longitude  + ",15z";
        new IsGd(callback, url);
    }

}
