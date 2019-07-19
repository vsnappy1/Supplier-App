package com.vishalkumar.supplier;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.vishalkumar.supplier.data.SupplierContract.ProductEntry;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Find Buttons by their id's
        Button adminButton = findViewById(R.id.buttonAdmin);
        Button userButton = findViewById(R.id.buttonUser);


        // Set On Click Listener on admin and user buttons
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the view product activity a when button is clicked
                Intent adminPanel = new Intent(MainActivity.this, ViewProductActivity.class);
                startActivity(adminPanel);
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the Billing Activity when button is clicked
                Intent billingActivity = new Intent(MainActivity.this, BillingActivity.class);
                startActivity(billingActivity);
            }
        });
    }
}
