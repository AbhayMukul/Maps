package com.example.maps;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class MyLocationService extends BroadcastReceiver {

    //Firebase DataBase
    DatabaseReference mUserDatabase;

    //time
    Date date = new Date();
    String time = (String) android.text.format.DateFormat.format("hh:mm:ss", date);


    public static final String ACTION_PROCESS_UPDATE = "edmt.dev.googlelocationbackground.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    Location location  = result.getLastLocation();
                    ModelLocation modelLocation = new ModelLocation(location.getLatitude(),location.getLongitude(),time);

                    //set Data
                    mUserDatabase = FirebaseDatabase.getInstance().getReference("Location");

                    mUserDatabase.child("Activity2").child(mUserDatabase.push().getKey()).child("Locations").setValue(modelLocation);
                }
            }
        }
    }
}