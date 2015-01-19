package com.example.superseeker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;


public class SignIn_Activity extends Activity implements View.OnClickListener{

    ImageButton backButton, signInButton, createAccountButton;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        signInButton = (ImageButton)findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        createAccountButton = (ImageButton)findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(this);

        username = (EditText)findViewById(R.id.usernameField);
        password = (EditText)findViewById(R.id.passwordField);
    }

    public void onClick(View view){
        if(view.getId()==R.id.backButton){
            finish();
        }
        else if(view.getId()==R.id.signInButton){
            String usernameGuess = username.getText().toString();
            String passwordGuess = password.getText().toString();

            HashMap<String, String> allUsernamesAndPasswords = new HashMap<>();
            //fill up map with saved usernames and passwords from the cloud.
            if(allUsernamesAndPasswords.get(usernameGuess).equals(passwordGuess)){
                String destination = getIntent().getStringExtra("destination");
                TitleScreenActivity.signIn(usernameGuess);
                if(destination.equals("game")){
                    Intent i = new Intent(SignIn_Activity.this, NewGame_Activity.class);
                    i.putExtra("signedInAs", usernameGuess);
                    startActivity(i);
                }
                else if(destination.equals("account")){
                    Intent i = new Intent(SignIn_Activity.this, AccountScreen_Activity.class);
                    i.putExtra("signedInAs", usernameGuess);
                    startActivity(i);
                }
                else{
                    System.out.println("OHNO INVALID DESTINATION");
                }
            }
            else{
                Toast invalidCredentials = Toast.makeText(getApplicationContext(), "Invalid username or password",
                        Toast.LENGTH_SHORT);
                invalidCredentials.show();
            }
        }
        else if(view.getId()==R.id.createAccountButton){
            Intent i = new Intent(SignIn_Activity.this, NewAccount_Activity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in_, menu);
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
