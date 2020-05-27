package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import negocio.Appointment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import com.example.tuappto.adapters.AppoinmentAdapter;
import com.google.android.gms.maps.model.LatLng;
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
        mAdapter = new AppoinmentAdapter(mAppoinment, R.layout.interested,true);
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
                        if (user.equals(Objects.requireNonNull(ds.child("client").getValue()).toString())) {
                            int day = Integer.parseInt(Objects.requireNonNull(ds.child("day").getValue()).toString());
                            int month = Integer.parseInt(Objects.requireNonNull(ds.child("month").getValue()).toString());
                            int year = Integer.parseInt(Objects.requireNonNull(ds.child("year").getValue()).toString());
                            int hour = Integer.parseInt(Objects.requireNonNull(ds.child("hour").getValue()).toString());
                            int min = Integer.parseInt(Objects.requireNonNull(ds.child("min").getValue()).toString());

                            String client = Objects.requireNonNull(ds.child("client").getValue()).toString();
                            String clientImagePath = Objects.requireNonNull(ds.child("clientImagePath").getValue()).toString();
                            String clientName = Objects.requireNonNull(ds.child("clientName").getValue()).toString();
                            String clientSecondName = Objects.requireNonNull(ds.child("clientSecondName").getValue()).toString();
                            long clientPhone = Long.parseLong(Objects.requireNonNull(ds.child("clientPhone").getValue()).toString());

                            String owner = Objects.requireNonNull(ds.child("owner").getValue()).toString();
                            String ownerImagePath = Objects.requireNonNull(ds.child("ownerImagePath").getValue()).toString();
                            String ownerName = Objects.requireNonNull(ds.child("ownerName").getValue()).toString();
                            String ownerSecondName = Objects.requireNonNull(ds.child("ownerSecondName").getValue()).toString();
                            long ownerPhone = Long.parseLong(Objects.requireNonNull(ds.child("ownerPhone").getValue()).toString());

                            double latitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("latitude").getValue()).toString());
                            double longitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("longitude").getValue()).toString());

                            LatLng location = new LatLng(latitude,longitude);

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String address = addresses.get(0).getAddressLine(0);
                            Appointment aux2 = new Appointment();

                            aux2.setOwner(owner);
                            aux2.setOwnerImagePath(ownerImagePath);
                            aux2.setOwnerName(ownerName);
                            aux2.setOwnerPhone(ownerPhone);
                            aux2.setOwnerSecondName(ownerSecondName);

                            aux2.setClient(client);
                            aux2.setClientImagePath(clientImagePath);
                            aux2.setClientName(clientName);
                            aux2.setClientPhone(clientPhone);
                            aux2.setClientSecondName(clientSecondName);
                            aux2.setDay(day);
                            aux2.setMonth(month);
                            aux2.setYear(year);
                            aux2.setHour(hour);
                            aux2.setMin(min);
                            aux2.setLocation(location);
                            aux2.setAddress(address);

                            mAppoinment.add(aux2);

                        }
                    }

                    mAdapter = new AppoinmentAdapter(mAppoinment, R.layout.interested,true);
                    mAdapter.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            Intent i = new Intent(v.getContext(), DateInformationActivity.class);
                            bundle.putInt("year", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getYear());
                            bundle.putInt("month", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getMonth());
                            bundle.putInt("day", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getDay());
                            bundle.putInt("hour", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getHour());
                            bundle.putInt("min", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getMin());
                            bundle.putString("address", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getAddress());
                            bundle.putString("imagePath", mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getOwnerImagePath());
                            bundle.putString("name",mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getOwnerName());
                            bundle.putString("secondName",mAppoinment.get(mRecyclerView.getChildAdapterPosition(v)).getOwnerSecondName());

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
