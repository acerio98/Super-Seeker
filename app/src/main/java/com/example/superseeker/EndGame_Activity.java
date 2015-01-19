package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class EndGame_Activity extends Activity implements View.OnClickListener{

    ImageButton backButton, forfeitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_);

        backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        forfeitButton = (ImageButton)findViewById(R.id.forfeitButton);
        forfeitButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.backButton){
            finish();
        }
        else if(view.getId()==R.id.forfeitButton){
            Intent i = new Intent(EndGame_Activity.this, TitleScreenActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_game_, menu);
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
