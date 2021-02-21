package com.example.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity2 extends AppCompatActivity {
    private static final int INTERVAL_UPDATE_MAP = 1;
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;

    //Firebase DataBase
    DatabaseReference mUserDatabase;

    //google API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    static MainActivity instance;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initialize();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Location");

        setDexter();

        getDeviceLocation();
    }

    private void setDexter() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                getDeviceLocation();
                upDateLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
    }

    private void upDateLocation() {
        setLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this,MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL_UPDATE_MAP * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void initialize() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity2.this);
        if(ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission granted
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
                                googleMap.addMarker(markerOptions).showInfoWindow();
                            }
                        });
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(MainActivity2.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 111){
            if(grantResults.length > 0  && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                getDeviceLocation();
            }
        }else
            Toast.makeText(this, "permission needed", Toast.LENGTH_SHORT).show();
    }

//    public void setDatabase(final ModelLocation modelLocation){
//        MainActivity2.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mUserDatabase.child("Activity2").child("Locations").setValue(modelLocation);
//            }
//        });
//    }
}