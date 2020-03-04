package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SellerMenuActivity extends AppCompatActivity {

    Button buttonPublishProperty;
    Button buttonMyPublications;
    Button buttonInterested;
    Button buttonDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);

        buttonPublishProperty = findViewById(R.id.buttonPublishProperty);
        buttonMyPublications = findViewById(R.id.buttonMyPublications);
        buttonInterested = findViewById(R.id.buttonInterested);
        buttonDates = findViewById(R.id.buttonDates);

        buttonPublishProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),PublishPropertyActivity.class));
            }
        });

        buttonMyPublications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),MyPublicationsActivity.class));
            }
        });

        buttonInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),InterestedActivity.class));
            }
        });

        buttonDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),PublishPropertyActivity.class));
            }
        });

    }
}
