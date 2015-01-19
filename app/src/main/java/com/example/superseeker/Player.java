package com.example.superseeker;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by Austin on 1/15/15.
 */
public class Player {
    private double latitude, longitude;
    private String name, username, password, email, about;
    private int wins, losses;

    public Player(){
        updatePosition();
        name = "";
        username = "";
        password = "";
        email = "";
        about = "";
        wins = 0;
        losses = 0;
    }
    public Player(String name, String username, String password, String email, String about){
        updatePosition();
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.about = about;
        wins = 0;
        losses = 0;
    }
    public void updatePosition(){
        //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }
    public void signIn(){
        TitleScreenActivity.signIn(username);
    }
    public void signOut(){
        TitleScreenActivity.signIn("");
    }
}
