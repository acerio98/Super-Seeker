package com.example.superseeker;

import com.google.android.gms.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TitleScreenActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {

    private ImageButton newGameButton, accountButton, creditsButton;
    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;


    ArrayList<Participant> mParticipants = null;
    Map<String, Boolean> mParticipantInfo = new HashMap<>();

    // My participant ID in the currently active game
    String mMyId = null;

    // The participant ID of the host of the currently active game
    String mHostId = null;

    String mIncomingInvitationId = null;

    byte[] mMsgBuf = new byte[2];

    String mRoomId = null;

    final static int RC_SELECT_PLAYERS = 10000;
    final static int MIN_PLAYERS = 2;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    private static final int RC_SIGN_IN = 9001;

    ////////

    final static String TAG = "SuperSeeker";

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


        // set up a click listener for everything we care about
        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
    }

    public void onClick(View view){
        Intent i;
        switch(view.getId()){
            case R.id.button_sign_in:
                //start the sign-in flow
                Log.d(TAG, "Sign-in button clicked");
                mSignInClicked = true;
                mGoogleApiClient.connect();
                break;
            case R.id.button_sign_out:
                //sign out.
                Log.d(TAG, "Sign-out button clicked");
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                switchToScreen(R.id.screen_sign_in);
                break;
            case R.id.button_invite_players:
                i = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(i, RC_SELECT_PLAYERS);
                break;
            case R.id.button_see_invitations:
                i = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
                switchToScreen(R.id.screen_wait);
                startActivityForResult(i, RC_INVITATION_INBOX);
                break;
            case R.id.button_accept_popup_invitation:
                //accept on the invitation popup )from OnInvitationReceivedListener)
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
                break;
            //***all of the game buttons went in here, actually.
        }
    }

    //from tutorial
    private void handleSelectPlayersResult(int response, Intent data){
        if(response != Activity.RESULT_OK){
            Log.w(TAG, "*** select players UI cancelled, "+ response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Number of Invitees: "+invitees.size());

//        Bundle autoMatchCriteria = null;
//        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
//        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
//        if(minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0){
//            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
//                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
//            Log.d(TAG, "Automatch critera: "+ autoMatchCriteria);
//        }

        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
//        if(autoMatchCriteria != null){
//            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
//        }
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    //from tutorial
    private void handleInvitationInboxResult(int response, Intent data){
        if(response != Activity.RESULT_OK){
            Log.w(TAG, "*** invitation inbox UI cancelled, "+response);
            switchToMainScreen();
            return;
        }
        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation invite = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        acceptInviteToRoom(invite.getInvitationId());
    }

    //from tutorial
    void acceptInviteToRoom(String invId){
        //accept the invitation
        Log.d(TAG, "Accepting invitation: "+ invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        switchToScreen(R.id.screen_wait);
        keepScreenOn();
        resetGameVars();

        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    //from tutorial
    //Activity is going to background. Leave current room.
    @Override
    public void onStop(){
        Log.d(TAG, "**** got onStop");

        //if we're in a room, leave it.
        leaveRoom();

        stopKeepingScreenOn();

        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            switchToScreen(R.id.screen_sign_in);
        }
        else{
            switchToScreen(R.id.screen_wait);
        }
        super.onStop();
    }

    //from tutorial
    @Override
    public void onStart(){
        switchToScreen(R.id.screen_wait);
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            Log.w(TAG, "GameHelper: client was already connected at onStart()");
        }
        else{
            Log.d(TAG, "Connecting...");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    //from tutorial
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == R.id.screen_game){
            leaveRoom();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    //from tutorial
    void leaveRoom(){
        Log.d(TAG, "Leaving room.");
        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if(mRoomId != null){
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            mRoomId = null;
            switchToScreen(R.id.screen_wait);
        }
        else{
            switchToMainScreen();
        }
    }

    //from tutorial
    //Show the waiting room UI to track the progress of other players as they connect
    void showWaitingRoom(Room room){
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        startActivityForResult(i, RC_WAITING_ROOM);
    }

    //from tutorial
    //Called when an invitation is received.
    @Override
    public void onInvitationReceived(Invitation invite){
        mIncomingInvitationId = invite.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invite.getInviter().getDisplayName() + " " +
                        "is challenging you to a game!");
        switchToScreen(mCurScreen);
    }

    //from tutorial
    @Override
    public void onInvitationRemoved(String invitationId){
        if(mIncomingInvitationId.equals(invitationId)){
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen);
        }
    }

    //from tutorial callbacks section
    @Override
    public void onConnected(Bundle connectionHint){
        Log.d(TAG, "onConnected() called. Sign in successful!!");

        Log.d(TAG, "Sign-in succeeded.");

        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
        if(connectionHint != null){
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if(inv != null && inv.getInvitationId() != null){
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();
    }

    //from tutorial callbacks section
    @Override
    public void onConnectionSuspended(int i){
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect...");
        mGoogleApiClient.connect();
    }

    //from tutorial callbacks section
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d(TAG, "onConnectionFailed() called, result: "+connectionResult);

        if(mResolvingConnectionFailure){
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if(mSignInClicked || mAutoStartSignInFlow){
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, "Error signing in!");
        }
        switchToScreen(R.id.screen_sign_in);
    }

    //from tutorial callbacks section
    @Override
    public void onConnectedToRoom(Room room){
        Log.d(TAG, "onConnectedToRoom.");

        mRoomId = room.getRoomId();
        mParticipants = room.getParticipants();
        for(Participant p : mParticipants){
            mParticipantInfo.put(p.getDisplayName(), true); //name, inGame
        }
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        mHostId = room.getCreatorId();

        if(!mMyId.equals(mHostId)) {
            ((TextView) findViewById(R.id.instructions)).setText(R.string.player_instructions);
            ((TextView) findViewById(R.id.hidingInstructions)).setText(R.string.hiding_player_instructions);
        }

        Log.d(TAG, "Room ID: "+mRoomId);
        Log.d(TAG, "My ID: "+ mMyId);
        Log.d(TAG, "~~ CONNECTED TO ROOM ~~");
        if(mMyId.equals(mHostId))
            Log.d(TAG, "I will be seeking!");
        else
            Log.d(TAG, "I will be hiding.");
    }

    //from tutorial callbacks section
    @Override
    public void onLeftRoom(int statusCode, String roomId){
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        switchToMainScreen();
    }

    //from tutorial callbacks section
    @Override
    public void onDisconnectedFromRoom(Room room){
        mRoomId = null;
        showGameError();
    }

    //from tutorial callbacks section
    void showGameError(){
        BaseGameUtils.makeSimpleDialog(this, "An error occurred while starting the game...Sorry.");
        switchToMainScreen();
    }

    //from tutorial callbacks section
    @Override
    public void onRoomCreated(int statusCode, Room room){
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if(statusCode != GamesStatusCodes.STATUS_OK){
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }
        showWaitingRoom(room);
    }

    //from tutorial callbacks section
    //called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room){
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if(statusCode != GamesStatusCodes.STATUS_OK){
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
    }

    //from tutorial callbacks section
    @Override
    public void onJoinedRoom(int statusCode, Room room){
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // We treat most of the room update callbacks in the same way: we update our list of
    // participants and update the display. In a real game we would also have to check if that
    // change requires some action like removing the corresponding player avatar from the screen,
    // etc.
    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    void updateRoom(Room room){
        if(room != null){
            mParticipants = room.getParticipants();
        }
        if(mParticipants != null){
            if(mMyId.equals(mHostId))
                updatePeerDistanceDisplayForHost();
            else
                updatePeerDistanceDisplayForPlayer();
        }
    }

////// GAME LOGIC SECTION ////////////

    int mSecondsLeft = -1; //seconds
    final static int HIDING_PERIOD_DURATION = 180;
    final static int GAME_DURATION = 600; //seconds
    int mPlayersFound = 0;

    // Reset game variables in preparation for a new game.
    void resetGameVars() {
        mSecondsLeft = GAME_DURATION;
        mPlayersFound = 0;
        mParticipantLocations.clear();
        mFinishedParticipants.clear();
    }

    // Start the gameplay phase of the game.
    void startGame() {
        // DO GAME LOGIC every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= ( GAME_DURATION - HIDING_PERIOD_DURATION ) ) {
                    startRealGame();
                    return;
                }
                hidingTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    void startRealGame(){
        updateScoreDisplay();
        broadcastScore(false);
        switchToScreen(R.id.screen_game);

        // DO GAME LOGIC every second to update the game.
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSecondsLeft <= 0)
                    return;
                gameTick();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // Hiding tick -- update countdown
    void hidingTick() {
        if (mSecondsLeft > 0)
            --mSecondsLeft;

        // update countdown
        ((TextView) findViewById(R.id.hideCountdown)).setText("0:" +
                (mSecondsLeft < 10 ? "0" : "") + String.valueOf(mSecondsLeft));
    }

    // Game tick -- update countdown, check if game ended.
    void gameTick() {
        if (mSecondsLeft > 0)
            --mSecondsLeft;

        // update countdown
        ((TextView) findViewById(R.id.countdown)).setText("0:" +
                (mSecondsLeft < 10 ? "0" : "") + String.valueOf(mSecondsLeft));

        if (mSecondsLeft <= 0) {
            // finish game
            ((TextView)findViewById(R.id.instructions)).setText("Time is up!\nGAME OVER!!");
            ((TextView)findViewById(R.id.instructions)).setTextColor(R.color.DistanceColor10);//just a nice, assertive color
            broadcastScore(true);
        }
    }

    //////// COMMUNICATIONS SECTION ////////

    //Scores of the other participants.
    Map<String, Integer> mParticipantLocations = new HashMap<>();

    //Participants who sent their final scores.
    Set<String> mFinishedParticipants = new HashSet<>();

    // Messages are made up of two bytes: first is 'F' or 'U': final or interim score
    // Second byte is the score. Also there is the 'S' message, indicating that the game should start.
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm){
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        Log.d(TAG, "Message received: "+ (char)buf[0] + "/" + (int)buf[1]);

        if( buf[0] == 'F' || buf[0] == 'U'){
            int thisDistance = (int)buf[1];
            mParticipantLocations.put(sender, thisDistance);

            if(mMyId.equals(mHostId))
                updatePeerDistanceDisplayForHost();
            else
                updatePeerDistanceDisplayForPlayer();

            if((char)buf[0] == 'F'){
                mFinishedParticipants.add(rtm.getSenderParticipantId());
            }
        }
    }

    // Broadcast my score to everybody else.
    void broadcastScore(boolean finalScore) {

        // First byte in message indicates whether it's a final score or not
        mMsgBuf[0] = (byte) (finalScore ? 'F' : 'U');

        // Second byte is the score.
        mMsgBuf[1] = (byte) mPlayersFound; //************ NEED TO DIFFERENTIATE BETWEEN HOST AND OTHERS

        // Send to every other participant.
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId))
                continue;
            if (p.getStatus() != Participant.STATUS_JOINED)
                continue;
            if (finalScore) {
                // final score notification must be sent via reliable message
                Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, mMsgBuf,
                        mRoomId, p.getParticipantId());
            } else {
                // it's an interim score notification, so we can use unreliable
                Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, mMsgBuf, mRoomId,
                        p.getParticipantId());
            }
        }
    }

    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_accept_popup_invitation, R.id.button_invite_players,
            R.id.button_see_invitations, R.id.button_sign_in,
            R.id.button_sign_out
    };

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.screen_game, R.id.screen_main, R.id.screen_sign_in,
            R.id.screen_wait
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;

        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == R.id.screen_main);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    void switchToMainScreen() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToScreen(R.id.screen_main);
        }
        else {
            switchToScreen(R.id.screen_sign_in);
        }
    }

    // updates the label that shows my score
    void updateScoreDisplay() {
        ((TextView) findViewById(R.id.my_score)).setText(mPlayersFound + "/" + mParticipants.size());
    }

    // updates the screen with the scores from the players
    void updatePeerDistanceDisplayForHost() {
        int[] names = {
                R.id.p2name, R.id.p3name, R.id.p4name, R.id.p5name, R.id.p6name
        };
        int[] colorBoxes = {
                R.id.p2color, R.id.p3color, R.id.p4color, R.id.p5color, R.id.p6color
        };
        int i = 0;

        if (mRoomId != null) {
            for (Participant p : mParticipants) {
                String pid = p.getParticipantId();
                if (pid.equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                int distance = mParticipantLocations.containsKey(pid) ? mParticipantLocations.get(pid) : Integer.MAX_VALUE;

                ((TextView) findViewById(names[i])).setText(p.getDisplayName());

                //maximum 'red' distance is 315 yards
                //minimum 'no show' distance is 15 yards

                int colorToUse;
                if(distance<=15)
                    colorToUse = R.color.DistanceColorOff;
                else {
                    int adjustedDistanceZone = (distance - 15) / 30;
                    switch (adjustedDistanceZone) {
                        case 0: colorToUse = R.color.DistanceColor0; break;
                        case 1: colorToUse = R.color.DistanceColor1; break;
                        case 2: colorToUse = R.color.DistanceColor2; break;
                        case 3: colorToUse = R.color.DistanceColor3; break;
                        case 4: colorToUse = R.color.DistanceColor4; break;
                        case 5: colorToUse = R.color.DistanceColor5; break;
                        case 6: colorToUse = R.color.DistanceColor6; break;
                        case 7: colorToUse = R.color.DistanceColor7; break;
                        case 8: colorToUse = R.color.DistanceColor8; break;
                        case 9: colorToUse = R.color.DistanceColor9; break;
                        default: colorToUse = R.color.DistanceColor10; break;
                    }
                }

                if(distance < 2) //you basically found them, yay
                {
                    ((TextView)findViewById(names[i])).setText("FOUND ME!");
                    //****************something else???
                }

                findViewById(colorBoxes[i]).setBackgroundColor(colorToUse);
                ++i;
            }
        }

        for (; i < names.length; ++i) {
            ((TextView) findViewById(names[i])).setText("");
        }
    }

    // updates the screen with the info for the player
    void updatePeerDistanceDisplayForPlayer() {
        if (mRoomId != null) {
            //COULD BE CLEANED UP
            for (Participant p : mParticipants) {
                String pid = p.getParticipantId();
                if (pid.equals(mMyId))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;
                if (!pid.equals(mHostId))
                    continue;
                //this person is the host
                int seekerDistance = mParticipantLocations.containsKey(pid) ? mParticipantLocations.get(pid) : Integer.MAX_VALUE;

                if(seekerDistance < 2) //I am basically found
                {
                    //*****************set some text to YOU'VE BEEN FOUND!
                    broadcastScore(true);//leave game
                }

            }
        }
    }

    //////////////////////////////////////////////////////////////////////////

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case RC_SELECT_PLAYERS:
                //we got the result from the "select players" UI: create the room
                handleSelectPlayersResult(resultCode, intent);
                break;
            case RC_INVITATION_INBOX:
                //we got the result from the "select invitation" UI: accept the invitation
                handleInvitationInboxResult(resultCode, intent);
                break;
            case RC_WAITING_ROOM:
                if(resultCode == Activity.RESULT_OK){
                    //start playing
                    Log.d(TAG, "Starting game...");
                    startGame();
                }
                else if(resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM){
                    leaveRoom();
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    leaveRoom();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, resultCode = "+resultCode+", intent = " + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if(resultCode == RESULT_OK){
                    mGoogleApiClient.connect();
                }
                else{
                    BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_screen, menu);
        return true;
    }

    // Sets the flag to keep this screen on. It's recommended to do that during
    // the
    // handshake when setting up a game, because if the screen turns off, the
    // game will be
    // cancelled.
    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
