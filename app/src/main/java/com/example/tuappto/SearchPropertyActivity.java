package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.example.tuappto.adapters.PropertyAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

import negocio.Property;

public class SearchPropertyActivity extends AppCompatActivity {

    public ImageButton imageButtonFilters;
    private DatabaseReference mDatabase;
    private PropertyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Property> mProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);

        imageButtonFilters = findViewById(R.id.imageButtonFilters);
        mRecyclerView = findViewById(R.id.recyclerViewProperties);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageButtonFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),FiltersActivity.class));
            }
        });

        getPropertiesFromFirebase();

    }

    private  void getPropertiesFromFirebase(){
        mDatabase.child("properties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        int parking = Integer.parseInt(Objects.requireNonNull(ds.child("parking").getValue()).toString());
                        int area = Integer.parseInt(Objects.requireNonNull(ds.child("area").getValue()).toString());
                        int price = Integer.parseInt(Objects.requireNonNull(ds.child("price").getValue()).toString());
                        int rooms = Integer.parseInt(Objects.requireNonNull(ds.child("rooms").getValue()).toString());
                        String kind = Objects.requireNonNull(ds.child("sellOrRent").getValue()).toString();
                        String imagePath = Objects.requireNonNull(ds.child("imagePath").getValue()).toString();
                        String description = Objects.requireNonNull(ds.child("description").getValue()).toString();
                        String ownerId = Objects.requireNonNull(ds.child("ownerId").getValue()).toString();
                        //String address = ds.child("sellOrRent").getValue().toString();
                        double latitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("latitude").getValue()).toString());
                        double longitude = Double.parseDouble(Objects.requireNonNull(ds.child("location").child("longitude").getValue()).toString());
                        LatLng latLng = new LatLng(latitude, longitude);
                        Property aux = new Property();
                        aux.setSellOrRent(kind);
                        aux.setParking(parking);
                        aux.setArea(area);
                        aux.setPrice(price);
                        aux.setRooms(rooms);
                        aux.setImagePath(imagePath);
                        aux.setLocation(latLng);
                        aux.setDescription(description);
                        aux.setOwnerId(ownerId);
                        mProperties.add(aux);
                    }

                    mAdapter = new PropertyAdapter(mProperties, R.layout.publication);

                    mAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            Intent i = new Intent(v.getContext(), PropertyDescriptionActivity.class);
                            bundle.putString("imagePath", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getImagePath());
                            bundle.putInt("price", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getPrice());
                            bundle.putString("sellOrRent", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getSellOrRent());
                            bundle.putInt("rooms", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getRooms());
                            bundle.putInt("area", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getArea());
                            bundle.putInt("parking", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getParking());
                            bundle.putString("description", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getDescription());
                            bundle.putDouble("latitude", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().latitude);
                            bundle.putDouble("longitude", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getLocation().longitude);
                            bundle.putString("ownerId", mProperties.get(mRecyclerView.getChildAdapterPosition(v)).getOwnerId());
                            i.putExtra("bundle", bundle);
                            startActivity(i);
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
