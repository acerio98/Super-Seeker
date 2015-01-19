package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class NewAccount_Activity extends Activity implements View.OnClickListener{

    ImageButton cancelButton, okButton, addPicButton;
    EditText nameField, emailField, aboutField, usernameField, passwordField;
    ImageView friendIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_);

        cancelButton = (ImageButton)findViewById(R.id.backButton);
        cancelButton.setOnClickListener(this);
        okButton = (ImageButton)findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        addPicButton = (ImageButton)findViewById(R.id.addPicButton);
        addPicButton.setOnClickListener(this);

        nameField = (EditText)findViewById(R.id.nameField);
        emailField = (EditText)findViewById(R.id.emailField);
        aboutField = (EditText)findViewById(R.id.aboutField);
        usernameField = (EditText)findViewById(R.id.usernameField);
        passwordField = (EditText)findViewById(R.id.passwordField);

        friendIcon = (ImageView)findViewById(R.id.friendIcon);
    }

    public void onClick(View view){
        if(view.getId() == R.id.backButton){
            finish();
        }
        if(view.getId() == R.id.okButton){
            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String about = aboutField.getText().toString();
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            if(name.equals("")||
               email.equals("")||
               about.equals("")||
               username.equals("")||
               password.equals("")){
                Toast emptyFields = Toast.makeText(getApplicationContext(), "Please fill in all fields.",
                        Toast.LENGTH_SHORT);
                emptyFields.show();
            }
            else{
                Player newPlayer = new Player(name, username, password, email, about);
                //put the player into the cloud database
                Intent i = new Intent(NewAccount_Activity.this, AccountCreated_Activity.class);
                startActivity(i);
            }
        }
        else if(view.getId() == R.id.addPicButton){
            //add a picture; to be implemented later...
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_account_, menu);
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
