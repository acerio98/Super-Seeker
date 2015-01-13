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

    ImageButton newGameButton, accountButton, creditsButton;

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
            Intent i = new Intent(TitleScreenActivity.this, NewGameActivity.class);
            System.out.println("yay");
            startActivity(i);
        }
        else if(view.getId()==R.id.accountButton){
            //Intent i = new Intent(TitleScreenActivity.this, NewGameActivity.class);
            //startActivity(i);
        }
        else if(view.getId()==R.id.creditsButton){
            ///Intent i = new Intent(TitleScreenActivity.this, NewGameActivity.class);
            //startActivity(i);
        }
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
