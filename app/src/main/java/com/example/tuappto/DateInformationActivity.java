package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DateInformationActivity extends AppCompatActivity {

    public Bundle bundle;
    public Intent intent;

    int year;
    int month;
    int day;
    int hour;
    int min;
    String address;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_information);

        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        assert bundle != null;
        year = bundle.getInt("Year");
        month = bundle.getInt("Month");
        day = bundle.getInt("Day");
        hour = bundle.getInt("Hour");
        min = bundle.getInt("Min");
        address = bundle.getString("Address");

        textView = findViewById(R.id.textViewDateDescription);

        textView.setText("Tienes una cita a las "+hour+":"+min+
                " el dia "+day+"/"+month+"/"+year+" en la direccion "+ address);

    }
}
