package com.example.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class MainActivity extends AppCompatActivity {
    public static final int INTERVAL_UPDATE_MAP = 10;
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private int REQUEST_CODE = 111;

    //Firebase DataBase
    DatabaseReference mUserDatabase;

    //google API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Location");

        setLocationRequest();
//        UpdateLocationFirebase();

        getDeviceLocation();

//        sendLocationUpdate();
    }

    private void UpdateLocationFirebase() {
        //updates location after duration
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
//                ModelLocation modelLocation = new ModelLocation(location.getLatitude(),location.getLongitude(),);
//                mUserDatabase.child(mUserDatabase.push().getKey()).setValue(modelLocation);
            }
        };
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL_UPDATE_MAP * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void sendLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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
                                UpdateLocationFirebase();
                                sendLocationUpdate();
                            }
                        });
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }
//
//    private void setMap() {
//        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//                }
//        });
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0  && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                getDeviceLocation();
            }
        }else
            Toast.makeText(this, "permission needed", Toast.LENGTH_SHORT).show();
    }

    private void initialize() {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }
}