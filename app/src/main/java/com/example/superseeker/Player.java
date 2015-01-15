package com.example.superseeker;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by Austin on 1/15/15.
 */
public class Player {
    private double latitude, longitude;
    private String name;

    public Player(){
        updatePosition();
        name = "Blank";
    }
    public Player(String name){
        updatePosition();
        this.name = name;
    }
    public void updatePosition(){
        //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }
}
