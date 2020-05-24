package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import negocio.Appointment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tuappto.adapters.AppoinmentAdapter;
import com.example.tuappto.adapters.PropertyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class BuyerDatesActivity extends AppCompatActivity {

    public Button button;
    private DatabaseReference mDatabase;
    private AppoinmentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Appointment> mAppoinment = new ArrayList<>();
    private ArrayList<String> mInterest = new ArrayList<>();
    private FirebaseUser fuser;
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dates);

        mRecyclerView = findViewById(R.id.recyclerViewInterest);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

                        Appointment aux = new Appointment();

                        aux.setUser(user);
                        aux.setDay(day);
                        aux.setMonth(month);
                        aux.setYear(year);
                        aux.setHour(hour);
                        aux.setMin(min);
                        aux.setLocation(location);

                        //aux.setOwner(owner);

                        mAppoinment.add(aux);

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
                        bundle.putDouble("Longitud", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().getLongitude());
                        bundle.putDouble("Latitud", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().getLatitude());

                        //Mandar la direccion en el bundle
                        i.putExtra("bundle", bundle);

                        startActivity(i);
                        finish();
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
