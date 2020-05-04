package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class SellerMenuActivity extends AppCompatActivity {

    public Button buttonPublishProperty;
    public Button buttonMyPublications;
    public Button buttonInterested;
    public Button buttonDates;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);

        mAuth = FirebaseAuth.getInstance();
        buttonPublishProperty = findViewById(R.id.buttonPublishProperty);
        buttonMyPublications = findViewById(R.id.buttonMyPublications);
        buttonInterested = findViewById(R.id.buttonInterested);
        buttonDates = findViewById(R.id.buttonDates);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        if (item.getItemId() == R.id.logOut) {
            mAuth.signOut();
            startActivity(new Intent(SellerMenuActivity.this, PrincipalActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
