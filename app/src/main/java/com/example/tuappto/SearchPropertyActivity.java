package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class SearchPropertyActivity extends AppCompatActivity {

    ImageButton imageButtonFilters;
    Button button;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);

        imageButtonFilters = findViewById(R.id.imageButtonFilters);
        button = findViewById(R.id.buttonTemporal);
        listView = findViewById(R.id.listView);

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
}
