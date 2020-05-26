package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import negocio.Appointment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.example.tuappto.adapters.AppoinmentAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BuyerDatesActivity extends AppCompatActivity {

    public Button button;
    private DatabaseReference mDatabase;
    private AppoinmentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Appointment> mAppoinment = new ArrayList<>();
    private FirebaseUser fuser;
    public FirebaseAuth mAuth;
    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dates);

        geocoder = new Geocoder(this, Locale.getDefault());

        mRecyclerView = findViewById(R.id.recyclerViewDatesB);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new AppoinmentAdapter(mAppoinment, R.layout.publication);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();

        getAppoinmentsFromFirebase();
        // TEMPORAL A DateInformationActivity
    }

    private void getAppoinmentsFromFirebase() {
        final String user = fuser.getUid();
        mDatabase.child("appoinments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (user.equals(Objects.requireNonNull(ds.child("user").getValue()).toString())) {
                        int day = Integer.parseInt(Objects.requireNonNull(ds.child("day").getValue()).toString());
                        int month = Integer.parseInt(Objects.requireNonNull(ds.child("month").getValue()).toString());
                        int year = Integer.parseInt(Objects.requireNonNull(ds.child("year").getValue()).toString());
                        int hour = Integer.parseInt(Objects.requireNonNull(ds.child("hour").getValue()).toString());
                        int min = Integer.parseInt(Objects.requireNonNull(ds.child("min").getValue()).toString());

                        double latitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("latitude").getValue()).toString());
                        double longitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("longitude").getValue()).toString());

                        Location location = new Location("");
                        location.setLongitude(longitude);
                        location.setLatitude(latitude);

                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address = addresses.get(0).getAddressLine(0);
                        Appointment aux2 = new Appointment();

                        aux2.setUser(user);
                        aux2.setDay(day);
                        aux2.setMonth(month);
                        aux2.setYear(year);
                        aux2.setHour(hour);
                        aux2.setMin(min);
                        aux2.setLocation(location);
                        aux2.setAddress(address);

                        //aux.setOwner(owner);

                        mAppoinment.add(aux2);

                    }
                }
                mAdapter = new AppoinmentAdapter(mAppoinment, R.layout.publication);
                mAdapter.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Intent i = new Intent(v.getContext(), DateInformationActivity.class);
                        bundle.putInt("Year", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getYear());
                        bundle.putInt("Month", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getMonth());
                        bundle.putInt("Day", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getDay());
                        bundle.putInt("Hour", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getHour());
                        bundle.putInt("Min", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getMin());
                        bundle.putString("Address", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getAddress());
                        //bundle.putDouble("Longitud", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().getLongitude());
                        //bundle.putDouble("Latitud", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().getLatitude());

                        //Mandar la direccion en el bundle
                        i.putExtra("bundle", bundle);

                        startActivity(i);
                        finish();
                    }
                });

                mRecyclerView.setAdapter(mAdapter);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
