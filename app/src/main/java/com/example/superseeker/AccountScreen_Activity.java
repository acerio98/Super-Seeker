package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class AccountScreen_Activity extends Activity implements View.OnClickListener{

    ImageView friendIcon;
    TextView nameText, aboutText, winsText, lossesText;
    ImageButton backButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_screen_);

        friendIcon = (ImageView)findViewById(R.id.friendIcon);
        nameText = (TextView)findViewById(R.id.nameText);
        aboutText = (TextView)findViewById(R.id.aboutText);
        winsText = (TextView)findViewById(R.id.winsText);
        lossesText = (TextView)findViewById(R.id.lossesText);

        backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        editButton = (ImageButton)findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.backButton){
            //goto main menu
        }
        else if (view.getId()==R.id.editButton){
            String iAmSignedInAs = getIntent().getStringExtra("signedInAs");
            Intent i = new Intent(AccountScreen_Activity.this, EditAccount_Activity.class);
            i.putExtra("signedInAs", iAmSignedInAs);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_screen_, menu);
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
