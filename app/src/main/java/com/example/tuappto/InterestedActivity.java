package com.example.tuappto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.tuappto.adapters.InterestedAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import negocio.Interest;

public class InterestedActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private InterestedAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Interest> mInterested = new ArrayList<>();
    private FirebaseUser fuser;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        mRecyclerView = findViewById(R.id.recyclerViewInterested);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();

        getInterestedFromFirebase();

    }

    private  void getInterestedFromFirebase(){
        final String owner = fuser.getUid();
        mDatabase.child("interest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (owner.equals(Objects.requireNonNull(ds.child("owner").getValue()).toString())) {

                            String clientName = Objects.requireNonNull(ds.child("clientName").getValue()).toString();
                            String clientSecondName = Objects.requireNonNull(ds.child("clientSecondName").getValue()).toString();
                            String clientEmail = Objects.requireNonNull(ds.child("clientEmail").getValue()).toString();
                            String property = Objects.requireNonNull(ds.child("property").getValue()).toString();
                            String clientImagePath = Objects.requireNonNull(ds.child("clientImagePath").getValue()).toString();
                            long clientPhone = Long.parseLong(Objects.requireNonNull(ds.child("clientPhone").getValue()).toString());
                            String clientId = Objects.requireNonNull(ds.child("clientId").getValue()).toString();

                            Interest aux = new Interest();
                            aux.setClientId(clientId);
                            aux.setClientImagePath(clientImagePath);
                            aux.setClientName(clientName);
                            aux.setClientSecondName(clientSecondName);
                            aux.setClientEmail(clientEmail);
                            aux.setClientPhone(clientPhone);
                            aux.setProperty(property);

                            mInterested.add(aux);
                        }

                    }
                    mAdapter = new InterestedAdapter(mInterested, R.layout.interested);

                    mAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(v.getContext(), ChatActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putString("clientId",mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientId());
                            bundle.putString("clientImagePath", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientImagePath());
                            bundle.putString("clientName", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientName());
                            bundle.putString("clientSecondName", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientSecondName());
                            bundle.putString("clientEmail", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientEmail());
                            bundle.putLong("clientPhone", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getClientPhone());
                            bundle.putString("property", mInterested.get(mRecyclerView.getChildAdapterPosition(v)).getProperty());
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
