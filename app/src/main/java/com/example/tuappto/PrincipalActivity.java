package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.content.Intent;


public class PrincipalActivity extends AppCompatActivity {

    Button buttonRegister;
    Button buttonEnter;
    Button buttonTemporal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        buttonEnter = findViewById(R.id.buttonEnter);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonTemporal = findViewById(R.id.buttonTemporal);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),SellerMenuActivity.class));
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),RegistryActivity.class);
                startActivity(intent);

            }
        });

        buttonTemporal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),BuyerMenuActivity.class));
            }
        });


    }
}
