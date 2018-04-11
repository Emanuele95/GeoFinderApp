package com.example.francesco.mapsdemo;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Francesco on 07/04/2018.
 */

public class OspedaleOnLongClick extends AsyncTask<Object,String,String> {

    private GoogleMap mMap;
    String json_url;
    String coordinate;
    String JSON_STRING;
    private static final int Raggio = 6371;
    Globals globals;


    @Override
    protected String doInBackground(Object... objects) {
        Globals globals = new Globals();
        String email = globals.getEmail();
        json_url = "http://aeff.hopto.org:8002/Test/fetch_ospedale.php?email="+email;
        mMap = (GoogleMap)objects[0];

        try {
            URL url = new URL(json_url);
            HttpURLConnection httpURLConnection =(HttpURLConnection)url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            while((JSON_STRING = bufferedReader.readLine()) != null){
                stringBuilder.append(JSON_STRING+"\n");
            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        ArrayList<Coordinate> coordinate = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(s);
            JSONObject jsonObject;
            for (int i = 0; i < array.length(); i++ ) {
                jsonObject = array.getJSONObject(i);
                double lat = jsonObject.getDouble("lat");
                double lng = jsonObject.getDouble("lng");
                String nome = (String)jsonObject.get("nomeLuogo");
                int idUser = jsonObject.getInt("idUser");
                coordinate.add(new Coordinate(lat,lng,nome,idUser));
            }

            showSchool(coordinate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showSchool(ArrayList<Coordinate> lc) {

        globals = new Globals();
        double min = 384400;
        double latmin = 0, lngmin = 0;
        MarkerOptions markerOptions = new MarkerOptions();
        String placeName = "";
        int idUser = 0;

        for (int j = 0; j < lc.size(); j++) {
            Coordinate c = lc.get(j);

            double lat = c.getLat();
            double lng = c.getLng();
            double risultato = getDistanceFromLatLonInKm(globals.getLatitude(), globals.getLongitude(), lat, lng);

            if (risultato < min) {
                min = risultato;
                latmin = lat; lngmin = lng;
                placeName = c.getNomeLuogo();
                idUser = c.getIdUser();
            }
        }
        LatLng latLng = new LatLng(latmin, lngmin);
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        if (idUser != 1) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        }

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
    }

    public double getDistanceFromLatLonInKm(double lat1, double lng1,double lat2, double lng2){
        double dLat = degr2rad(lat2-lat1);
        double dLng = degr2rad(lng2-lng1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(degr2rad(lat1)) * Math.cos(degr2rad(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = Raggio * c; //Distanza in Km
        return d;
    }

    public double degr2rad(double deg) {
        return deg * (Math.PI/180);
    }

}
