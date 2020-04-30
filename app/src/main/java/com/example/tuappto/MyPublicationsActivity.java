package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.example.tuappto.adapters.PropertyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import negocio.Property;

public class MyPublicationsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private PropertyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Property> mProperties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publications);

        mRecyclerView = findViewById(R.id.recyclerViewProperties);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getPropertiesFromFirebase();

    }

    private  void getPropertiesFromFirebase(){
        mDatabase.child("properties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        int parking = Integer.parseInt(ds.child("parking").getValue().toString());
                        int area = Integer.parseInt(ds.child("area").getValue().toString());
                        int price = Integer.parseInt(ds.child("price").getValue().toString());
                        int rooms = Integer.parseInt(ds.child("rooms").getValue().toString());
                        String kind = ds.child("sellOrRent").getValue().toString();
                        //String address = ds.child("sellOrRent").getValue().toString();
                        Property aux = new Property();
                        aux.setSellOrRent(kind);
                        aux.setParking(parking);
                        aux.setArea(area);
                        aux.setPrice(price);
                        aux.setRooms(rooms);
                        mProperties.add(aux);
                    }
                    mAdapter = new PropertyAdapter(mProperties, R.layout.publication);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
