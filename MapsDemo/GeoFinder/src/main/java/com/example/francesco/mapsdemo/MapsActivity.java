package com.example.francesco.mapsdemo;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        String email = intent.getStringExtra("email");

        Globals globals = new Globals();
        globals.setEmail(email);

        String mesage = "Benvenuto, "+nome+"!";
        Toast.makeText(MapsActivity.this, mesage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permessi negati", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        LatLng roma = new LatLng(41.890, 12.494);
        googleMap.addMarker(new MarkerOptions().position(roma));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(roma));
        new CaricaPuntiUtente().execute(mMap);
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }


    @Override
    public void onLocationChanged(Location location) {

        Globals globals = new Globals();

        globals.setLatitude(location.getLatitude());
        globals.setLongitude(location.getLongitude());
        lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ", "" + globals.getLatitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Posizione attuale");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    public void onClick(View v) {
        final Object dataTransfer[] = new Object[1];
        Schools schools = new Schools();
        Aeroporti aeroporti = new Aeroporti();
        Bar bar = new Bar();
        Atm atm = new Atm();
        Cafe cafe = new Cafe();
        Farmacia farmacia = new Farmacia();
        Medico medico = new Medico();
        Museo museo = new Museo();
        Ospedale ospedale = new Ospedale();
        Ristorante ristorante = new Ristorante();
        Palestra palestra = new Palestra();
        Parcheggio parcheggio = new Parcheggio();
        Università università = new Università();

        switch (v.getId()) {

            case R.id.B_search:
                EditText tf_location = findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icona));
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.B_hopistals:
                mMap.clear();
                dataTransfer[0] = mMap;

                ospedale.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai l'ospedale più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button1 = (Button) findViewById(R.id.B_hopistals);
                button1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new OspedaleOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_schools:
                mMap.clear();
                dataTransfer[0] = mMap;

                schools.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai la scuola più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button2 = (Button) findViewById(R.id.B_schools);
                button2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new SchoolOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_restaurants:
                mMap.clear();
                dataTransfer[0] = mMap;

               ristorante.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il ristorante più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button3 = (Button) findViewById(R.id.B_restaurants);
                button3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new RistoranteOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_aeroporti:
                mMap.clear();
                dataTransfer[0] = mMap;

                aeroporti.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai l'aeroporto più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button4 = (Button) findViewById(R.id.B_aeroporti);
                button4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new AeroportiOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_atm:
                mMap.clear();
                dataTransfer[0] = mMap;

                atm.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai l'atm più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button5 = (Button) findViewById(R.id.B_atm);
                button5.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new AtmOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_bar:
                mMap.clear();
                dataTransfer[0] = mMap;

                bar.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il bar più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button6 = (Button) findViewById(R.id.B_bar);
                button6.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new BarOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_cafe:
                mMap.clear();
                dataTransfer[0] = mMap;

                cafe.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il cafe più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button7 = (Button) findViewById(R.id.B_cafe);
                button7.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new CafeOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });

                break;
            case R.id.B_faramcie:
                mMap.clear();
                dataTransfer[0] = mMap;

                farmacia.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai la farmacia più vicina alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button = (Button) findViewById(R.id.B_faramcie);
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new FarmaciaOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_medico:
                mMap.clear();
                dataTransfer[0] = mMap;

                medico.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il medico più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button8 = (Button) findViewById(R.id.B_medico);
                button8.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new MedicoOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_musei:
                mMap.clear();
                dataTransfer[0] = mMap;

                museo.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il museo più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button button9 = (Button) findViewById(R.id.B_musei);
                button9.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new MuseoOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });

                break;
            case R.id.B_palestre:
                mMap.clear();
                dataTransfer[0] = mMap;

                palestra.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai la palestra più vicina alla tua posizione!", Toast.LENGTH_LONG).show();

                Button buttonP = (Button) findViewById(R.id.B_palestre);
                buttonP.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new PalestraOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_parcheggi:
                mMap.clear();
                dataTransfer[0] = mMap;

               parcheggio.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai il parcheggio più vicino alla tua posizione!", Toast.LENGTH_LONG).show();

                Button buttonPa = (Button) findViewById(R.id.B_parcheggi);
                buttonPa.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new ParcheggioOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });

                break;
            case R.id.B_università:
                mMap.clear();
                dataTransfer[0] = mMap;

                università.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Tieni premuto il pulsante per qualche secondo: visualizzerai l'università più vicina alla tua posizione!", Toast.LENGTH_LONG).show();

                Button buttonU = (Button) findViewById(R.id.B_università);
                buttonU.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mMap.clear();
                        new UniversitàOnLongClick().execute(dataTransfer);
                        return true;
                    }
                });
                break;
            case R.id.B_logout:
                mMap.clear();

                Intent intent = new Intent(MapsActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


}





