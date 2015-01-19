package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class GameScreen_Activity extends Activity implements View.OnClickListener{

    ImageButton playersButton, endButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_);

        playersButton = (ImageButton)findViewById(R.id.playersLeftButton);
        playersButton.setOnClickListener(this);
        endButton = (ImageButton)findViewById(R.id.endButton);
        endButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.playersLeftButton){
            //to be implemented
        }
        else if(view.getId()==R.id.endButton){
            Intent i = new Intent(GameScreen_Activity.this, EndGame_Activity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen_, menu);
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
