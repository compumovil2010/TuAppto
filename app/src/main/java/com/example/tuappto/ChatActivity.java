package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class ChatActivity extends AppCompatActivity {

    public Button makeDateButton;
    public Bundle bundle;
    public Intent intent;

    public String clientImagePath;
    public String clientName;
    public String clientSecondName;
    public String clientEmail;
    public String clientId;
    public String property;
    public long clientPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        makeDateButton = findViewById(R.id.buttonMakeDate);


        intent = getIntent();
        bundle = intent.getBundleExtra("bundle");

        assert bundle != null;
        clientId = bundle.getString("clientId");
        clientEmail = bundle.getString("clientEmail");
        clientSecondName = bundle.getString("clientSecondName");
        clientName = bundle.getString("clientName");
        clientImagePath = bundle.getString("clientImagePath");
        clientPhone = bundle.getLong("clientPhone");


        makeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), DateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("clientId", clientId);
                bundle.putString("clientImagePath", clientImagePath );
                bundle.putString("clientName", clientName);
                bundle.putString("clientSecondName", clientSecondName);
                bundle.putString("clientEmail", clientEmail);
                bundle.putLong("clientPhone", clientPhone);
                bundle.putString("property", property);
                i.putExtra("bundle", bundle);

                startActivity(i);
            }
        });
    }
}
