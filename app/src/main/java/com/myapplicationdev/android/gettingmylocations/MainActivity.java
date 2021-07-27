package com.myapplicationdev.android.gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button btnGetLocationUpdate, btnRemoveLocationUpdate, btnCheck, btnMusic;
    private GoogleMap map;
    TextView tvLat, tvLong;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    boolean music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnMusic = findViewById(R.id.btnMusic);
        btnGetLocationUpdate = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveLocationUpdate = findViewById(R.id.btnRemoveLocationUpdate);
        btnCheck = findViewById(R.id.btnCheck);
        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);


        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, permission, 0);
        }

        String folderLocation_I = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        File folder_I = new File(folderLocation_I);
        if (folder_I.exists() == false) {
            boolean result = folder_I.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // starting the service
                if(music == true){
                    music = false;
                    btnMusic.setText("Music On");
                    stopService(new Intent(MainActivity.this, MyService.class));

                }
                else {
                    music = true;
                    btnMusic.setText("Music Off");
                    startService(new Intent(MainActivity.this, MyService.class));
                }
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
            }
        });
        if(checkPermission()){
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String msg = "Lat : " + location.getLatitude() + " Lng : " + location.getLongitude();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        tvLat.setText("Latitude: "+location.getLatitude());
                        tvLong.setText("Longitude: "+location.getLongitude());
                    } else {
                        String msg = "No Last Known Location found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult != null) {
//                    Location data = locationResult.getLastLocation();
//                    double lat = data.getLatitude();
//                    double lng = data.getLongitude();
//                    String msg = "Lat : " + lat + " Lng : " + lng;
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    tvLat.setText("Latitude: "+lat);
//                    tvLong.setText("Longitude: "+lng);
//                    LatLng poi_new = new LatLng(lat,lng);
//                    Marker poi = map.addMarker(new
//                            MarkerOptions()
//                            .position(poi_new)
//                            .title("Current location")
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//                    try {
//                        String folderLocation =
//                                Environment.getExternalStorageDirectory()
//                                        .getAbsolutePath() + "/MyFolder";
//                        File folder = new File(folderLocation);
//                        if (folder.exists() == false) {
//                            boolean result = folder.mkdir();
//                            if (result == true) {
//                                Log.d("File Read/Write", "Folder created");
//                            }
//                        }
//                        try {
//                            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
//                            File targetFile = new File(folderLocation, "data.txt");
//                            FileWriter writer = new FileWriter(targetFile, true);
//                            writer.write(""+lat+", "+lng+"\n");
//                            writer.flush();
//                            writer.close();
//                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
//                            e.printStackTrace();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };



        btnGetLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()){
                    Intent i = new Intent(MainActivity.this, MyService2.class);
                    startService(i);
//                    mLocationRequest = LocationRequest.create();
//                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                    mLocationRequest.setInterval(100);
//                    mLocationRequest.setFastestInterval(50);
//                    mLocationRequest.setSmallestDisplacement(0);
//
//                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

            }
        });

        btnRemoveLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                Intent i = new Intent(MainActivity.this, MyService2.class);
                stopService(i);
//                client.removeLocationUpdates(mLocationCallback);
            }
        });
    }
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        UiSettings ui = map.getUiSettings();

        ui.setCompassEnabled(true);

        ui.setZoomControlsEnabled(true);


        LatLng poi_Sing= new LatLng(1.3311577,103.8325641);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_Sing,
                11));

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            Log.e("GMap - Permission", "GPS access has not been granted");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }
}