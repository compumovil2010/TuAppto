package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class SearchPropertyActivity extends AppCompatActivity {

    ImageButton imageButtonFilters;
    Button button;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);

        imageButtonFilters = findViewById(R.id.imageButtonFilters);
        button = findViewById(R.id.buttonTemporal);
        mAuth = FirebaseAuth.getInstance();

        imageButtonFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),FiltersActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),PropertyDescriptionActivity.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusettings, menu);
        return true;
    }

    public boolean onOptionsItemsSelected(@NonNull MenuItem item){
        int itemCliked = item.getItemId();
        if(itemCliked == R.id.menuSingOut){
            mAuth.signOut();
            Intent intent = new Intent(SearchPropertyActivity.this, PrincipalActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
