package com.myapplicationdev.android.gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MyService2 extends Service {

    private DownloadBinder mBinder = new DownloadBinder();

    class DownloadBinder extends Binder {
        public void startDownload() {
            Log.d("MyService", "startDownload executed");
        }

        public int getProgress() {
            Log.d("MyService", "getProgress executed");
            return 0;
        }
    }

    boolean started;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d("MyService", "Service created");
        super.onCreate();
    }

    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false) {
            started = true;
            Log.d("MyService", "Service started");
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MyService2.this);

            String folderLocation_I = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
            File folder_I = new File(folderLocation_I);
            if (folder_I.exists() == false) {
                boolean result = folder_I.mkdir();
                if (result == true) {
                    Log.d("File Read/Write", "Folder created");
                }
            }
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location data = locationResult.getLastLocation();
                        double lat = data.getLatitude();
                        double lng = data.getLongitude();
                        String msg = "Lat : " + lat + " Lng : " + lng;
                        Toast.makeText(MyService2.this, msg, Toast.LENGTH_SHORT).show();
//                        tvLat.setText("Latitude: "+lat);
//                        tvLong.setText("Longitude: "+lng);
//                        LatLng poi_new = new LatLng(lat, lng);
//                        Marker poi = map.addMarker(new
//                                MarkerOptions()
//                                .position(poi_new)
//                                .title("Current location")
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        try {
                            String folderLocation =
                                    Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/MyFolder";
                            File folder = new File(folderLocation);
                            if (folder.exists() == false) {
                                boolean result = folder.mkdir();
                                if (result == true) {
                                    Log.d("File Read/Write", "Folder created");
                                }
                            }
                            try {
                                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
                                File targetFile = new File(folderLocation, "data.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write("" + lat + ", " + lng + "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MyService2.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MyService2.this, "Failed to write!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            };
            if (checkPermission()){
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(100);
                mLocationRequest.setFastestInterval(50);
                mLocationRequest.setSmallestDisplacement(0);

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        } else {
            Log.d("MyService", "Service is still running");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyService2.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyService2.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "Service exited");
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MyService2.this);
        client.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }

}