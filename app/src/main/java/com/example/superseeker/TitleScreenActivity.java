package com.example.superseeker;

import com.google.android.gms.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TitleScreenActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener
        {

    private ImageButton newGameButton, accountButton, creditsButton;
    private Button signInButton, signOutButton;
    private static String signedInAs;
    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    boolean mExplicitSignOut = false;
    boolean mInSignInFlow = false;
    boolean mPlaying = false;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int MIN_PLAYERS = 2;
    final static int RC_WAITING_ROOM = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                //add other APIs and scopes here as needed
                .build();


        newGameButton = (ImageButton)findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(this);

        accountButton = (ImageButton)findViewById(R.id.accountButton);
        accountButton.setOnClickListener(this);

        creditsButton = (ImageButton)findViewById(R.id.creditsButton);
        creditsButton.setOnClickListener(this);

        signInButton = (Button)findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        signOutButton = (Button)findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.newGameButton){
//            if(signedInAs.equals("")){
//                Intent i = new Intent(TitleScreenActivity.this, SignIn_Activity.class);
//                i.putExtra("destination", "game");
//                startActivity(i);
//            }
//            else{
//                Intent i = new Intent(TitleScreenActivity.this, NewGame_Activity.class);
//                i.putExtra("signedInAs", signedInAs);
//                startActivity(i);
//            }

              if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                  //Intent i = new Intent(TitleScreenActivity.this, NewGame_Activity.class);
                  //startActivity(i);
                  Intent i = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
                  startActivityForResult(i, RC_SELECT_PLAYERS);

              }
              else{
                  Toast needToSignIn = Toast.makeText(getApplicationContext(), "You need to sign in first!",
                          Toast.LENGTH_SHORT);
                  needToSignIn.show();
              }
        }
        else if(view.getId()==R.id.accountButton){
//            if(signedInAs.equals("")) {
//                Intent i = new Intent(TitleScreenActivity.this, SignIn_Activity.class);
//                i.putExtra("destination", "account");
//                startActivity(i);
//            }
//            else{
//                Intent i = new Intent(TitleScreenActivity.this, AccountScreen_Activity.class);
//                i.putExtra("signedInAs", signedInAs);
//                startActivity(i);
//            }

              if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                  Intent i = new Intent(TitleScreenActivity.this, AccountScreen_Activity.class);
                  startActivity(i);
              }
              else{
                  Toast needToSignIn = Toast.makeText(getApplicationContext(), "You need to sign in first!",
                        Toast.LENGTH_SHORT);
                  needToSignIn.show();
              }
        }
        else if(view.getId()==R.id.creditsButton){
            Intent i = new Intent(TitleScreenActivity.this, Credits_Activity.class);
            startActivity(i);
        }
        else if(view.getId()==R.id.signInButton){
            //start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
        else if(view.getId()==R.id.signOutButton){
            // sign out.
            mSignInClicked = false;
            //Games.signOut(mGoogleApiClient);

            //user explicitly signed out, so turn off auto sign in
            mExplicitSignOut = true;
            if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }

            // show sign-in button, hide the sign-out button
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutButton).setVisibility(View.GONE);
        }
    }

    public static void signIn(String username){
        signedInAs = username;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mInSignInFlow && !mExplicitSignOut) {
            //auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.

        findViewById(R.id.signInButton).setVisibility(View.GONE);
        findViewById(R.id.signOutButton).setVisibility(View.VISIBLE);

        // (your code here: update UI, enable functionality that depends on sign in, etc)
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_screen, menu);
        return true;
    }

    boolean shouldStartGame(Room room){
        int connectedPlayers = 0;
        for(Participant p : room.getParticipants()){
            if(p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    boolean shouldCancelGame(Room room){
        //Game specific cancellation logic here. E.g. cancel if enough people have declined or left.
        //Maybe use Participant.getStatus()
        //Cancel button too

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        if(mResolvingConnectionFailure){
            //Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "error, could not sign in!")) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
        else if(requestCode == RC_SELECT_PLAYERS){
            if(resultCode != Activity.RESULT_OK){
                //user canceled
                return;
            }

            //get the invitee list
            Bundle extras = intent.getExtras();
            final ArrayList<String> invitees =
                intent.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            //get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);

            if(minAutoMatchPlayers > 0){
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            }else{
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if(autoMatchCriteria != null){
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

            //prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if(requestCode == RC_WAITING_ROOM){
            if(resultCode == Activity.RESULT_OK){
                //start game
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                
            }
        }
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder(){
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

    @Override
    public void onRoomCreated(int statusCode, Room room){
        if(statusCode != GamesStatusCodes.STATUS_OK){
            //let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            return;
            //show error message, return to main screen.
        }

        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room){
        if(statusCode != GamesStatusCodes.STATUS_OK){
            //let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            return;
            //show error message, return to main screen.
        }

        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onRoomConnected(int statusCode, Room room){
        if(statusCode != GamesStatusCodes.STATUS_OK){
            //let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //show error message, return to main screen.
        }
    }

    @Override
    public void onRoomAutoMatching(Room room){}

    @Override
    public void onP2PDisconnected(String s){}

    @Override
    public void onLeftRoom(int i, String s){}

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list){}

    @Override
    public void onPeerJoined(Room room, List<String> list){}

    @Override
    public void onPeersConnected(Room room, List<String> list){
        if(mPlaying){
            //add new player to an ongoing game
        }else if(shouldStartGame(room)){
            //start game!
        }
    }


    @Override
    public void onPeerLeft(Room room, List<String> list){}

    @Override
    public void onPeerDeclined(Room room, List<String> list){}

    @Override
    public void onPeersDisconnected(Room room, List<String> list){
        if(mPlaying){
            //do game-specific handling of this -- remove player's avatar
            //from the screen, etc. If not enough players are left for the
            //game to go on, end the game and leave the room.
        }else if (shouldCancelGame(room)){
            //cancel the game
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onRoomConnecting(Room room){}

    @Override
    public void onDisconnectedFromRoom(Room room){}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
