package com.vishalkumar.supplier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String ADMIN = "admin";

    private static final String USER = "user";

    //Layout to ask for password form Admin/User
    LinearLayout linearLayoutAskPassword;

    //Edit text for password
    EditText editTextPassword;

    //Button for admin to login
    Button adminButton;

    //Button for user to login
    Button userButton;

    //Login button to authorize
    Button login;

    //To tell the app weather user or admin wants to login
    String whoWantsToLogin;

    // To get user preference
    SharedPreferences userPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize user preference
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Find reference to the linear layout
        linearLayoutAskPassword = findViewById(R.id.linearLayoutPassword);

        //Find reference to the editText where Admin/User enters password
        editTextPassword = findViewById(R.id.editTextPassword);

        //Find Login button by id
        login = findViewById(R.id.buttonLogin);

        //Find Buttons by their id's
        adminButton = findViewById(R.id.buttonAdmin);
        userButton = findViewById(R.id.buttonUser);

        //Set on click listener on login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordValid()){

                    //Open the view product activity a when button is clicked and password is correct for admin
                    if(whoWantsToLogin.equals(ADMIN)){
                        Intent adminPanel = new Intent(MainActivity.this, AdminPanelActivity.class);
                        startActivity(adminPanel);
                    }

                    //Open the Billing Activity when button is clicked and password is correct for user
                    else if(whoWantsToLogin.equals(USER)){
                        Intent billingActivity = new Intent(MainActivity.this, BillingActivity.class);
                        startActivity(billingActivity);
                    }
                }
            }
        });

        // Set On Click Listener on admin and user buttons
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAdminPasswordEnabled()){
                    whoWantsToLogin = ADMIN;
                    showPasswordScreen();
                }
                else {
                    Intent adminPanel = new Intent(MainActivity.this, AdminPanelActivity.class);
                    startActivity(adminPanel);
                }
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserPasswordEnabled()){
                    whoWantsToLogin = USER;
                    showPasswordScreen();
                }
                else {
                    Intent billingActivity = new Intent(MainActivity.this, BillingActivity.class);
                    startActivity(billingActivity);
                }

            }
        });
    }

    //Show password Screen
    private void showPasswordScreen(){

        // Make the linearLayout visible as admin/user can enter password
        linearLayoutAskPassword.setVisibility(View.VISIBLE);
        linearLayoutAskPassword.animate().alpha(1).setDuration(500);

        //Disable other buttons
        adminButton.setClickable(false);
        userButton.setClickable(false);
    }

    //Hide Password Screen
    private void hidePasswordScreen(){
        // Make the linearLayout Invisible as admin/user can select other options
        linearLayoutAskPassword.animate().alpha(0).setDuration(500);
        linearLayoutAskPassword.setVisibility(View.GONE);

        //enable other buttons
        adminButton.setClickable(true);
        userButton.setClickable(true);

        // Empty password field
        editTextPassword.setText("");

    }

    // Ask Password
    private String askForPassword(){
        //Get the password from edit text;
        String password = editTextPassword.getText().toString().trim();
        return password;
    }

    //Verify Password
    private boolean isPasswordValid(){

        String password = askForPassword();

        //If admin wants to login
        if(password.equalsIgnoreCase(getAdminPassword()) && whoWantsToLogin.equalsIgnoreCase(ADMIN)){
            hidePasswordScreen();
            return true;
        }
        // else If user wants to login
        else if(password.equalsIgnoreCase(getUserPassword()) && whoWantsToLogin.equalsIgnoreCase(USER)){
            hidePasswordScreen();
            return true;
        }
        // If password is incorrect
        else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
            return false;
        }
    }

    // Get user password from preference
    private String getUserPassword(){

        String key = getString(R.string.pref_user_password_key);
        String defaultValue = getString(R.string.pref_user_password_default);
        String userPassword = userPreferences.getString(key,defaultValue);
        return userPassword;
    }

    // Get admin password from preference
    private String getAdminPassword(){

        String key = getString(R.string.pref_admin_password_key);
        String defaultValue = getString(R.string.pref_admin_password_default);
        String adminPassword = userPreferences.getString(key,defaultValue);

        return adminPassword;
    }

    // Check if User password is enabled
    private boolean isUserPasswordEnabled(){
        String key = getString(R.string.pref_user_cb_password_key);
        return userPreferences.getBoolean(key,false);
    }

    // Check if Admin password is enabled
    private boolean isAdminPasswordEnabled(){
        String key = getString(R.string.pref_admin_cb_password_key);
        return userPreferences.getBoolean(key, false);
    }

    @Override
    public void onBackPressed() {
        // If password screen is visible then hide the password screen on back pressed
        if(View.VISIBLE == linearLayoutAskPassword.getVisibility()){
            hidePasswordScreen();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        hidePasswordScreen();
    }
}
