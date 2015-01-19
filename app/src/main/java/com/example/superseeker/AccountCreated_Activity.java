package com.example.superseeker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class AccountCreated_Activity extends Activity implements View.OnClickListener{

    ImageView friendIcon;
    TextView nameText;
    ImageButton mainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_created_);

        friendIcon = (ImageView)findViewById(R.id.friendIcon);
        nameText = (TextView)findViewById(R.id.nameText);
        mainMenuButton = (ImageButton)findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.mainMenuButton){
            //goto main menu
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_created_, menu);
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
