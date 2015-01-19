package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class TitleScreenActivity extends Activity implements View.OnClickListener{

    private ImageButton newGameButton, accountButton, creditsButton;
    private static String signedInAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

        newGameButton = (ImageButton)findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(this);

        accountButton = (ImageButton)findViewById(R.id.accountButton);
        accountButton.setOnClickListener(this);

        creditsButton = (ImageButton)findViewById(R.id.creditsButton);
        creditsButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.newGameButton){
            if(signedInAs.equals("")){
                Intent i = new Intent(TitleScreenActivity.this, SignIn_Activity.class);
                i.putExtra("destination", "game");
                startActivity(i);
            }
            else{
                Intent i = new Intent(TitleScreenActivity.this, NewGame_Activity.class);
                i.putExtra("signedInAs", signedInAs);
                startActivity(i);
            }
        }
        else if(view.getId()==R.id.accountButton){
            if(signedInAs.equals("")) {
                Intent i = new Intent(TitleScreenActivity.this, SignIn_Activity.class);
                i.putExtra("destination", "account");
                startActivity(i);
            }
            else{
                Intent i = new Intent(TitleScreenActivity.this, AccountScreen_Activity.class);
                i.putExtra("signedInAs", signedInAs);
                startActivity(i);
            }
        }
        else if(view.getId()==R.id.creditsButton){
            Intent i = new Intent(TitleScreenActivity.this, Credits_Activity.class);
            startActivity(i);
        }
    }

    public static void signIn(String username){
        signedInAs = username;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_screen, menu);
        return true;
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
}
