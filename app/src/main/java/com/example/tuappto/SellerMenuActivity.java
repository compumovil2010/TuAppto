package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SellerMenuActivity extends AppCompatActivity {

    Button buttonPublishProperty;
    Button buttonMyPublications;
    Button buttonInterested;
    Button buttonDates;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
                startActivity(new Intent(view.getContext(), SellerDatesActivity.class));
            }
        });


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                mAuth.signOut();
                startActivity(new Intent(SellerMenuActivity.this,PrincipalActivity.class));
                finish();
                return true;

        }
        return false;
    }
}
