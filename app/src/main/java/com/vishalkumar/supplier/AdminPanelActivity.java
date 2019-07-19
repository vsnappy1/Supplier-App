package com.vishalkumar.supplier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Find button by their id's
        Button productButton = findViewById(R.id.buttonProduct);

        //set On click listener on the product Button
        productButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open editor activity
                Intent editorActivity = new Intent(AdminPanelActivity.this, ViewProductActivity.class);
                startActivity(editorActivity);
            }
        });
    }
}
