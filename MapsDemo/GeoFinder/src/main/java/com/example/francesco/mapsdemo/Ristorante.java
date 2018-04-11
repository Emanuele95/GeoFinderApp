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
 * Created by Francesco on 09/04/2018.
 */

public class Ristorante extends AsyncTask<Object,String,String> {

    private GoogleMap mMap;
    String json_url;
    String coordinate;
    String JSON_STRING;


    @Override
    protected String doInBackground(Object... objects) {
        Globals globals = new Globals();
        String email = globals.getEmail();
        json_url = "http://aeff.hopto.org:8002/Test/fetch_ristorante.php?email="+email;
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

        for (int j = 0; j < lc.size(); j++) {
            MarkerOptions markerOptions = new MarkerOptions();
            Coordinate c = lc.get(j);

            double lat = c.getLat();
            double lng = c.getLng();
            String placeName = c.getNomeLuogo();
            int idUser = c.getIdUser();

            LatLng latLng = new LatLng(lat,lng);
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


    }


}
